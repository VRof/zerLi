package clientGUI;

import client.ClientController;
import client.NewWindowFrameController;
import clientClasses.Message;
import clientClasses.UserLoginData;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;


public class LoginGUIController {
    /**
     * LoginGUIController - controller class for login window
     */
    @FXML
    private TextField txt_username;

    @FXML
    private PasswordField txt_password;

    @FXML
    private Label lbl_error;

    @FXML
    private ImageView btn_login;

    @FXML
    private ImageView btn_exit;


    public static LoginGUIController loginController;
    private ClientController connection;


    /**
     * initialize with empty fields
     */
    @FXML
    public void initialize() {
        loginController = this;
        lbl_error.setText("");
        txt_username.setText("");
        txt_password.setText("");
    }

    /**
     * clickedExitBtn - method called when clicked on exit button, closes connection and exits app
     * @param event - mouse click
     * @throws IOException - throws if failed to close connection between client and server
     */
    @FXML
    void clickedExitBtn(MouseEvent event) throws IOException {
        ClientController.getClientController().disconnect();
        while(ClientController.awaitResponse);
        connection.closeConnection();
        System.exit(0);
    }

    /**
     * clickedLoginBtn -  method called when clicked on login button, opens appropriate window for each user type
     * identify user by sending username and password to server and receive user data from it
     * @param event - mouse click
     * @throws Exception - sqlexeption if error occurred while reading data send from server, exception if error occurred while creating new window  specific user type
     */
    @FXML
    void clickedLoginBtn(MouseEvent event) throws Exception {
        connection = ClientController.getClientController();
        CachedRowSet cachedMsg = null; //row from db with user login data
        UserLoginData userLoginData = new UserLoginData(); //user login data from server (id,username,password,usertype,status)
        String[] userData = new String[2]; //array which is sent to server (username,password)
        Message msg = new Message(); //msg to be sent to server

        userData[0] = txt_username.getText();
        userData[1] = txt_password.getText();

        if (txt_username.getText().equals("") || txt_password.getText().equals("")) { //if username or password is empty fields
            lbl_error.setText("  Please enter username and password");
            return;
        }
        //convert and send to server:
        msg.setCommand("login");
        msg.setMsg((Object) userData);
        connection.send(msg);
        while(connection.awaitResponse); //wait for data from server

        if(connection.messageFromServer.getCommand().equals("already logged in")){ //if client already connected
            lbl_error.setText("       this user is already connected");
            return;
        }


        if (connection.messageFromServer.getCommand().equals("wrong")) { //wrong username or password
            lbl_error.setText("    Wrong username or password");
            return;
        }

        cachedMsg = (CachedRowSet) (connection.messageFromServer.getMsg()); //message received from server (row from login table)

        while (cachedMsg.next()) { //get user data
            userLoginData.setUserid(cachedMsg.getInt("userid"));
            userLoginData.setUsername(cachedMsg.getString("username"));
            userLoginData.setPassword(cachedMsg.getString("password"));
            userLoginData.setUsertype(cachedMsg.getString("usertype"));
            userLoginData.setStatus(cachedMsg.getString("status"));
        }

        connection.userLoginData = userLoginData; //save user login data for future use

        if (userLoginData.getStatus().equals("frozen")) { //if account status is frozen
            lbl_error.setText("Account is frozen, contact administrator");
        } else {
            switch (userLoginData.getUsertype()) { //open new window for each user type
                case "customer":
                    newWindow("CustomerGUI");
                    break;
                case "manager":
                    newWindow("ManagerGUI");
                    break;
                case "ceo":
                    newWindow("CEOGUI");
                    break;
                case "marketingworker":
                    newWindow("MarketingWorkerGUI");
                    break;
                case "deliveryguy":
                    newWindow("DeliveryGuyGUI");
                    break;
                case "customerservice":
                    newWindow("CustomerServiceGUI");
                    break;
            }
        }
    }

    @FXML
    void enteredExitBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_exit);
    }

    @FXML
    void enteredLoginBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_login);
    }

    @FXML
    void leavedExitBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_exit);
    }

    @FXML
    void leavedLoginBTn(MouseEvent event) {ColorAdjust blackout = new ColorAdjust();
        ClientController.getClientController().leavedButton(btn_login);
    }

    /**
     * newWindow - creates new window and hides login window
     * @param windowName - name of fxml file to open in clientFXML folder
     * @throws Exception - if error while creating new window
     */
    private void newWindow(String windowName) throws Exception {
        NewWindowFrameController customerWindow = new NewWindowFrameController(windowName);
        customerWindow.start(new Stage());
        Stage stage = (Stage)  lbl_error.getScene().getWindow();
        ClientController.savedWindows.setLoginWindow(stage);
        stage.hide();
    }
}
