package Model;

import lejos.robotics.EncoderMotor;
import lejos.robotics.Gyroscope;

import lejos.hardware.Sound;

/**
 * Luokka <code>Tasapainottaja</code> sisältää kaikki tarvittavat metodit
 * robotin tasapainottamiseen. Koodi perustuu:
 * <a href="http://www.hitechnic.com/blog/gyro-sensor/htway/">http://www.hitechnic.com/blog/gyro-sensor/htway/</a> ja <a href=
 * "https://lejos.sourceforge.io/nxt/nxj/api/lejos/robotics/navigation/Segoway.html">https://lejos.sourceforge.io/nxt/nxj/api/lejos/robotics/navigation/Segoway.html</a>
 * 
 * @author Tommi,Jukka,Kimmo
 *
 */
public class Tasapainottaja extends Thread {

	// Moottorit ja gyroskooppi
	private Gyroscope gyro;
	protected EncoderMotor vM; // vasen
	protected EncoderMotor oM; // oikea

	/**
	 * Kokonaisluku <code>WAIT_TIME</code>, jolla ilmaistaan Thread.sleep() aika
	 * millisekunteina.
	 */
	private static final int WAIT_TIME = 7;

	// TAIKA-ARVOT

	/**
	 * Liukuluku <code>PGYROKULMA</code> ilmaisee lasketun kallistuskulman
	 * painoarvon, kun lasketaan moottoreille annettavaa voimaa.
	 */
	private static final double PGYROKULMA = 13;
	/**
	 * Liukuluku <code>PGYRONOPEUS</code> ilmaisee gyroskoopista saadun
	 * kulmanopeuden painoarvoa, kun lasketaan moottoreille annettavaa voimaa.
	 */
	private static final double PGYRONOPEUS = 0.5;
	/**
	 * Liukuluku <code>PMOOTTORIPOS</code> ilmaisee moottoreiden position
	 * painoarvoa, kun lasketaan moottoreille annettavaa voimaa.
	 */
	private static final double PMOOTTORIPOS = 0.1;
	/**
	 * Liukuluku <code>PNOPEUS</code> ilmaisee moottoreiden neljän edellisen
	 * nopeuden keskiarvon painoarvoa, kun lasketaan moottoreille annettavaa voimaa.
	 */
	private static final double PNOPEUS = 0.08;
	/**
	 * Liukuluku <code>PAJO</code> ilmaisee käyttäjän liikkumisohjauksen painoarvoa,
	 * kun lasketaan moottoreille annettavaa voimaa.
	 */
	private static final double PAJO = -0.02;
	/**
	 * Liukuluku <code>POHJAUS</code> ilmaisee käyttäjän kääntymisohjauksen
	 * painoarvoa, kun lasketaan moottoreille annettavaa voimaa.
	 */
	private static final double POHJAUS = 0.25;

	/**
	 * Liukuluku <code>EMAOFFSET</code> ilmaisee painoarvoa, jolla jokaisella
	 * säikeen toisto kerralla saatu uusi gyroskoopin lukema huomioidaan gyroskoopin
	 * virhearviossa (goffset).
	 */
	private static final double EMAOFFSET = 0.0005;

	// Gyroskooppi muuttujat
	private double gOffset;
	private double gAngleGlobal = 0;
	private double gyroSpeed, gyroAngle;

	/**
	 * Liukuluku <code>moottoriAjoControl</code> ilmaisee oikean ja vasemman
	 * moottorin nopeuksien haettua summaa (astetta/sekunti).
	 */
	private double moottoriAjoControl = 0.0;
	/**
	 * Liukuluku <code>moottoriOhjausControl</code> ilmaisee moottoreiden nopeuksien
	 * haettua erotusta (astetta/sekunti)
	 */
	private double moottoriOhjausControl = 0.0;

	/**
	 * Liukuluku <code>haettuPosEro</code> ilmaisee moottoreiden positioiden haettua
	 * erotusta (<code>moottoriOhjausControl</code> * <code>loopAika</code>).
	 */
	private double haettuPosEro = 0.0;

	/**
	 * Liukuluku <code>aikaLaskuriStart</code> ilmaisee hetkeä, kun robotti alkaa
	 * laskea aikaa, käytetään <code>loopAika</code> laskemisessa.
	 */
	private long aikaLaskuriStart;
	/**
	 * Liukuluku <code>loopAika</code> ilmaisee jokaisen yksittäisen säikeen toisto
	 * kerran kestoa sekunteina.
	 */
	private double loopAika;

	// moottorimuuttujat
	private double motorPos = 0;
	private long posSumma = 0, posSummavanha;
	private long posEro;
	private long posDelta3 = 0;
	private long posDelta2 = 0;
	private long posDelta1 = 0;
	private double moottoriNopeus;

	/**
	 * Luo <code>Tasapainottaja</code>-olion. Saa parametrinä 2
	 * <code>EncoderMotor</code>-oliota sekä <code>Gyroscope</code>-olion.
	 * 
	 * @param vasen <code>EncoderMotor</code>-olio.
	 * @param oikea <code>EncoderMotor</code>-olio.
	 * @param gyro  <code>Gyroscope</code>-olio.
	 */
	public Tasapainottaja(EncoderMotor vasen, EncoderMotor oikea, Gyroscope gyro) {
		this.vM = vasen;
		this.oM = oikea;
		this.gyro = gyro;
		gyroKalibrointi();

	}

	/**
	 * Kalibroi gyroskoopin sekä ilmoittaa käyttäjälle kun robotin voi käynnistää
	 * GUI:sta.
	 */
	private void gyroKalibrointi() {
		gyro.recalibrateOffset();

		Sound.beep();
		for (int c = 5; c > 0; c--) {
			System.out.print(c + " ");

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		Sound.beep();
	}

	/**
	 * Hakee uuden kulmanopeuden (astetta/s). Laskee uuden virhearvion. Ottaa
	 * virhearvion huomioon kulmanopeudessa. Laskee kulman.
	 */
	private void päivitäGyro() {
		float gyroRaw;
		gyroRaw = gyro.getAngularVelocity(); // hakee kulmanopeuden astetta / s
		gOffset = EMAOFFSET * gyroRaw + (1 - EMAOFFSET) * gOffset; // laskee offsetin
		gyroSpeed = gyroRaw - gOffset; // kulmanopeus - offset
		gAngleGlobal += gyroSpeed * loopAika; // kulma
		gyroAngle = gAngleGlobal;

	}

	/**
	 * Pitää kirjaa moottoreiden positioista.
	 */
	private void päivitäMoottori() {
		long posVasen, posOikea, posDelta;

		// pitää kirjaa moottoreiden positiosta
		posVasen = vM.getTachoCount();
		posOikea = oM.getTachoCount();

		// pitää kirjaa vanhasta moottoreiden positiosta
		// uusi posSumma ja posEro
		posSummavanha = posSumma;
		posSumma = posVasen + posOikea;
		posEro = posVasen - posOikea;

		// posDelta päivitys
		posDelta = posSumma - posSummavanha;
		motorPos += posDelta;

		// neljän edellisen positio muutoksen keskiarvo
		moottoriNopeus = (posDelta + posDelta1 + posDelta2 + posDelta3) / (4 * loopAika);

		// oottaa talteen 4 positio muutosta
		posDelta3 = posDelta2;
		posDelta2 = posDelta1;
		posDelta1 = posDelta;
	}

	private int powerVasen, powerOikea;

	/**
	 * Määrittää oikean ja vasemman moottorin voiman tasapainoon tarvittavasta
	 * voimasta sekä mahdollisesta käyttäjän ohjauksesta
	 * 
	 * @param power Muuttuja <code>power</code> on laskettu voima joka tarvitaan
	 *              robotin tasapainon ylläpitämiseksi.
	 */
	private void ohjausControl(int power) {
		int ohjausPower;

		haettuPosEro += moottoriOhjausControl * loopAika;

		ohjausPower = (int) (POHJAUS * (haettuPosEro - posEro));

		powerVasen = power + ohjausPower;
		powerOikea = power - ohjausPower;

		// rajoittaa powerit 100 ja -100 välille
		if (powerVasen > 100)
			powerVasen = 100;
		if (powerVasen < -100)
			powerVasen = -100;
		if (powerOikea > 100)
			powerOikea = 100;
		if (powerOikea < -100)
			powerOikea = -100;
	}

	/**
	 * Laskee säikeen jokaisen yksittäiseen suoritus kertaan kuluneen ajan.
	 * 
	 * @param loopLKM kertoo monta kertaa säie on suoritettu.
	 */
	private void laskeAika(long loopLKM) {
		if (loopLKM == 0) { // ensimmäinen kerta
			loopAika = 0.0055;
			aikaLaskuriStart = System.currentTimeMillis();
		} else {
			loopAika = (System.currentTimeMillis() - aikaLaskuriStart) / (loopLKM * 1000.0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {

		int power;
		long cLoop = 0;

		System.out.println("Balancing");
		System.out.println();

		// Resettaa moottori positiot
		vM.resetTachoCount();
		oM.resetTachoCount();

		while (true) {
			laskeAika(cLoop++);

			päivitäGyro();

			päivitäMoottori();

			motorPos -= moottoriAjoControl * loopAika;

			power = (int) ((PGYRONOPEUS * gyroSpeed + PGYROKULMA * gyroAngle) + PMOOTTORIPOS * motorPos
					+ PAJO * moottoriAjoControl + PNOPEUS * moottoriNopeus);

			ohjausControl(power);

			vM.setPower(Math.abs(powerVasen));
			oM.setPower(Math.abs(powerOikea));

			if (powerVasen > 0) {
				vM.forward();
			} else {
				vM.backward();
			}

			if (powerOikea > 0) {
				oM.forward();
			} else {
				oM.backward();
			}

			try {
				Thread.sleep(WAIT_TIME);
			} catch (InterruptedException e) {
			}
		}

	}

	/**
	 * Saa robotin liikkumaan eteen/taakse tai kääntymään käyttäjän ohjauksen
	 * mukaan. Saa parametreinä kaksi kokonaislukua jotka ilmaisevat ohjausvoimia
	 * moottoreille.
	 * 
	 * @param vasen Ilmaisee vasemmalle moottorille annettavaa ohjausvoimaa.
	 * @param oikea Ilmaisee oikealle moottorille annettavaa ohjausvoimaa.
	 */
	public void ohjaus(int vasen, int oikea) {
		moottoriAjoControl = (vasen + oikea) * 3;
		moottoriOhjausControl = (vasen - oikea) * 3;
	}
}