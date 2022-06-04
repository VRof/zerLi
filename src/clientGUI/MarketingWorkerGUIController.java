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
 *
 *  Marketing Worker Controller - made for users from type "Marketing Worker",
 *  the controller include 2 buttons, User can "Update catalog"/"Upload Survey Results"
 *  using specific window, if there is an error, a suitable message will be shown.
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */
public class MarketingWorkerGUIController {

    @FXML
    private Label lbl_userName;

    @FXML
    private ImageView btn_UpdateCatalog;

    @FXML
    private ImageView btn_UploadSurveyResults;

    @FXML
    private ImageView btn_logout;


    private ClientController conn = ClientController.getClientController();
    /**
     * set up window
     */
    @FXML
    public void initialize() {
        CachedRowSet cachedMsg;
        String first = "", last = "";
        //1.send msg to server
        Message msg = new Message(); //msg to be sent to server
        msg.setCommand("getName");
        msg.setMsg(ClientController.userLoginData.getUserid());
        conn.send(msg);
        //2.receive from server
        while (conn.awaitResponse) ; //wait for data from server
        cachedMsg = (CachedRowSet) (conn.messageFromServer.getMsg());
        try {
            while (cachedMsg.next()) {
                first = cachedMsg.getString("firstname");
                last = cachedMsg.getString("lastname");
            }
        } catch (SQLException e) {
        }
        lbl_userName.setText("Hello" + " " + first + " " + last);
    }

    /**
     * * clickedLogoutBtn - user click on this button to log out and then return to log in window
     * @param event - mouse click
     * @throws Exception - if error while creating new window
     */
    @FXML
    void clickedLogoutBtn(MouseEvent event) {
        Stage stage = (Stage)lbl_userName.getScene().getWindow();
        conn.logout(stage);
    }

    /**
     * clickedUpdateCatalogBtn - user click on this button to pen "Update Catalog" window
     * @param event - mouse click
     * @throws Exception - if error while creating new window
     */
    @FXML
    void clickedUpdateCatalogBtn(MouseEvent event) throws Exception  { newWindow("UpdateCatalogGUI"); }

    /**
     * clickedInsertComplaintBtn - user click on this button to pen "Upload Survey Results" window
     * @param event - mouse click
     * @throws Exception - if error while creating new window
     */
    @FXML
    void clickedUploadSurveyResultsBtn(MouseEvent event) throws Exception { newWindow("UploadSurveyResultsGUI"); }

    /**
     * enteredLogoutBtn - function for making shadow for button
     * @param event - entering the button with the mouse
     */
    @FXML
    void enteredLogoutBtn(MouseEvent event) { conn.enteredButton(btn_logout); }

    /**
     * enteredUpdateCatalogBtn - function for making shadow for button
     * @param event - entering the button with the mouse
     *
     */
    @FXML
    void enteredUpdateCatalogBtn(MouseEvent event) {  conn.enteredButton(btn_UpdateCatalog); }

    /**
     * enteredUploadSurveyResultsBtn - function for making shadow for button
     * @param event - entering the button with the mouse
     */
    @FXML
    void enteredUploadSurveyResultsBtn(MouseEvent event) { conn.enteredButton(btn_UploadSurveyResults); }

    /**
     * exitedLogoutBtn - function to hide shadow for button
     * @param event - leaving the button with the mouse
     */
    @FXML
    void exitedLogoutBtn(MouseEvent event) { conn.leavedButton(btn_logout); }

    /**
     * exitedUpdateCatalogBtn - function to hide shadow for button
     * @param event - leaving the button with the mouse
     */
    @FXML
    void exitedUpdateCatalogBtn(MouseEvent event) { conn.leavedButton(btn_UpdateCatalog); }

    /**
     * exitedUploadSurveyResultsBtn - function to hide shadow for button
     * @param event - leaving the button with the mouse
     */
    @FXML
    void exitedUploadSurveyResultsBtn(MouseEvent event) { conn.leavedButton(btn_UploadSurveyResults); }

    /**
     * newWindow - creates new window and hides login window
     * @param windowName - name of fxml file to open in clientFXML folder
     * @throws Exception - if error while creating new window
     */
    private void newWindow(String windowName) throws Exception {
        NewWindowFrameController window = new NewWindowFrameController(windowName);
        window.start(new Stage());
        Stage stage = (Stage)lbl_userName.getScene().getWindow();
        ClientController.savedWindows.setUserWindow(stage);
        stage.hide();
    }
}

