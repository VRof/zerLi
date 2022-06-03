package clientGUI;

import client.ClientController;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class HelpPOPUPController {

    @FXML
    private ImageView btn_ok;

    @FXML
    void clickedOkBtn(MouseEvent event) {
        Stage thisstage = (Stage)btn_ok.getScene().getWindow();
        thisstage.close();
    }

    @FXML
    void enteredOkBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_ok);
    }

    @FXML
    void leavedOkBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_ok);
    }

}
