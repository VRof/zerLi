package server;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 *  class descriptions
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */
public class ServerUI extends Application {

	public static void main(String[] args) {	

		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		ServerWindowFrameController startwindow = new ServerWindowFrameController(); //server window
		startwindow.start(primaryStage);
	}

}
