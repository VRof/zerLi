package serverFrameControllers;

import java.io.IOException;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import server.ServerControl;

public class ServerWindowFrameController extends Application {
	private Stage primaryStage;
	private AnchorPane mainLayout;
	public static void main(String[] args) {
		launch(args);
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("ZerLi demo server");
		showMainView();
		
	}
	private void showMainView() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/serverFXML/ServerWindow.fxml"));
		mainLayout = loader.load();
		Scene scene = new Scene(mainLayout);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		      public void handle(WindowEvent we) {
		    	  try {
					ServerControl.closeAll();
				} catch (SQLException e) {
					System.out.println("Can't close connections "+e);
				}
		  }});
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
