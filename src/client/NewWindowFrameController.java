package client;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

/**
 *
 *  new window frame creator, loads window by its name, sends disconnect request when closed
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */

public class NewWindowFrameController extends Application {
    private AnchorPane mainLayout;
    private String windowName;
    public NewWindowFrameController (String windowName) {
        this.windowName = windowName;
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("ZerLi");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/clientFXML/" + windowName +".fxml"));
        mainLayout = loader.load();
        Scene scene = new Scene(mainLayout);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                ClientController.getClientController().disconnect();
                try {
                    ClientController.getClientController().closeConnection();
                } catch (IOException e) {
                    System.out.println(e);
                }
                System.exit(0);
            }});
        primaryStage.show();
    }
}
