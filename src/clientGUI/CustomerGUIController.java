package clientGUI;

import client.ClientController;
import client.NewWindowFrameController;
import commonClasses.Message;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 *
 *  Controller class for customer main window
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */

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
    private Label lbl_discount;

    public static CustomerGUIController controller;

    @FXML
    public void initialize() {
        controller = this;
        ClientController.userData.setFirstOrder(ifFirstOrder()); //check if it's first order
        if(ClientController.userData.isFirstOrder())
            lbl_discount.setText("You have 20% discount on first order!");
        else
            lbl_discount.setText("");
        lbl_username.setText("Hello, " + ClientController.userData.getFirstname() + " " + ClientController.userData.getLastname());
        double balance = ClientController.userData.getBalance();
        //round balance
        balance = balance*100;
        balance = Math.round(balance);
        balance = balance /100;
        lbl_balance.setText("Your balance is " + balance + " ₪");
    }

    /**
     * sends server request to check if its customer's first order
     * @return true if first order discount is available
     */
    private boolean ifFirstOrder(){
        Message msg = new Message();
        boolean res;
        msg.setCommand("isFirstOrder");
        msg.setMsg(ClientController.userLoginData.getUserid());
        ClientController.getClientController().send(msg);
        res = (boolean)(ClientController.messageFromServer).getMsg();
        return res;
    }

    /**
     * click on logout button
     * @param event mouse click
     */
    @FXML
    void clickedLogoutBtn(MouseEvent event) {
        Stage stage = (Stage) lbl_username.getScene().getWindow();
        ClientController.getClientController().logout(stage);
    }

    /**
     * click on MyOrders button, creates new MyOrders window and hides this window
     * @param event mouse click
     * @throws Exception javafx exception when creating a new window
     */
    @FXML
    void clickedMyOrdersBtn(MouseEvent event) throws Exception {
        NewWindowFrameController myordersWindow = new NewWindowFrameController("MyOrdersGUI");
        myordersWindow.start(new Stage());
        Stage thisScene = (Stage)btn_logout.getScene().getWindow();
        ClientController.savedWindows.setClientGUIWindow(thisScene);
        thisScene.hide();
    }

    /**
     * click on NewOrder button, creates new NewOrder window and hides this window
     * @param event mouse click
     * @throws Exception javafx exception when creating a new window
     */
    @FXML
    void clickedNewOrderBtn(MouseEvent event) throws Exception {
        NewWindowFrameController newOrderWindow = new NewWindowFrameController("NewOrderGUI");
        newOrderWindow.start(new Stage());
        Stage thisScene = (Stage)btn_logout.getScene().getWindow();
        ClientController.savedWindows.setClientGUIWindow(thisScene);
        thisScene.hide();
    }

    /**
     * click on ViewCatalog button, creates new ViewCatalog window and hides this window
     * @param event mouse click
     * @throws Exception javafx exception when creating a new window
     */
    @FXML
    void clickedViewCatalogBtn(MouseEvent event) throws Exception {
        NewWindowFrameController viewCatalogWindow = new NewWindowFrameController("ViewCatalogGUI");
        viewCatalogWindow.start(new Stage());
        Stage thisScene = (Stage)btn_logout.getScene().getWindow();
        ClientController.savedWindows.setClientGUIWindow(thisScene);
        thisScene.hide();
    }


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
