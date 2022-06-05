package clientGUI;

import client.ClientController;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
/**
 *
 *  controller class for help popup window in new order window
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */
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
