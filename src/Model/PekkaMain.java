package Model;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.robotics.Gyroscope;
import lejos.robotics.GyroscopeAdapter;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.HiTechnicGyro;
import lejos.hardware.sensor.SensorModes;


/**
 * Luokka <code>PekkaMain</code> Sisältää robotin mainin ja tarvittavien uokkien luonnit.
 * 
 * @author Tommi, Kimmo, Jukka
 *
 */

public class PekkaMain {

	private static Port port;
	private static SensorModes sensor;
	private static SampleProvider sample;
	private static Gyroscope gyro;
	private static UnregulatedMotor M1 = new UnregulatedMotor(MotorPort.D);
	private static UnregulatedMotor M2 = new UnregulatedMotor(MotorPort.A);
	private static Tasapainottaja tasap;
	private static Aja aja;
	private static Autopilot auto;
	private static DataInputStream in;

	/**
	 * Liukuluku <code>freq</code> ilmaisee tajuutta, jolla gyroskooppi saa uusia kulmanopeuksia. 1/s
	 */
	private static float freq = 300;

	public static void main(String[] args) {
		port = LocalEV3.get().getPort("S4");
		sensor = new HiTechnicGyro(port);
		sample = ((HiTechnicGyro) sensor).getRateMode();
		gyro = new GyroscopeAdapter(sample, freq);
		tasap = new Tasapainottaja(M1,M2,gyro);
		auto = new Autopilot(tasap);
		auto.setPriority(1);

		tasap.setPriority(10);
		auto.start();
		try {
			ServerSocket serv = new ServerSocket(1111);
			Socket s = serv.accept();
			in = new DataInputStream(s.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		aja = new Aja(tasap, in, auto);
		aja.setPriority(1);
		aja.start();
		tasap.start(); // aloittaa tasapainottelun
	}
}
