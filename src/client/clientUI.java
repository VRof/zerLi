package client;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 *  main class of client side
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */

public class clientUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    /**
     * create client connection window
     */
    public void start(Stage primaryStage) throws Exception {
        ClientConnectionWindowFrameController startwindow = new ClientConnectionWindowFrameController();
        startwindow.start(primaryStage);

    }
}
