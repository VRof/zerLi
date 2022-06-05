package clientGUI;


import client.ClientController;
import client.NewWindowFrameController;
import commonClasses.Message;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;

/**
 * ShopManagerGUIController class is responsible for the main GUI of each shop manager, it
 * allows them to pick one of six desired actions that are made for shop managers to manage
 * customers, workers and viewing reports.
 *
 * <p> Project Name: Zer-Li (Flowers store in Java) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */
public class ShopManagerGUIController {
    @FXML
    private ImageView manageCustomersBtn;

    @FXML
    private ImageView approveCustomersBtn;

    @FXML
    private ImageView managePermissionsBtn;
    @FXML
    private Label lblManager;

    @FXML
    private ImageView logOutBtn;

    @FXML
    private ImageView viewReportsBtn;

    @FXML
    private ImageView viewCancellationRequestBtn;

    @FXML
    private ImageView viewOrdersBtn;

    public ClientController cc =ClientController.getClientController();

    /**
     * initialize method sends userid that we saved from login window to the server
     * for the goal of getting the firs and last name of the user in order to show as a welcome message
     */
    @FXML
    public void initialize() {
        CachedRowSet cachedMsg;
        String first="",last="";
        //*********
        //1.send msg to server
        Message msg = new Message(); //msg to be sent to server
        msg.setCommand("getName");
        msg.setMsg(ClientController.userLoginData.getUserid());
        cc.send(msg);
        //2.receive from server
        while (cc.awaitResponse) ; //wait for data from server
        cachedMsg = (CachedRowSet) (cc.messageFromServer.getMsg());
        try {
            while (cachedMsg.next()){
               first = cachedMsg.getString("firstname");
               last =  cachedMsg.getString("lastname");
            }
        }
        catch(SQLException e){}

        lblManager.setText("Hello " +first+" "+last);
    }

    /**
     * clicked logout button, send logout request to server
     * @param event mouse click
     */
    @FXML
    void ClickedLogoutBtn(MouseEvent event) {
        Stage stage = (Stage)lblManager.getScene().getWindow();
        cc.logout(stage);

    }

    /**
     * clicked on "cancellation request" button
     * @param event mouse click
     * @throws Exception javafx exception when creating a new window
     */
    @FXML
    void clickedViewCancellationRequestBtn(MouseEvent event) throws Exception{
        NewWindowFrameController customerWindow = new NewWindowFrameController("ReviewCancellationGUI");
        customerWindow.start(new Stage());
        Stage stage = (Stage) lblManager.getScene().getWindow();
        stage.hide();
    }

    /**
     * clicked on "View orders" button
     * @param event mouse click
     * @throws Exception javafx exception when creating a new window
     */
    @FXML
    void clickedViewOrdersBtn(MouseEvent event) throws Exception {
        NewWindowFrameController customerWindow = new NewWindowFrameController("ReviewOrdersGUI");
        customerWindow.start(new Stage());
        Stage stage = (Stage) lblManager.getScene().getWindow();
        stage.hide();

    }

    /**
     * clicked "ViewReports" button
     * @param event mouse click
     * @throws Exception javafx exception when creating a new window
     */
    @FXML
    void clickedViewReportsBtn(MouseEvent event) throws Exception {
        NewWindowFrameController customerWindow = new NewWindowFrameController("ViewReportsGUI");
        customerWindow.start(new Stage());
        Stage stage = (Stage) lblManager.getScene().getWindow();
        stage.hide();
    }
    @FXML
    /**
     * clicked "ManageCustomers" button
     * @param event mouse click
     * @throws Exception javafx exception when creating a new window
     */
    void clickedManageCustomersBtn(MouseEvent event) throws Exception {
        NewWindowFrameController customerWindow = new NewWindowFrameController("ManageCustomersGUI");
        customerWindow.start(new Stage());
        Stage stage = (Stage) lblManager.getScene().getWindow();
        stage.hide();
    }
    /**
     * clicked "ApproveCustomers" button
     * @param event mouse click
     * @throws Exception javafx exception when creating a new window
     */
    @FXML
    void clickedApproveCustomersBtn(MouseEvent event) throws Exception {
        NewWindowFrameController customerWindow = new NewWindowFrameController("ApproveCustomersGUI");
        customerWindow.start(new Stage());
        Stage stage = (Stage) lblManager.getScene().getWindow();
        stage.hide();
    }
    /**
     * clicked "ManagePermissions" button
     * @param event mouse click
     * @throws Exception javafx exception when creating a new window
     */
    @FXML
    void clickedManagePermissionsBtn(MouseEvent event) throws Exception {
        NewWindowFrameController customerWindow = new NewWindowFrameController("ManagePermissionsGUI");
        customerWindow.start(new Stage());
        Stage stage = (Stage) lblManager.getScene().getWindow();
        stage.hide();
    }

    @FXML
    void enteredLogoutBtn(MouseEvent event) {
        cc.enteredButton(logOutBtn);
    }

    @FXML
    void enteredViewCancelationRequestBtn(MouseEvent event) {
        cc.enteredButton(viewCancellationRequestBtn);
    }

    @FXML
    void enteredViewOrdersBtn(MouseEvent event) {
        cc.enteredButton(viewOrdersBtn);
    }

    @FXML
    void enteredViewReportsBtn(MouseEvent event) {
        cc.enteredButton(viewReportsBtn);
    }

    @FXML
    void leavedLogoutBtn(MouseEvent event) {
        cc.leavedButton(logOutBtn);
    }

    @FXML
    void leavedViewCancelationRequestBtn(MouseEvent event) {
        cc.leavedButton(viewCancellationRequestBtn);
    }

    @FXML
    void leavedViewOrdersBtn(MouseEvent event) {
        cc.leavedButton(viewOrdersBtn);
    }

    @FXML
    void leavedViewReportsBtn(MouseEvent event) {
        cc.leavedButton(viewReportsBtn);
    }

    @FXML
    void enteredApproveCustomersBtn(MouseEvent event) {cc.enteredButton(approveCustomersBtn);}
    @FXML
    void enteredManagePermissionsBtn(MouseEvent event) {cc.enteredButton(managePermissionsBtn);}
    @FXML
    void enteredManageCustomersBtn(MouseEvent event) {cc.enteredButton(manageCustomersBtn);}
    @FXML
    void leavedManageCustomersBtn(MouseEvent event) {cc.leavedButton(manageCustomersBtn);}

    @FXML
    void leavedApproveCustomersBtn(MouseEvent event) {cc.leavedButton(approveCustomersBtn);}
    @FXML
    void leavedManagePermissionsBtn(MouseEvent event) {cc.leavedButton(managePermissionsBtn);}

}
