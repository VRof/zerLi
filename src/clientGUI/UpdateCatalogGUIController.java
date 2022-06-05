package clientGUI;

import client.ClientController;
import commonClasses.Message;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import static client.ClientController.savedWindows;

/**
 *
 *  Update Catalog Controller - Marketing Worker can update catalog using this window,
 *  by applying discount for all items in catalog
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */
public class UpdateCatalogGUIController {

    @FXML
    private Label lbl_error;

    @FXML
    private ImageView btn_activeDiscount;

    @FXML
    private ImageView btn_back;

    @FXML
    private ImageView btn_inactiveDiscount;

    @FXML
    private TextField txt_p;

    private Message msg = new Message();

    private ClientController conn = ClientController.getClientController();
    private float num1 = getDiscount();

    /**
     *
     * Gui init - setup labels
     */
    public void initialize() {
        if (num1 != 0.0){ lbl_error.setText(num1* 100 +"% Discount available on catalog, click on inactive button to end it! "); }
    }

    /**
     * getDiscount - get discount value from Data Base if there is one available
     * @return msgFromServer - float number
     */
    private float getDiscount(){
        Message msg = new Message();
        msg.setCommand("ifDiscount");
        msg.setMsg("");
        ClientController.getClientController().send(msg);
        while(ClientController.getClientController().awaitResponse);
        float  msgFromServer = (float)(ClientController.getClientController().messageFromServer.getMsg());
        return msgFromServer;
    }
    /**
     * clickedActiveDiscountBtn - send the amount of discount to server in order to activate it
     * @param event - entering the button with the mouse
     */
    @FXML
    void clickedActiveDiscountBtn(MouseEvent event) {
        if (num1 != 0.0){ lbl_error.setText(num1* 100 +"% Discount available on catalog, Cannot activate another one!"); }
        else {
        lbl_error.setText("Activating Discount ..");
        float dis = Float.parseFloat(txt_p.getText());
        if(dis > 100 || dis<0){
            lbl_error.setText("Incorrect discount percent");
            return;
        }
        msg.setCommand("activeDiscount");
        msg.setMsg(dis);
        conn.send(msg);
        while(conn.awaitResponse);
        lbl_error.setText("Discount Activated!");}
    }

    /**
     * clickedBackBtn - click in back button to get back to user original window
     * @param event - entering the button with the mouse
     */
    @FXML
    void clickedBackBtn(MouseEvent event) {
        Stage stage = ((Stage)lbl_error.getScene().getWindow());
        stage.close();
        Stage window = savedWindows.getUserWindow();
        LoginGUIController.loginController.initialize();
        window.show();
    }

    /**
     * clickedInactiveDiscountBtn - send request to server to end the discount and return the origin prices in catalog
     * @param event - entering the button with the mouse
     */
    @FXML
    void clickedInactiveDiscountBtn(MouseEvent event) {
        lbl_error.setText("Dis Activating Discount ..");
        msg.setCommand("DistinctiveDiscount");
        msg.setMsg("");
        conn.send(msg);
        while(conn.awaitResponse);
        num1 = 0;
        lbl_error.setText("Discount Dis Activated!");
    }

    /**
     * enteredActiveDiscountBtn - function for making shadow for button
     * @param event - entering the button with the mouse
     */
    @FXML
    void enteredActiveDiscountBtn(MouseEvent event) { conn.enteredButton(btn_activeDiscount);}

    /**
     * enteredBackBtn - function for making shadow for button
     * @param event - entering the button with the mouse
     */
    @FXML
    void enteredBackBtn(MouseEvent event) { conn.enteredButton(btn_back); }

    /**
     * enteredInactiveDiscountBtn - function for making shadow for button
     * @param event - entering the button with the mouse
     */
    @FXML
    void enteredInactiveDiscountBtn(MouseEvent event) { conn.enteredButton(btn_inactiveDiscount);}

    /**
     * leavedActiveDiscountBtn - function to hide shadow for button
     * @param event - leaving the button with the mouse
     */
    @FXML
    void leavedActiveDiscountBtn(MouseEvent event) { conn.leavedButton(btn_activeDiscount); }

    /**
     * leavedBackBtn - function to hide shadow for button
     * @param event - leaving the button with the mouse
     */
    @FXML
    void leavedBackBtn(MouseEvent event) { conn.leavedButton(btn_back); }

    /**
     * leavedInactiveDiscountBtn - function to hide shadow for button
     * @param event - leaving the button with the mouse
     */
    @FXML
    void leavedInactiveDiscountBtn(MouseEvent event) { conn.leavedButton(btn_inactiveDiscount); }
}

