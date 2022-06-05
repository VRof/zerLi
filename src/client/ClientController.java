package client;

import clientClasses.UserData;
import clientClasses.UserLoginData;
import clientGUI.LoginGUIController;
import commonClasses.Message;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import ocsf.client.AbstractClient;

import java.io.IOException;

/**
 *
 *  Client connection class describes client side of project
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */

public class ClientController extends AbstractClient {
    private static ClientController clientController;
    public static boolean awaitResponse = false;
    public static Message messageFromServer;
    public static UserLoginData userLoginData;
    public static UserData userData;
    public static SavedWindows savedWindows = new SavedWindows();
    private int deliveryPrice = 30; //delivery cost

    public ClientController(String host, int port) throws IOException {
        super(host, port);
        openConnection();
        clientController = this;
    }

    /**
     * handles message from server
     * @param msg   the message sent from server
     */
    @Override
    protected void handleMessageFromServer(Object msg) {
        messageFromServer = (Message) msg;
        awaitResponse = false;
        System.out.println("received command : " + messageFromServer.getCommand() + " data: " + messageFromServer.getMsg());
    }

    /**
     * to send messages to serever
     * @param msg message to server
     */
    public void send(Object msg) {
        awaitResponse = true;
        try {
            sendToServer(msg);
        } catch (IOException e) {
            System.out.println("Could not send message to server: Terminating client." + e);
            System.exit(1);
        }
        // wait for response
        while (awaitResponse) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * send request to disconnect this client from server
     */
    public void disconnect() {
        Message msg = new Message();
        msg.setCommand("disconnect");
        if (userLoginData != null) //if user was connected
            msg.setMsg((Object) (userLoginData.getUserid()));
        send(msg);
    }

    /**
     * send request to logout for this client and create new window
     * @param stage new window to show
     */
    public void logout(Stage stage) {
        Message msg = new Message();
        msg.setCommand("logout");
        msg.setMsg((Object) (userLoginData.getUserid()));
        send(msg);
        while (ClientController.awaitResponse) ;
        stage.hide();
        Stage loginStage = savedWindows.getLoginWindow();
        LoginGUIController.loginController.initialize();
        loginStage.show();
    }

    /**
     *
     * @return client controller of project
     */
    public static ClientController getClientController() {
        return clientController;
    }

    /**
     * change color to more dark when entered button image
     * @param btn image of button
     */
    public void enteredButton(ImageView btn) {
        ColorAdjust blackout = new ColorAdjust();
        blackout.setBrightness(-0.2);
        btn.setEffect(blackout);
    }

    /**
     * restore color of exited button image
     * @param btn image to restore
     */
    public void leavedButton(ImageView btn) {
        ColorAdjust blackout = new ColorAdjust();
        blackout.setBrightness(0);
        btn.setEffect(blackout);
    }

    /**
     *
     * @return delivery price
     */
    public int getDeliveryPrice() {
        return deliveryPrice;
    }
}

