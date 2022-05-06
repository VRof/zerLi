package server;

import javafx.application.Application;
import javafx.stage.Stage;
import serverFrameControllers.ServerWindowFrameController;

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
