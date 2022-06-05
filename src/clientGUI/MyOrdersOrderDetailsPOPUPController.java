package clientGUI;

import client.ClientController;
import commonClasses.Order;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 *
 *  controller class for order details popup window in My Orders window
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */

public class MyOrdersOrderDetailsPOPUPController {

    @FXML
    private ImageView btn_back;

    @FXML
    private TextArea txt_orderDetails;

    @FXML
    private Label lbl_orderNumber;

    @FXML
    public void initialize(){
       Order selectedOrder =  MyOrdersGUIController.getMyOrdersControl().getSelectedOrder();
       txt_orderDetails.setText(selectedOrder.getDetails());
       lbl_orderNumber.setText("Order number " + selectedOrder.getOrderNumber() + ":");
    }

    /**
     * click on back button, closes this popup
     * @param event mouse click
     */
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
