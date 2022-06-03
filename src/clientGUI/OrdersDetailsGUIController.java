package clientGUI;

import client.ClientController;
import client.NewWindowFrameController;
import commonClasses.Message;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrdersDetailsGUIController {

    @FXML
    private ImageView btn_back;

    @FXML
    private TextArea txt_blessing;

    @FXML
    private ComboBox<String> cBox_shop;

    @FXML
    private DatePicker date_deliveryDate;

    @FXML
    private ComboBox<String> cBox_hour;

    @FXML
    private ComboBox<String> cBox_minutes;

    @FXML
    private TextArea txt_order;

    @FXML
    private Label lbl_orderPrice;

    @FXML
    private TextField txt_address;

    @FXML
    private TextField txt_phoneNumeber;

    @FXML
    private ComboBox<String> cBox_deliveryMethod;

    @FXML
    private TextField txt_fullName;

    @FXML
    private ImageView btn_proceedWithThePayment;

    @FXML
    private Label lbl_msg;

    private String greetingCard = "";
    private String shop = "";
    private String phoneNumber = "";
    private Timestamp deliveryDate;
    private String pendingForText = "";
    private String orderPlusDeliveryDetails;
    public static OrdersDetailsGUIController controller;
    private String deliveryAndPrice = ""; //string with delivery price in it "delivery(30₪)"
    private double finalPrice;

    @FXML
    public void initialize() throws SQLException {
        controller = this;
        printOrderWithDeliveryDetails();
        lbl_msg.setText("");
        Message msgToServer = new Message();
        List<String> shopsList = new ArrayList<>();
        msgToServer.setCommand("getAllTheShops");
        ClientController.getClientController().send(msgToServer);
        CachedRowSet cachedRowSet = null;
        if (ClientController.messageFromServer != null)
            cachedRowSet = (CachedRowSet) ClientController.messageFromServer.getMsg();
        else
            lbl_msg.setText("error getting shops list from server");
        while (cachedRowSet.next()) {
            shopsList.add(cachedRowSet.getString("shop"));
        }
        deliveryAndPrice = "delivery(" + ClientController.getClientController().getDeliveryPrice() + "₪)";
        String[] deliveryMethods = {"pick-up", deliveryAndPrice};
        String[] hours = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
        String[] minutes = {"00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"};
        cBox_deliveryMethod.getItems().addAll(deliveryMethods);
        cBox_hour.getItems().addAll(FXCollections.observableArrayList(hours));
        cBox_minutes.getItems().addAll(FXCollections.observableArrayList(minutes));
        cBox_shop.getItems().addAll(FXCollections.observableArrayList(shopsList));
    }

    public void printOrderWithDeliveryDetails() {
        String order = NewOrderGUIController.controller.getFullOrder();
        orderPlusDeliveryDetails = "";
        finalPrice = NewOrderGUIController.controller.getFullPrice(); //return delivery cost
        if (cBox_deliveryMethod.getValue() != null){
            if (cBox_deliveryMethod.getValue().equals("pick-up")) {
                finalPrice = NewOrderGUIController.controller.getFullPrice(); //return delivery cost
                pendingForText = "pending for pickup";
            } else if (cBox_deliveryMethod.getValue().equals(deliveryAndPrice)) {
                pendingForText = "pending for delivery";
                finalPrice = NewOrderGUIController.controller.getFullPrice() + ClientController.getClientController().getDeliveryPrice(); //add delivery cost
            }
            orderPlusDeliveryDetails = orderPlusDeliveryDetails + "Delivery method: " + cBox_deliveryMethod.getValue() + "\n";
        }
        if (!txt_fullName.getText().equals(""))
            orderPlusDeliveryDetails = orderPlusDeliveryDetails + "Full name: " + txt_fullName.getText() + "\n";
        if (!txt_address.getText().equals(""))
            orderPlusDeliveryDetails = orderPlusDeliveryDetails + "Delivery address: " + txt_address.getText() + "\n";
        if (!phoneNumber.equals(""))
            orderPlusDeliveryDetails = orderPlusDeliveryDetails + "Phone: " + txt_phoneNumeber.getText() + "\n";
        if (date_deliveryDate.getValue() != null && cBox_hour.getValue() != null && cBox_minutes.getValue() != null) {
            //---------------------------create timestamp to write in db:
            int year = date_deliveryDate.getValue().getYear();
            int month = date_deliveryDate.getValue().getMonthValue();
            if (month == 12)
                month = 0; //convert to timestamp month (0-11)
            int day = date_deliveryDate.getValue().getDayOfMonth();
            int hour = Integer.parseInt(cBox_hour.getValue());
            int minute = Integer.parseInt(cBox_minutes.getValue());
            String myDate = year + "/" + month + "/" + day + " " + hour + ":" + minute + ":" + "00";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = null;
            try {
                date = sdf.parse(myDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long millis = date.getTime();
            deliveryDate = new Timestamp(millis);
            //------------------------------------------------------
            orderPlusDeliveryDetails = orderPlusDeliveryDetails + "Delivery date: " + date_deliveryDate.getValue() + " " + cBox_hour.getValue() + ":" + cBox_minutes.getValue() + "\n";
        }
        if (cBox_shop.getValue() != null)
            orderPlusDeliveryDetails = orderPlusDeliveryDetails + "Shop: " + cBox_shop.getValue() + "\n";
        if (!txt_blessing.getText().equals(""))
            orderPlusDeliveryDetails = orderPlusDeliveryDetails + "Blessing message: " + txt_blessing.getText() + "\n";
        orderPlusDeliveryDetails = order + "\n" + orderPlusDeliveryDetails;
        finalPrice = finalPrice*100;
        finalPrice = Math.round(finalPrice);
        finalPrice = finalPrice /100;
        orderPlusDeliveryDetails = orderPlusDeliveryDetails + "-----------------------------\n" + "Price include delivery: " + finalPrice + "₪";
        txt_order.setText(orderPlusDeliveryDetails);
        lbl_orderPrice.setText("Order price: " + finalPrice + "₪");
    }

    public String getOrderPlusDeliveryDetails() {
        return orderPlusDeliveryDetails;
    }

    public String getGreetingCard() {
        return greetingCard;
    }

    public String getShop() {
        return shop;
    }

    public Timestamp getDeliveryDate() {
        return deliveryDate;
    }

    public String getPendingForText() {
        return pendingForText;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    @FXML
    void changedFullname(KeyEvent event) {
        printOrderWithDeliveryDetails();
    }

    @FXML
    void changedBlessing(KeyEvent event) {
        greetingCard = txt_blessing.getText();
        printOrderWithDeliveryDetails();
    }

    @FXML
    void changedDeliveryAddress(KeyEvent event) {
        printOrderWithDeliveryDetails();
    }

    @FXML
    void changedPhoneNumber(KeyEvent event) {
        lbl_msg.setText("");
        phoneNumber = "";
        if (!event.getCharacter().matches("[0-9]+")) {
            lbl_msg.setText("Phone number must include only digits");
        } else if (txt_phoneNumeber.getText().length() != 10)
            lbl_msg.setText("Phone number must be 10-digit number");
        else {
            phoneNumber = txt_phoneNumeber.getText();
        }
        printOrderWithDeliveryDetails();
    }

    @FXML
    void selectedDate(ActionEvent event) {
        printOrderWithDeliveryDetails();
    }

    @FXML
    void selectedDeliveryMethod(ActionEvent event) {
        if(cBox_deliveryMethod.getValue().equals("pick-up")){
            txt_address.setDisable(true);
            txt_address.setText("");
        }
        else{
            txt_address.setDisable(false);
        }
        printOrderWithDeliveryDetails();
    }

    @FXML
    void selectedHour(ActionEvent event) {
        printOrderWithDeliveryDetails();
    }

    @FXML
    void selectedMinutes(ActionEvent event) {
        printOrderWithDeliveryDetails();
    }

    @FXML
    void selectedShop(ActionEvent event) {
        printOrderWithDeliveryDetails();
        shop = cBox_shop.getValue();
    }

    @FXML
    void clickedBackBtn(MouseEvent event) {
        Stage thisWindow = (Stage) btn_back.getScene().getWindow();
        ClientController.savedWindows.setOrderDetailsWindow(thisWindow);
        thisWindow.hide();
        ClientController.savedWindows.getNewOrderWindow().show();
    }

    @FXML
    void clickedProceedWithThePayment(MouseEvent event) throws Exception {
        lbl_msg.setText("");
        if (cBox_deliveryMethod.getValue() == null) {
            lbl_msg.setText("please choose delivery method");
            return;
        }
        if (txt_fullName.getText().equals("")) {
            lbl_msg.setText("please enter full name of addressee");
            return;
        }
        if (!cBox_deliveryMethod.getValue().equals("pick-up") && txt_address.getText().equals("")) {
            lbl_msg.setText("please enter delivery address");
            return;
        }
        if (phoneNumber.equals("")) {
            lbl_msg.setText("please enter correct 10-digit mobile number");
            return;
        }
        if (!cBox_deliveryMethod.getValue().equals("pick-up") && (date_deliveryDate.getValue() == null || cBox_hour.getValue() == null || cBox_minutes.getValue() == null)) {
            lbl_msg.setText("please select date and time for delivery");
            return;
        }
        if (!cBox_deliveryMethod.getValue().equals("pick-up") && date_deliveryDate.getValue() != null && cBox_hour.getValue() != null && cBox_minutes.getValue() != null) {
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            if (deliveryDate.getTime() < (currentTime.getTime() + 10810000)) {
                lbl_msg.setText("please select correct date and time for delivery,it must be at least 3 hours from now");
                return;
            }
        }
        if (shop.equals("")) {
            lbl_msg.setText("please select shop to order from");
            return;
        }
        Stage thisScene = (Stage) btn_back.getScene().getWindow();
        ClientController.savedWindows.setOrderDetailsWindow(thisScene);
        thisScene.hide();
        if (ClientController.savedWindows.getPaymentWindow() != null)
            ClientController.savedWindows.getPaymentWindow().show();
        else {
            NewWindowFrameController paymentWindow = new NewWindowFrameController("PaymentGUI");
            paymentWindow.start(new Stage());
        }
    }

    @FXML
    void enteredBtnBack(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_back);
    }

    @FXML
    void enteredProceedWithThePayment(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_proceedWithThePayment);

    }

    @FXML
    void leavedBtnBack(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_back);
    }

    @FXML
    void leavedProceedWithThePayment(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_proceedWithThePayment);
    }

}
