package clientGUI;

import client.ClientController;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class PopUpMessageGUIController {

    @FXML
    private TextArea txt_msg;

    @FXML
    public void initialize(){
        txt_msg.setText((String)ClientController.messageFromServer.getMsg());
    }
}
