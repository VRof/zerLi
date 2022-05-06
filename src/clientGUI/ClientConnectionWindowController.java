package clientGUI;

import client.ClientController;
import clientFrameControllers.LoginWindowFrameController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientConnectionWindowController {
	@FXML
	private TextField txt_serveripaddress;
	@FXML
	private Button btn_connect;
	@FXML
	private Label label_msg;
	@FXML
	private Button btn_exit;
	@FXML
    private TextField txt_serverPort;
	
	@FXML
	public void initialize() {
	label_msg.setText("");	
	}
    @FXML

	ClientController clientCtrl;

    @FXML
	void btnConnectClick(ActionEvent event) throws Exception {
    	try {
    		int port = Integer.parseInt(txt_serverPort.getText());
			clientCtrl = new ClientController(txt_serveripaddress.getText(),port);
			if (clientCtrl.isConnected()) {
				LoginWindowFrameController tableWindow = new LoginWindowFrameController();
				tableWindow.start(new Stage());
				btn_connect.getScene().getWindow().hide();
			}
		} catch (NumberFormatException e) {
			label_msg.setText("Port must be a number (5555)");
		} catch (IOException e) {
			label_msg.setText("Can't connect to " + txt_serveripaddress.getText() +" port: " + txt_serverPort.getText());
		}
    }

    @FXML
    void btnExitClick(ActionEvent event) {
    	System.out.println("Exiting client connection window");
    	System.exit(0);
    }
}
