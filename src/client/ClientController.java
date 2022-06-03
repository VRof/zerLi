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


    @Override
    protected void handleMessageFromServer(Object msg) {
        messageFromServer = (Message) msg;
        awaitResponse = false;
        System.out.println("received command : " + messageFromServer.getCommand() + " data: " + messageFromServer.getMsg());
//        String msgFromServer = ((Message) msg).getCommand();
//        if (msgFromServer.equals("newPopUpMessage")) {
//            messageFromServer = (Message)msg;
//            Platform.runLater(new Runnable() {
//                @Override
//                public void run() {
//                    NewPopUpWindowFrameController popupmsg = new NewPopUpWindowFrameController("PopUpMessage");
//                    try {
//                        popupmsg.start(new Stage());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        } else {

 //       }
    }


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

    public void disconnect() {
        Message msg = new Message();
        msg.setCommand("disconnect");
        if (userLoginData != null) //if user was connected
            msg.setMsg((Object) (userLoginData.getUserid()));
        send(msg);
    }

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

    public static ClientController getClientController() {
        return clientController;
    }

    public void enteredButton(ImageView btn) {
        ColorAdjust blackout = new ColorAdjust();
        blackout.setBrightness(-0.2);
        btn.setEffect(blackout);
    }

    public void leavedButton(ImageView btn) {
        ColorAdjust blackout = new ColorAdjust();
        blackout.setBrightness(0);
        btn.setEffect(blackout);
    }

    public int getDeliveryPrice() {
        return deliveryPrice;
    }
}

