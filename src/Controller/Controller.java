package Controller;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import View.View_Interface;

/**
 * Välittää tietoja View_Interfacin ja EV3:n välillä.
 * @author jukka, Kimmo Karjalainen, Tommi Väyrynen
 * @version 1.0
 */
public class Controller implements Controller_Interface {
	
	/**
	 * Vastaanottaa ja lähettää tietoja luokan kanssa, joka toteutaa View_Interfacin.
	 */
	private View_Interface view;
	
	private Socket s;
	
	private static DataOutputStream out;
	
	/**
	 * Kokonaisluku <code>arvo</code> ilmaisee robottiin lähetetettävää arvoa.
	 */
	private int arvo;
	
	/**
	 * Muuttuja <code>virhe</code> Ilmoittaa tapahtuiko käynnistyksessä virhettä.
	 */
	private int virhe = 0;
	
	/**
	 * Muuttuja <code>kerta</code> kertoo onko annettu viesti jo lähetetty EV3:een.
	 */
	private boolean kerta = true;
	
	/**
	 * Muuttuja <code>manuaali</code> ilmoittaa onko manuaali ohjaus päällä
	 */
	private boolean manuaali = true;
	
	/**
	 * @param view
	 * GUI johon lähetetään ilmoituksia.
	 */
	public Controller(View_Interface view) {
		this.view = view;
	}

	/* (non-Javadoc)
	 * @see Controller.Controller_Interface#Sammu()
	 */
	@Override
	public void Sammu() {
		manuaali = true;
		arvo = -1;
		kirjoitus(arvo);
		view.setSammuViesti("Pekka sammutettiin");
		CloseCon();	
	}
	
	/* (non-Javadoc)
	 * @see Controller.Controller_Interface#Pysahdy()
	 */
	@Override
	public void Pysahdy() {
		
		arvo = 0;
		kerta = true;
		
		kirjoitus(arvo);
		view.setSammuViesti("Pekka lopetti liikeen");
	}
	
	/* (non-Javadoc)
	 * @see Controller.Controller_Interface#Eteen()
	 */
	@Override
	public void Eteen() {
		arvo = 3;
		if (kerta == true) {
			kirjoitus(arvo);
			kerta = false;
		}
		view.setSammuViesti("Pekka menee eteenpäin");	
	}

	/* (non-Javadoc)
	 * @see Controller.Controller_Interface#Oikealle()
	 */
	@Override
	public void Oikealle() {
		arvo = 2;
		if (kerta == true) {
			kirjoitus(arvo);
			kerta = false;
		}
		view.setSammuViesti("Pekka menee oikealle");	
	}

	/* (non-Javadoc)
	 * @see Controller.Controller_Interface#Vasemalle()
	 */
	@Override
	public void Vasemalle() {
		arvo = 4;
		if (kerta == true) {
			kirjoitus(arvo);
			kerta = false;
		}
		view.setSammuViesti("Pekka menee vasemalle");
	}

	/* (non-Javadoc)
	 * @see Controller.Controller_Interface#Taakse()
	 */
	@Override
	public void Taakse() {
		arvo = 1;
		if (kerta == true) {
			kirjoitus(arvo);
			kerta = false;
		}
		view.setSammuViesti("Pekka menee taaksepäin");
	}

	/* (non-Javadoc)
	 * @see Controller.Controller_Interface#Yhdistys()
	 */
	@Override
	public void Yhdistys() throws UnknownHostException, IOException {
		view.setSammuViesti("Pekkaan yhdistetään");
		s = new Socket("10.0.1.1", 1111);
		out = new DataOutputStream(s.getOutputStream());
	}

	/* (non-Javadoc)
	 * @see Controller.Controller_Interface#CloseCon()
	 */
	@Override
	public void CloseCon() {
		view.setSammuViesti("Pekkasta poistetaan yhteys");
		try {
			s.close();
		} catch (IOException e) {
	
		}
	}

	/* (non-Javadoc)
	 * @see Controller.Controller_Interface#Paalle()
	 */
	@Override
	public void Paalle() {
		try {
			Yhdistys();
			view.setSammuViesti("Pekka laitetaan päälle");
			virhe = 0;
		} catch (UnknownHostException e) {
			virhe = 1;
			view.setSammuViesti("Pekkaa ei löytynyt");
			
			
		} catch (IOException e) {
			virhe = 1;
			view.setSammuViesti("Pekkaa ei löytynyt");
			
			
		}
	}

	/**
	 * 
	 * @return
	 * palautaa <code>virhe</code>:en.
	 */
	public int getVirhe() {
		return virhe;
	}

	/**
	 * Vie arvon EV3:seen
	 * @param arvo
	 * Arvo joka viedään EV3:en.
	 */
	public void kirjoitus(int arvo) {
		if (manuaali==true){
			try {
				out.writeInt(arvo);
				out.flush();
			} catch (IOException e) {
				view.setSammuViesti("Käskyn lähettäminen epäonnistui");
			}
		}
	}

	/* (non-Javadoc)
	 * @see Controller.Controller_Interface#Nopeus(int)
	 */
	@Override
	public void Nopeus(int maara) {
		
		kirjoitus(5);
		kirjoitus(maara);
		view.setSammuViesti("nopeus: " + maara);
	}

	/* (non-Javadoc)
	 * @see Controller.Controller_Interface#ManPaalle()
	 */
	@Override
	public void ManPaalle() {
		manuaali=true;
		kirjoitus(6);
		
		view.setSammuViesti("Pekkaa tottelee käskyjä");
		
	}

	/* (non-Javadoc)
	 * @see Controller.Controller_Interface#ManPois()
	 */
	@Override
	public void ManPois() {
		
		kirjoitus(7);
		manuaali = false;
		view.setSammuViesti("Pekka liikuu itsenäisesti");
	}
}
