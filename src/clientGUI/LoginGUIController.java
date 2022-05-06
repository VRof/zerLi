package clientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class LoginGUIController {

    @FXML
    private TextField txt_username;

    @FXML
    private TextField txt_password;

    @FXML
    private Label lbl_error;

    @FXML
    private ImageView btn_login;

    @FXML
    private ImageView btn_exit;

    @FXML
    void clickedExitBtn(MouseEvent event) {

    }

    @FXML
    void clickedLoginBtn(MouseEvent event) {

    }

    @FXML
    void enteredExitBtn(MouseEvent event) {
        ColorAdjust blackout = new ColorAdjust();
        blackout.setBrightness(-0.2);
        btn_exit.setEffect(blackout);
    }

    @FXML
    void enteredLoginBtn(MouseEvent event) {
        ColorAdjust blackout = new ColorAdjust();
        blackout.setBrightness(-0.2);
        btn_login.setEffect(blackout);
    }

    @FXML
    void leavedExitBtn(MouseEvent event) {
        ColorAdjust blackout = new ColorAdjust();
        blackout.setBrightness(0);
        btn_exit.setEffect(blackout);
    }

    @FXML
    void leavedLoginBTn(MouseEvent event) {
        ColorAdjust blackout = new ColorAdjust();
        blackout.setBrightness(0);
        btn_login.setEffect(blackout);
    }

}
