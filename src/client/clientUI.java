package client;

import clientFrameControllers.ClientConnectionWindowFrameController;
import javafx.application.Application;
import javafx.stage.Stage;

public class clientUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        ClientConnectionWindowFrameController startwindow = new ClientConnectionWindowFrameController();
        startwindow.start(primaryStage);
    }
}
