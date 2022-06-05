package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 *  new popup window frame creator, loads popup by its name, doesn't disconnect when closed
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */

public class NewPopUpWindowFrameController extends Application {
    private AnchorPane mainLayout;
    private String windowName;
    public NewPopUpWindowFrameController (String windowName) {
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
        primaryStage.show();
    }
}
