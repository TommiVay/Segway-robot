package Model;

/**
 * Luokka <code>Autopilot</code> saa robotin tarvittaessa liikkumaan itsenäisesti ns. "koreografian" mukaan.
 * @author Tommi, Jukka, Kimmo
 *
 */
public class Autopilot extends Thread {
	private Tasapainottaja robotti;
	
	/**
	 * Muuttuja <code>start</code> hallitsee meneekö säie odotus tilaan vai jatketaanko sen suoritusta.
	 * Arvo true saa säikeen odotustilaan.
	 */
	private boolean start = true;

	/**
	 * Luo Autopilot -olion. Saa parametrinä Tasapainottaja -olion.
	 * @param robotti <code>Tasapainottaja</code> -olio
	 */
	public Autopilot(Tasapainottaja robotti) {
		this.robotti = robotti;
	}

	public void run() {
		while(true) {
			try {
				odota();
				robotti.ohjaus(50, 50);
				Thread.sleep(5000);
				odota();
				robotti.ohjaus(0, 0);
				Thread.sleep(500);
				odota();
				robotti.ohjaus(-50, -50);
				Thread.sleep(5000);
				odota();
				robotti.ohjaus(0, -0);
				Thread.sleep(500);
				robotti.ohjaus(50, -50);
				Thread.sleep(5000);
				robotti.ohjaus(0, -0);
				Thread.sleep(500);
				robotti.ohjaus(-50, 50);
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
	}

	
	/**
	 * Tarkastaa <code>start</code> -muuttujan tilan. Jos true, säie menee odotus tilaan.
	 */
	public synchronized void odota() {
		if (start) {
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	
	
	/**
	 * Muuttaa <code>start</code> -muuttujan arvon falseksi ja herättää kaikki synkronoidut säikeet.
	 */
	public synchronized void käynnistä() {
		this.start = false;
		notifyAll();
	}
	
	/**
	 *  Muuttaa <code>start</code> -muuttujan arvon trueksi.
	 */
	public void setStart() {
		this.start = true;
	}
	
	/**
	 * Palauttaa start -olion.
	 * @return <code>start</code> -olio
	 */
	public boolean getStart() {
		return start;
	}
}
