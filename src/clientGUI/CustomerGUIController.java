package clientGUI;

import client.ClientController;
import client.LoginWindowFrameController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import serverClasses.Message;

public class CustomerGUIController {

    @FXML
    private Label lbl_username;

    @FXML
    private ImageView btn_MyOrders;

    @FXML
    private ImageView btn_NewOrder;

    @FXML
    private ImageView btn_ViewCatalog;

    @FXML
    private ImageView btn_logout;

    @FXML
    private Label lbl_balance;

    @FXML
    public void initialize() {
        lbl_username.setText("Hello " + ClientController.userLoginData.getUsername());
    }

    @FXML
    void clickedLogoutBtn(MouseEvent event) throws Exception {
        Message msg = new Message();
        msg.setCommand("disconnect");
        msg.setMsg((Object) ClientController.userLoginData.getUserid());
        ClientController.getClientController().send(msg);
        Stage stage = (Stage)lbl_username.getScene().getWindow();
        stage.close();
        LoginWindowFrameController newloginwindow = new LoginWindowFrameController("LoginGUI");
        newloginwindow.start(new Stage());
    }

    @FXML
    void clickedMyOrdersBtn(MouseEvent event) {

    }

    @FXML
    void clickedNewOrderBtn(MouseEvent event) {

    }

    @FXML
    void clickedViewCatalogBtn(MouseEvent event) {

    }



















    @FXML
    void enteredLogoutBtn(MouseEvent event) {
        ColorAdjust blackout = new ColorAdjust();
        blackout.setBrightness(-0.2);
        btn_logout.setEffect(blackout);
    }

    @FXML
    void enteredMyOrdersBtn(MouseEvent event) {
        ColorAdjust blackout = new ColorAdjust();
        blackout.setBrightness(-0.2);
        btn_MyOrders.setEffect(blackout);
    }

    @FXML
    void enteredNewOrderBtn(MouseEvent event) {
        ColorAdjust blackout = new ColorAdjust();
        blackout.setBrightness(-0.2);
        btn_NewOrder.setEffect(blackout);
    }

    @FXML
    void enteredViewCatalogBtn(MouseEvent event) {
        ColorAdjust blackout = new ColorAdjust();
        blackout.setBrightness(-0.2);
        btn_ViewCatalog.setEffect(blackout);
    }

    @FXML
    void leavedLogoutBtn(MouseEvent event) {
        ColorAdjust blackout = new ColorAdjust();
        blackout.setBrightness(0);
        btn_logout.setEffect(blackout);
    }

    @FXML
    void leavedMyOrdersBtn(MouseEvent event) {
        ColorAdjust blackout = new ColorAdjust();
        blackout.setBrightness(0);
        btn_MyOrders.setEffect(blackout);
    }

    @FXML
    void leavedNewOrderBtn(MouseEvent event) {
        ColorAdjust blackout = new ColorAdjust();
        blackout.setBrightness(0);
        btn_NewOrder.setEffect(blackout);
    }

    @FXML
    void leavedViewCatalogBtn(MouseEvent event) {
        ColorAdjust blackout = new ColorAdjust();
        blackout.setBrightness(0);
        btn_ViewCatalog.setEffect(blackout);
    }

}
