package clientGUI;

import client.ClientController;
import commonClasses.Message;
import commonClasses.Order;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class PaymentGUIController {

    @FXML
    private ImageView btn_pay;

    @FXML
    private CheckBox checkBox_savedCard;

    @FXML
    private TextField txt_firstName;

    @FXML
    private TextField txt_lastName;

    @FXML
    private TextField txt_cardNumber;

    @FXML
    private TextField txt_cvv;

    @FXML
    private DatePicker date_expireDate;

    @FXML
    private Label lbl_price;

    @FXML
    private Label lbl_balance;

    @FXML
    private ImageView btn_useBalance;

    @FXML
    private TextArea txt_order;

    @FXML
    private ImageView btn_back;

    @FXML
    private Label lbl_msg;

    private String finalOrderData = "";
    private double priceToPay;
    private Date expireDate = new Date();
    private String cardNumber = "";
    private String cvv = "";
    private boolean balanceUsed = false;
    private boolean cardNeeded = true;

    private String newFirstName = "";
    private String newLastName = "";
    private String newCardNumber = "";
    private Date newExpiredDate;
    private String newCvv = "";
    double newBalance;

    @FXML
    public void initialize() throws SQLException {
        lbl_msg.setText("");
        finalOrderData = OrdersDetailsGUIController.controller.getOrderPlusDeliveryDetails();
        priceToPay = OrdersDetailsGUIController.controller.getFinalPrice();
        newBalance = ClientController.userData.getBalance();
        txt_order.setText(finalOrderData);
        lbl_price.setText("Order price: " + priceToPay + "₪");
        lbl_balance.setText("Your balance: " + ClientController.userData.getBalance() + "₪");
        checkBox_savedCard.setSelected(true);
        txt_firstName.setText(ClientController.userData.getFirstname());
        txt_firstName.setDisable(true);
        txt_lastName.setText(ClientController.userData.getLastname());
        txt_lastName.setDisable(true);
        setCreditCardData();
        txt_cardNumber.setText(cardNumber);
        txt_cardNumber.setDisable(true);
        txt_cvv.setText(cvv);
        txt_cvv.setDisable(true);
        LocalDate date = LocalDate.of(expireDate.getYear() + 1900, expireDate.getMonth() + 1, expireDate.getDate());
        date_expireDate.setValue(date);
        date_expireDate.setDisable(true);
        printOrder();
    }

    private void setCreditCardData() throws SQLException {
        Message msgToServer = new Message();
        msgToServer.setCommand("getCreditCardData");
        msgToServer.setMsg(ClientController.userLoginData.getUserid());
        ClientController.getClientController().send(msgToServer);
        CachedRowSet cachedRowSet = (CachedRowSet) ClientController.messageFromServer.getMsg();
        while (cachedRowSet.next()) {
            expireDate = cachedRowSet.getDate("creditCardExpiryDate");
            cardNumber = cachedRowSet.getString("creditCardNumber");
            cvv = cachedRowSet.getString("creditCardCVV");
        }
    }

    private void printOrder() {
        finalOrderData = OrdersDetailsGUIController.controller.getOrderPlusDeliveryDetails();
        finalOrderData = finalOrderData + "\n\nPayment method:\n";
        if (cardNeeded && checkBox_savedCard.isSelected()) {
            finalOrderData += "Name: " + ClientController.userData.getFirstname() + " " + ClientController.userData.getLastname() + "\n";
            finalOrderData += "Credit card: " + "************" + cardNumber.substring(12) + "\n";
        } else if (cardNeeded && !checkBox_savedCard.isSelected()) {
            if (!newFirstName.equals("") && !newLastName.equals(""))
                finalOrderData += "Name: " + newFirstName + " " + newLastName + "\n";
            if (!newCardNumber.equals(""))
                finalOrderData += "Credit card number: " + "************" + newCardNumber.substring(12) + "\n";
        }
        if (balanceUsed) {
            double diff = ClientController.userData.getBalance() - newBalance;
            finalOrderData += "Will be paid from balance: " + diff + "₪" + "\n";
        }
        priceToPay = priceToPay*100;
        priceToPay = Math.round(priceToPay);
        priceToPay = priceToPay /100;
        finalOrderData += "---------------------------\n" + "Final price: " + priceToPay + "₪" + "\n";
        txt_order.setText(finalOrderData);
        lbl_price.setText("Order price: " + priceToPay + "₪");
        lbl_balance.setText("Your balance: " + newBalance + "₪");

    }

    @FXML
    void changedCardNumber(KeyEvent event) {
        lbl_msg.setText("");
        newCardNumber = "";
        if (cardNeeded && !event.getCharacter().matches("[0-9]+")) {
            lbl_msg.setText("Card number must include only digits");
        } else if (cardNeeded && txt_cardNumber.getText().length() != 16)
            lbl_msg.setText("Card number must be 16-digit number");
        else {
            newCardNumber = txt_cardNumber.getText();
        }
        printOrder();
    }

    @FXML
    void changedCvv(KeyEvent event) {
        lbl_msg.setText("");
        newCvv = "";
        if (cardNeeded && !event.getCharacter().matches("[0-9]+")) {
            lbl_msg.setText("CVV must include only digits");
        } else if (cardNeeded && txt_cvv.getText().length() != 3)
            lbl_msg.setText("CVV must be 3-digit number");
        else {
            newCvv = txt_cvv.getText();
        }
        printOrder();
    }

    @FXML
    void changedExpireDate(ActionEvent event) {
        LocalDate date = date_expireDate.getValue();
        newExpiredDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        printOrder();
    }

    @FXML
    void changedFirstName(KeyEvent event) {
        lbl_msg.setText("");
        newFirstName = txt_firstName.getText();
        if (cardNeeded && newFirstName.equals(""))
            lbl_msg.setText("Please enter first name");
        printOrder();
    }

    @FXML
    void changedLastName(KeyEvent event) {
        lbl_msg.setText("");
        newLastName = txt_lastName.getText();
        if (cardNeeded && newLastName.equals(""))
            lbl_msg.setText("Please enter last name");
        printOrder();
    }

    @FXML
    void changedSavedCard(ActionEvent event) {
        if (cardNeeded && checkBox_savedCard.isSelected()) {
            txt_firstName.setText(ClientController.userData.getFirstname());
            txt_firstName.setDisable(true);
            txt_lastName.setText(ClientController.userData.getLastname());
            txt_lastName.setDisable(true);
            txt_cardNumber.setText(cardNumber);
            txt_cardNumber.setDisable(true);
            txt_cvv.setText(cvv);
            txt_cvv.setDisable(true);
            LocalDate date = LocalDate.of(expireDate.getYear() + 1900, expireDate.getMonth() + 1, expireDate.getDate());
            date_expireDate.setValue(date);
            date_expireDate.setDisable(true);
        } else if(cardNeeded && !checkBox_savedCard.isSelected()) {
            newFirstName = "";
            txt_firstName.setText("");
            txt_firstName.setDisable(false);
            newLastName = "";
            txt_lastName.setText("");
            txt_lastName.setDisable(false);
            newCardNumber = "";
            txt_cardNumber.setText("");
            txt_cardNumber.setDisable(false);
            newCvv = "";
            txt_cvv.setText("");
            txt_cvv.setDisable(false);
            date_expireDate.setDisable(false);
        }
        printOrder();
    }

    @FXML
    void clickedBackBtn(MouseEvent event) {
        Stage thisStage = (Stage) lbl_price.getScene().getWindow();
        ClientController.savedWindows.getOrderDetailsWindow().show();
        thisStage.hide();
    }

    @FXML
    void clickedPay(MouseEvent event) {
        lbl_msg.setText("");
        if (cardNeeded && checkBox_savedCard.isSelected()) {
            if (txt_firstName.getText().equals("")) {
                lbl_msg.setText("First name field is empty");
                return;
            }
            if (txt_lastName.getText().equals("")) {
                lbl_msg.setText("Last name field is empty");
                return;
            }
            if (cardNumber.equals("") || cardNumber.length() < 16) {
                lbl_msg.setText("Card number field is empty or doesn't have 16 digits");
                return;
            }

            if (expireDate == null) {
                lbl_msg.setText("Card expire date is empty");
                return;
            }
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            if(expireDate.getTime() < currentTime.getTime()){
                lbl_msg.setText("This card is expired");
                return;
            }
            if (cvv.equals("") || cvv.length() < 3) {
                lbl_msg.setText("Card CVV field is empty or less than 3 digits");
                return;
            }
        } else if (cardNeeded && !checkBox_savedCard.isSelected()) {
            if (newFirstName.equals("")) {
                lbl_msg.setText("First name field is empty");
                return;
            }
            if (newLastName.equals("")) {
                lbl_msg.setText("Last name field is empty");
                return;
            }
            if (newCardNumber.equals("") || newCardNumber.length() < 16) {
                lbl_msg.setText("Card number field is empty or doesn't have 16 digits");
                return;
            }
            if (newExpiredDate == null) {
                lbl_msg.setText("Card expire date is empty");
                return;
            }
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            if(newExpiredDate.getTime() < currentTime.getTime()){
                lbl_msg.setText("This card is expired");
                return;
            }
            if (newCvv.equals("") || newCvv.length() < 3) {
                lbl_msg.setText("Card CVV field is empty or less than 3 digits");
                return;
            }
        }
        Timestamp currTime = new Timestamp(System.currentTimeMillis());
        Order order = new Order(0,priceToPay,OrdersDetailsGUIController.controller.getGreetingCard(),
                finalOrderData,OrdersDetailsGUIController.controller.getShop(),
                currTime,OrdersDetailsGUIController.controller.getDeliveryDate(),
                OrdersDetailsGUIController.controller.getPendingForText(),
                "no");
        order.setCustomerid(ClientController.userLoginData.getUserid());
        order.setNewBalance(newBalance);
        Message orderMsg = new Message();
        orderMsg.setCommand("addNewOrder");
        orderMsg.setMsg(order);
        ClientController.getClientController().send(orderMsg);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
            @Override
            public void handle(DialogEvent event) {
                Stage thisStage = (Stage)btn_back.getScene().getWindow();
                Stage customerWindow = ClientController.savedWindows.getClientGUIWindow();
                ClientController.savedWindows.setPaymentWindow(null);
                ClientController.savedWindows.setOrderDetailsWindow(null);
                ClientController.savedWindows.setNewOrderWindow(null);
                thisStage.hide();
                customerWindow.show();
                CustomerGUIController.controller.initialize(); //refresh userdata
            }
        });
        if(ClientController.messageFromServer.getCommand().equals("error creating order")){
            alert.setTitle("Oops...");
            alert.setHeaderText("Something went wrong...");
            alert.setContentText("Sorry and please try again later");
            alert.show();
        }
        else {
            alert.setTitle("Order completed");
            alert.setHeaderText("Order created successfully");
            alert.setContentText("Thank you");
            ClientController.userData.setBalance(newBalance); //after new balance was written in db, change it for connected customer
            alert.show();
        }
    }

    @FXML
    void clickedUseBalance(MouseEvent event) {
        lbl_msg.setText("");
        if (balanceUsed) return;
        newBalance = ClientController.userData.getBalance();
        if (newBalance > 0) {
            if (newBalance > priceToPay) {
                newBalance = newBalance - priceToPay;
                priceToPay = 0;
                cardNeeded = false;
                txt_firstName.setText("");
                txt_lastName.setText("");
                txt_cvv.setText("");
                txt_cardNumber.setText("");
                date_expireDate.setValue(null);
                txt_firstName.setDisable(true);
                txt_lastName.setDisable(true);
                txt_cardNumber.setDisable(true);
                txt_cvv.setDisable(true);
                date_expireDate.setDisable(true);
            } else {
                priceToPay = priceToPay - newBalance;
                newBalance = 0;
            }
            balanceUsed = true;
        } else {
            lbl_msg.setText("Your balance is 0");
        }
        printOrder();
    }

    @FXML
    void enteredBackBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_back);
    }

    @FXML
    void enteredPay(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_pay);
    }

    @FXML
    void enteredUseBalance(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_useBalance);
    }

    @FXML
    void leavedBackBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_back);
    }

    @FXML
    void leavedPay(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_pay);
    }

    @FXML
    void leavedUseBalance(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_useBalance);
    }

}
