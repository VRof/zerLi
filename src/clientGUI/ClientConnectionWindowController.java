package clientGUI;

import client.ClientController;
import client.NewWindowFrameController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
import java.io.IOException;
/**
 *
 *  Controller class of client connection window
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */
public class ClientConnectionWindowController {
	@FXML
	private TextField txt_serveripaddress;
	@FXML
	private Button btn_connect;
	@FXML
	private Label label_msg;
	@FXML
    private TextField txt_serverPort;

	@FXML
	public void initialize() {
	label_msg.setText("");	
	}
    @FXML

	ClientController clientCtrl;

	/**
	 * connect client to server when clicked "connect" button
	 * @param event mouse click
	 * @throws Exception javafx exception when creating a new window
	 */
    @FXML
	void btnConnectClick(ActionEvent event) throws Exception {
    	try {
    		int port = Integer.parseInt(txt_serverPort.getText());
			clientCtrl = new ClientController(txt_serveripaddress.getText(),port);
			if (clientCtrl.isConnected()) {
				NewWindowFrameController loginWindow = new NewWindowFrameController("LoginGUI");
				loginWindow.start(new Stage());
				btn_connect.getScene().getWindow().hide();
			}
		} catch (NumberFormatException e) {
			label_msg.setText("Port must be a number (5555)");
		} catch (IOException e) {
			label_msg.setText("Can't connect to " + txt_serveripaddress.getText() +" port: " + txt_serverPort.getText());
		}
    }

	/**
	 * close client connection window
	 * @param event mouse click on exit button
	 */
    @FXML
    void btnExitClick(ActionEvent event) {
    	System.out.println("Exiting client connection window");
    	System.exit(0);
    }
}
