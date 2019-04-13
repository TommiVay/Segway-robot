package Controller;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Controller_Interface luokka on rajapinta Controllerille
 * @author Jukka Holopainen, Kimmo Karjalainen, Tommi Väyrynen
 * @version 1.0
 */
public interface Controller_Interface {
	/**
	 * Hoitaa sammutus operaatioista modelin ja kontrollerin välillä.
	 */
	public void Sammu();
	
	/**
	 * Lähettää eteenpäin käskyn modelille ja ilmoitaa onnistuksesta view:iin.
	 */
	public void Eteen();
	
	/**
	 * Lähettää oikealle käskyn modelille ja ilmoitaa onnistuksesta view:iin.
	 */
	public void Oikealle();
	
	/**
	 * Lähettää vasemalle käskyn modelille ja ilmoitaa onnistuksesta view:iin.
	 */
	public void Vasemalle();
	
	/**
	 * Lähettää taakse käskyn modelille ja ilmoitaa onnistuksesta view:iin.
	 */
	public void Taakse();
	
	/**
	 * Yhdistää kontrollerin ja modelin.
	 * @throws UnknownHostException
	 * Heitää poikeuksen jos Host on tuntematon.
	 * @throws IOException
	 * Heitää poikeuksen jos tulee virhe.
	 */
	public void Yhdistys() throws UnknownHostException, IOException;
	
	/**
	 * Katkaisee yhteyden modelin ja kontrollerin välillä.
	 */
	public void CloseCon();
	
	/**
	 * Huolehtii laiteen käynistyksestä.
	 */
	public void Paalle();
	
	/**
	 * Antaa modelille pysähdys käskyn ja ilmoitaa siitä view:iin.
	 */
	public void Pysahdy();
	
	/**
	 * Antaa modelille <code>maara</code>n nopeudeksi. 
	 * @param maara
	 * kuinka suuren nopeuden model saa.
	 */
	public void Nopeus(int maara);
	
	/**
	 * Kun suoritetaan mahdollistaa tietojen lähetämisen modeliin ja ilmoitaa tästä modelille.
	 */
	public void ManPaalle();
	
	/**
	 * Kun suoritetaan, estää tietojen lähetämisen modeliin ja ilmoitaa tästä modelille.
	 */
	public void ManPois();
}
