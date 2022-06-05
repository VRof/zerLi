package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
/**
 *
 *  new window frame controller for ClientConnectionGUI.fxml file
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */
public class ClientConnectionWindowFrameController extends Application {
    private AnchorPane mainLayout;
    /**
     *
     * @param primaryStage stage for this window
     * @throws Exception javafx exception when creating a new window
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("ZerLi");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/clientFXML/ClientConnectionGUI.fxml"));
        mainLayout = loader.load();
        Scene scene = new Scene(mainLayout);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
