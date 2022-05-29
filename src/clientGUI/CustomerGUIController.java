package clientGUI;

import client.ClientController;
<<<<<<< HEAD
import client.NewWindowFrameController;
=======
>>>>>>> habib
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

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
        lbl_username.setText("Hello, " + ClientController.userData.getFirstname() + " " + ClientController.userData.getLastname());
        lbl_balance.setText("Your balance is " + ClientController.userData.getBalance() + " ₪");
    }

    @FXML
    void clickedLogoutBtn(MouseEvent event) {
        Stage stage = (Stage) lbl_username.getScene().getWindow();
        ClientController.getClientController().logout(stage);
    }

    @FXML
    void clickedMyOrdersBtn(MouseEvent event) throws Exception {
        NewWindowFrameController myordersWindow = new NewWindowFrameController("MyOrdersGUI");
        myordersWindow.start(new Stage());
        Stage thisScene = (Stage)btn_logout.getScene().getWindow();
        ClientController.savedWindows.setClientGUIWindow(thisScene);
        thisScene.hide();
    }

    @FXML
    void clickedNewOrderBtn(MouseEvent event) throws Exception {
        NewWindowFrameController newOrderWindow = new NewWindowFrameController("NewOrderGUI");
        newOrderWindow.start(new Stage());
        Stage thisScene = (Stage)btn_logout.getScene().getWindow();
        ClientController.savedWindows.setClientGUIWindow(thisScene);
        thisScene.hide();
    }

    @FXML
    void clickedViewCatalogBtn(MouseEvent event) throws Exception {
        NewWindowFrameController viewCatalogWindow = new NewWindowFrameController("ViewCatalogGUI");
        viewCatalogWindow.start(new Stage());
        Stage thisScene = (Stage)btn_logout.getScene().getWindow();
        ClientController.savedWindows.setClientGUIWindow(thisScene);
        thisScene.hide();
    }

<<<<<<< HEAD

=======
>>>>>>> habib
    @FXML
    void enteredLogoutBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_logout);
    }

    @FXML
    void enteredMyOrdersBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_MyOrders);
    }

    @FXML
    void enteredNewOrderBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_NewOrder);
    }

    @FXML
    void enteredViewCatalogBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_ViewCatalog);
    }

    @FXML
    void leavedLogoutBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_logout);
    }

    @FXML
    void leavedMyOrdersBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_MyOrders);
    }

    @FXML
    void leavedNewOrderBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_NewOrder);
    }

    @FXML
    void leavedViewCatalogBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_ViewCatalog);
    }

}
