package Model;
import java.io.DataInputStream;
import java.io.IOException;


/**
 * Luokka <code>Aja</code> välittää kontrollerilta saadut käyttäjän ohjaus käskyt <code>Tasapainottaja</code> -luokkaan.
 * Muuttaa ohjauksen voimaa/nopeutta.
 * Käynnistää tai sammuttaa luokan <code>Autopilot</code>.
 * @author Tommi, Kimmo, Jukka.
 *
 */
public class Aja extends Thread {

	/**
	 * Muuttuja <code>nopeus</code> ilmaisee ohjauksen voimaa/nopeutta joka välitetään käyttäjän ohjauksen mukaan <code>Tasapainottaja</code> -luokkaan. 
	 */
	private int nopeus = 50;
	/**
	 * Muuttuja <code>suunta</code> ilmaisee käyttäjän käskyä joka saadaan kontrollerilta, jonka mukaan robottia ohjataan.
	 */
	private int suunta;
	private Tasapainottaja robotti;
	private DataInputStream in;
	private Autopilot auto;

	/**
	 * Luo <code>Aja</code> -olion. Saa parametreinä Tasapainottaja, DataInputStream ja Autopilot -oliot.
	 * @param robotti <code>Tasapainottaja</code> -olio.
	 * @param in <code>DataInputStream</code> -olio.
	 * @param auto <code>Autopilot</code> -olio
	 */
	public Aja(Tasapainottaja robotti, DataInputStream in, Autopilot auto) {
		this.robotti = robotti;
		this.in = in;
		this.auto = auto;
	}

	@Override
	public void run() {
		while (true) {
			try {
				suunta = in.readInt();
			} catch (IOException e) {
			}
			if (suunta == 1) { // taakse
				robotti.ohjaus(nopeus, nopeus);
				Thread.yield();

			} else if (suunta == 3) { // eteen
				robotti.ohjaus(-(nopeus), -(nopeus));
				Thread.yield();

			} else if (suunta == 2) { // oikea
				robotti.ohjaus((int)(-nopeus / 1.5) , (int)(nopeus / 1.5));
				Thread.yield();

			} else if (suunta == 4) { // vasen
				robotti.ohjaus((int)(nopeus / 1.5) ,(int)( -nopeus / 1.5));
				Thread.yield();

			} else if (suunta == 0) { // seis
				robotti.ohjaus(0, 0);
				Thread.yield();

			} else if (suunta == 5) { // nopeuden muutos
				try {
					nopeus = ((int)(in.readInt() / 2.5));
					if (nopeus < 10)
						nopeus = 10;
				} catch (IOException e) {
				}
				Thread.yield();

			} else if (suunta == 7) { // autopilot päälle
				auto.käynnistä();
				Thread.yield();
			} else if (suunta == 6) { // autopilot pois
				auto.setStart();
				Thread.yield();
			}
			else if (suunta == -1) {
				System.exit(1);
			}
		}
	}
}
