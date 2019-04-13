
package View;
	
import java.io.File;
import Controller.Controller;
import Controller.Controller_Interface;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Toteutaa GUI:n projektille.
 * @author Jukka Holopainen, Kimmo Karjalainen, Tommi Väyrynen
 * @version 1.0
 */
public class Main extends Application implements View_Interface {
	
	/**
	 * Main vie käskyjä controlleriin.
	 */
	private Controller_Interface controller;
	
	/**
	 * Teksti joka ilmoitaa käytäjälle mitä tapahtuu.
	 */
	private Text sammuViesti;
	
	/**
	 * Seuraa onko manuaalivaihde päällä vai ei.
	 */
	private int kierros=1;
	
	/**
	 * Luo GUI:n taustan.
	 */
	private Canvas layer;
	
	/**
	 * Täytää <code>layer</code> taustavärillä.
	 */
	private GraphicsContext gc;
	
	/**
	 * Asettaa fontin napeille.
	 */
	private Font font = new Font(15);
	
	/**
	 * Seuraa onko yhteys EV3:een luotu.
	 */
	private boolean paalla = false; 
	
	/**
	 * Seuraa onko <code>MediaPlayer</code> päällä.
	 */
	private boolean mOn = false;
	
	/**
	 * Muutaa musiikki tiedoston Media-olioksi.
	 */
	private Media sound = new Media(new File("sonic.mp3").toURI().toString());
	
	/**
	 * Soittaa musiikkia käyttäjän halutessa.
	 */
	private final MediaPlayer mediaPlayer = new MediaPlayer(sound);
	
	/**
	 * GUI:ssa nappi, jonka avulla laitetaan musiikki päälle ja pois.
	 */
	private Button sonic;
	
	/* (non-Javadoc)
	 * @see View.View_Interface#setSammuViesti(java.lang.String)
	 */
	public void setSammuViesti(String sammuViesti) {
		this.sammuViesti.setText(sammuViesti);
	}

	/* (non-Javadoc)
	 * @see javafx.application.Application#init()
	 */
	@Override
	public void init() {
		controller = new Controller(this);
	}
	
	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) {
		try {	
			primaryStage.setTitle("Ultimate Pekka");
			sammuViesti = new Text("");
			
			Scene scene = new Scene(createGrid(),310,150);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Luo <code>GridPane</code>-olion, joka on GUI:n pohja.
	 * @return
	 * Palautaa tästä syntyneen <code>GridPane</code>:in.
	 */
	private GridPane createGrid() {
		GridPane grid = new GridPane();
		
		grid.addEventHandler(KeyEvent.KEY_PRESSED, ev ->{
			if (ev.getCode() == KeyCode.W) {
				controller.Eteen();
				ev.consume();
			}else if(ev.getCode() == KeyCode.S) {
				controller.Taakse();
				ev.consume();
			}else if(ev.getCode() == KeyCode.D) {
				controller.Oikealle();
				ev.consume();
			}else if(ev.getCode() == KeyCode.A) {
				controller.Vasemalle();
				ev.consume();
			}
		});
		
		grid.addEventHandler(KeyEvent.KEY_RELEASED, ev ->{
			if (ev.getCode() == KeyCode.W) {
				controller.Pysahdy();
				ev.consume();
			}else if(ev.getCode() == KeyCode.S) {
				controller.Pysahdy();
				ev.consume();
			}else if(ev.getCode() == KeyCode.D) {
				controller.Pysahdy();
				ev.consume();
			}else if(ev.getCode() == KeyCode.A) {
				controller.Pysahdy();
				ev.consume();
			}
		});
		
		sonic = new Button("GottaGoFast");
		sonic.setMinSize(60, 25);
		sonic.setMaxSize(60, 25);
		
		sonic.setOnAction(new EventHandler<ActionEvent>(){
			/* (non-Javadoc)
			 * @see javafx.event.EventHandler#handle(javafx.event.Event)
			 */
			@Override
			public void handle(ActionEvent event) {
				Musiikia();
			}
		});

		Button sammu = new Button("Pekka paalle.");
		sammu.setMinSize(100, 25);
		sammu.setMaxSize(100, 25);
		
		sammu.setOnAction(new EventHandler<ActionEvent>(){
			/* (non-Javadoc)
			 * @see javafx.event.EventHandler#handle(javafx.event.Event)
			 */
			@Override
			public void handle(ActionEvent event) {
				if (!paalla ) {
					controller.Paalle();
					if (((Controller) controller).getVirhe()==0) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						sammu.setText("Sammutta Pekka.");
						paalla = true;
						}
				}else {
					controller.Sammu();
					sammu.setText("Pekka päälle.");
					paalla = false;
				}
			}
		});
			
		Button eteen = new Button("w");
		eteen.setMinSize(35, 35);
		eteen.setMaxSize(35, 35);
		eteen.setFont(font);
		
		eteen.setOnMousePressed(new EventHandler<MouseEvent>(){
				/* (non-Javadoc)
				 * @see javafx.event.EventHandler#handle(javafx.event.Event)
				 */
				@Override
				public void handle(MouseEvent event) {
					controller.Eteen();	
			}
		});
		
		eteen.setOnMouseReleased(new EventHandler<MouseEvent>(){
			/* (non-Javadoc)
			 * @see javafx.event.EventHandler#handle(javafx.event.Event)
			 */
			@Override
			public void handle(MouseEvent event) {
				controller.Pysahdy();
			}
		});
		
		Button taakse = new Button("s");
		taakse.setMinSize(35, 35);
		taakse.setMaxSize(35, 35);
		taakse.setFont(font);
		
		taakse.setOnMousePressed(new EventHandler<MouseEvent>(){
			/* (non-Javadoc)
			 * @see javafx.event.EventHandler#handle(javafx.event.Event)
			 */
			@Override
			public void handle(MouseEvent event) {
				controller.Taakse();
			}
		});
		
		taakse.setOnMouseReleased(new EventHandler<MouseEvent>(){
			/* (non-Javadoc)
			 * @see javafx.event.EventHandler#handle(javafx.event.Event)
			 */
			@Override
			public void handle(MouseEvent event) {
				controller.Pysahdy();
			}
		});
		
		Button vasemalle = new Button("a");
		vasemalle.setMinSize(35, 35);
		vasemalle.setMaxSize(35, 35);
		vasemalle.setFont(font);
		
		vasemalle.setOnMousePressed(new EventHandler<MouseEvent>(){
			/* (non-Javadoc)
			 * @see javafx.event.EventHandler#handle(javafx.event.Event)
			 */
			@Override
			public void handle(MouseEvent event) {
					controller.Vasemalle();
				}
		});
		
		vasemalle.setOnMouseReleased(new EventHandler<MouseEvent>(){
			/* (non-Javadoc)
			 * @see javafx.event.EventHandler#handle(javafx.event.Event)
			 */
			@Override
			public void handle(MouseEvent event) {
				controller.Pysahdy();
			}
		});
		
		Button oikealle = new Button("d");
		oikealle.setMinSize(35, 35);
		oikealle.setMaxSize(35, 35);
		oikealle.setFont(font);
		
		oikealle.setOnMousePressed(new EventHandler<MouseEvent>(){
			/* (non-Javadoc)
			 * @see javafx.event.EventHandler#handle(javafx.event.Event)
			 */
			@Override
			public void handle(MouseEvent event) {
				controller.Oikealle();				
			}
		});
		
		oikealle.setOnMouseReleased(new EventHandler<MouseEvent>(){
			/* (non-Javadoc)
			 * @see javafx.event.EventHandler#handle(javafx.event.Event)
			 */
			@Override
			public void handle(MouseEvent event) {
				controller.Pysahdy();
				
				
			}
		});
		
		Button onoff = new Button("Autonominen ohjaus");
		onoff.setMinSize(130, 25);
		onoff.setMaxSize(130, 25);
		
		onoff.setOnAction(new EventHandler<ActionEvent>(){
			/* (non-Javadoc)
			 * @see javafx.event.EventHandler#handle(javafx.event.Event)
			 */
			@Override
			public void handle(ActionEvent event) {
					if (kierros == 1) {
						
						controller.ManPois();
						
						onoff.setText("Manuaalinen ohjaus");
						kierros = 0;
					}else {
						controller.ManPaalle();
						onoff.setText("Autonominen ohjaus");
						kierros = 1;
					}
			}
		});
		
		Slider slider = new Slider();
		slider.setMin(0);
		slider.setMax(100);
		slider.setValue(100);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setMajorTickUnit(25);
		slider.setMinorTickCount(5);
		slider.setBlockIncrement(1);
		slider.setMinSize(300, 30);
		
		slider.setOnMouseReleased(new EventHandler<MouseEvent>(){
			/* (non-Javadoc)
			 * @see javafx.event.EventHandler#handle(javafx.event.Event)
			 */
			@Override
			public void handle(MouseEvent event) {
				int maara = (int) slider.getValue();
				controller.Nopeus(maara);
				
			}
		});
		
		grid.addEventHandler(KeyEvent.KEY_PRESSED, ev ->{
			if (ev.getCode() == KeyCode.RIGHT) {
				slider.setValue(slider.getValue()+1);
				ev.consume();
			}else if(ev.getCode() == KeyCode.LEFT) {
				slider.setValue(slider.getValue()-1);
				ev.consume();
			}else if(ev.getCode() == KeyCode.ENTER) {
				int maara = (int) slider.getValue();
				controller.Nopeus(maara);
				ev.consume();
			}
		});
		layer = new Canvas(400,400);
		
		gc = layer.getGraphicsContext2D();
		gc.setFill(Color.GREENYELLOW);
		gc.fillRect(0, 0, 399, 399);
		
		grid.add(layer, 0, 0,5,5);
		grid.add(slider, 0, 4, 4, 4);
		grid.add(sammu, 3, 2);
		grid.add(eteen, 1, 0);
		grid.add(taakse, 1, 2);
		grid.add(oikealle, 2, 1);
		grid.add(vasemalle, 0, 1);
		grid.add(onoff, 3, 0);
		grid.add(sonic, 4, 0);
		grid.add(sammuViesti, 3, 1);
		
		return grid;
	}
	
	/**
	 * <code>Main</code>:in pääohjelma.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Hoitaa musiikin soiton.
	 */
	private void Musiikia() {
		if(!mOn) {
			mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
			mediaPlayer.onRepeatProperty();
			mediaPlayer.play();	
			mOn = true;
			sonic.setText("Hiljaa");
        }else {
        	mediaPlayer.stop();
        	mOn = false;
        	sonic.setText("GottaGoFast");
        }
	}
}