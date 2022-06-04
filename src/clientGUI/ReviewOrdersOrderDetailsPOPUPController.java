package clientGUI;

import client.ClientController;
import commonClasses.Order;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ReviewOrdersOrderDetailsPOPUPController {
    @FXML
    private ImageView btn_back;

    @FXML
    private TextArea txt_orderDetails;

    @FXML
    public void initialize(){
        Order selectedOrder = ReviewOrdersGUIController.controller.getSelectedOrder();
        txt_orderDetails.setText(selectedOrder.getDetails());
    }

    @FXML
    void clickedBackBtn(MouseEvent event) {
        Stage thisStage = (Stage) btn_back.getScene().getWindow();
        thisStage.close();
    }

    @FXML
    void enteredBackBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_back);
    }

    @FXML
    void leavedBackBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_back);
    }
}
