package clientGUI;

import client.ClientController;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 *
 *  Controller class for cancel order popup in MyOrders window
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */

public class CancelOrderPopUpGUIController {

    @FXML
    private ImageView btn_cancelOrder;

    @FXML
    private ImageView btn_back;

    @FXML
    void clickedBackBtn(MouseEvent event) {
        Stage thisStage = (Stage) btn_back.getScene().getWindow();
        thisStage.close();
    }

    @FXML
    void clickedCancelOrderBtn(MouseEvent event) {
        MyOrdersGUIController.getMyOrdersControl().cancelOrder(); //cancel order
        Stage thisStage = (Stage) btn_back.getScene().getWindow(); //close this window
        thisStage.close();
    }

    @FXML
    void enteredBackBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_back);
    }

    @FXML
    void enteredCancelOrderBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_cancelOrder);
    }

    @FXML
    void leavedBackBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_back);
    }

    @FXML
    void leavedCancelOrderBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_cancelOrder);
    }

}
