package clientGUI;

import client.ClientController;
import client.NewWindowFrameController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 *
 *  Customer Serves Controller - made for users from type "customer serves",
 *  the controller 2 buttons, user can "Insert complaint"/"Review Survey",
 *  if there is an error, a suitable message will be shown.
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */
public class CustomerServicesGUIController {

    @FXML
    private Label lbl_userName;

    @FXML
    private ImageView btn_reviewSurvey;

    @FXML
    private ImageView btn_insertComplaint;

    @FXML
    private Label lbl_intro;

    @FXML
    private ImageView btn_logout;

    /**
     *
     */
    @FXML
    public void initialize() {
        lbl_intro.setText("Customer Service " + ClientController.userLoginData.getUserid());
        lbl_userName.setText("Hello, " + ClientController.userLoginData.getUsername()); }

    /**
     * clickedInsertComplaintBtn - user click on this button to pen "Insert complaint" window
     * @param event - mouse click
     * @throws Exception - if error while creating new window
     */
    @FXML
    void clickedInsertComplaintBtn(MouseEvent event) throws Exception { newWindow("InsertComplaintGUI");}

    /**
     * clickedLogoutBtn - user click on this button to log out and then return to log in window
     * @param event - mouse click
     * @throws Exception - if error while creating new window
     */
    @FXML
    void clickedLogoutBtn(MouseEvent event) {
        Stage stage = (Stage)lbl_userName.getScene().getWindow();
        ClientController.getClientController().logout(stage);
    }

    /**
     * clickedInsertComplaintBtn - user click on this button to pen "review survey" window
     * @param event - mouse click
     * @throws Exception - if error while creating new window
     */
    @FXML
    void clickedReviewSurveyBtn(MouseEvent event) throws Exception { newWindow("ReviewSurveyGUI");}

    /**
     * enteredInsertComplaintBtn - function for making shadow for button
     * @param event - entering the button with the mouse
     *
     */
    @FXML
    void enteredInsertComplaintBtn(MouseEvent event) { ClientController.getClientController().enteredButton(btn_insertComplaint);}

    /**
     * enteredLogoutBtn - function for making shadow for button
     * @param event - entering the button with the mouse
     *
     */
    @FXML
    void enteredLogoutBtn(MouseEvent event) { ClientController.getClientController().enteredButton(btn_logout);}

    /**
     * enteredReviewSurveyBtn - function for making shadow for button
     * @param event - entering the button with the mouse
     *
     */
    @FXML
    void enteredReviewSurveyBtn(MouseEvent event) { ClientController.getClientController().enteredButton(btn_reviewSurvey);}

    /**
     * leavedInsertComplaintBtn - function to hide shadow for button
     * @param event - leaving the button with the mouse
     *
     */
    @FXML
    void leavedInsertComplaintBtn(MouseEvent event) { ClientController.getClientController().leavedButton(btn_insertComplaint);}

    /**
     * leavedLogoutBtn - function to hide shadow for button
     * @param event - leaving the button with the mouse
     *
     */
    @FXML
    void leavedLogoutBtn(MouseEvent event) { ClientController.getClientController().leavedButton(btn_logout);}

    /**
     * leavedReviewSurveyBtn - function to hide shadow for button
     * @param event - leaving the button with the mouse
     *
     */
    @FXML
    void leavedReviewSurveyBtn(MouseEvent event) { ClientController.getClientController().leavedButton(btn_reviewSurvey);}

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
