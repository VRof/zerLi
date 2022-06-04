package clientGUI;

import client.ClientController;
import clientClasses.Delivery;
import commonClasses.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 *
 *  Delivery Guy Controller - made for users from type "delivery Guy",
 *  the controller include a big table with all order for specific user id
 *  User can confirm delivery in using the table, if there is an error, a suitable message will be shown.
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */
public class DeliveryGuyGUIController {

    @FXML
    private TableView<Delivery> DeliveryMenuTable;

    @FXML
    private TableColumn<Delivery, Integer> col_orderID;

    @FXML
    private TableColumn<Delivery,Float> col_price;

    @FXML
    private TableColumn<Delivery,Timestamp> col_deliveryDate;
    @FXML
    private TableColumn<?, ?> col_shop;

    @FXML
    private Label lbl_userName;

    @FXML
    private Label lbl_errorMSG;

    @FXML
    private ImageView btn_logout;

    @FXML
    private Label lbl_intro;

    @FXML
    private ImageView btn_confirmDelivery;

    @FXML
    private ImageView btn_orderDetails;

    private ClientController conn = ClientController.getClientController();
    private String valOrder = null;
    private int x=1;

    /**
     * GUI Init
     */
    @FXML
    public void initialize() {
        if (x==1)
        this.setUpSQL(); // for later update delivery table !!
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
        lbl_intro.setText("Delivery Guy " + ClientController.userLoginData.getUserid());
        lbl_errorMSG.setText("");
        this.getTableFromDB();
    }

    /**
     * setUpSQL -
     */
    private void setUpSQL() {
        x=0;
        Message msg = new Message();
        msg.setCommand("setUpSQL");
        msg.setMsg(ClientController.userLoginData.getUserid());
        conn.send(msg);
        while(conn.awaitResponse);
    }

    /**
     * clickedConfirmDeliveryBtn - click on button to confirm delivery in table view
     * @param event - mouse click
     */
    @FXML
    void clickedConfirmDeliveryBtn(MouseEvent event) {
            try{ confirmDelivery(); lbl_errorMSG.setText("Delivery Confirmed");}
            catch (Exception e) { lbl_errorMSG.setText("Select order to confirm"); }
    }

    /**
     * clickedLogoutBtn - user click on this button to log out and then return to log in window
     * @param event - mouse click
     * @throws Exception - if error while creating new window
     */
    @FXML
    void clickedLogoutBtn(MouseEvent event) {
        Message msg = new Message();
        msg.setCommand("initTableForDelivery");
        msg.setMsg(ClientController.userLoginData.getUserid());
        conn.send(msg);
        while(conn.awaitResponse);
        Stage stage = (Stage)lbl_userName.getScene().getWindow();
        conn.logout(stage);
    }

    /**
     * clickedOrderDetailsBtn - show order details
     * @param event - mouse click
     */
    @FXML
    void clickedOrderDetailsBtn(MouseEvent event) {
        Message msg = new Message() ;
        int val;
        Alert alert;
        try{
            val = Integer.parseInt(valOrder);
            alert= new Alert(Alert.AlertType.INFORMATION);
        msg.setCommand("getOrderDetails");
        msg.setMsg(val);
        conn.send(msg);
        CachedRowSet msgFromServer;
        String details = "";
        while(conn.awaitResponse);
        msgFromServer= (CachedRowSet) (conn.messageFromServer.getMsg());
        try { while (msgFromServer.next()) { details = msgFromServer.getString("dOrder");}}
        catch (SQLException e) { System.out.println("Error read data from server " + e);}
        alert.setTitle("Order Details");
        alert.setHeaderText("Order Number " + valOrder);
        alert.setContentText(details);
        alert.showAndWait().ifPresent(rs -> { if (rs == ButtonType.OK) { System.out.println("deliver Confirmed"); }});}
        catch (Exception e) { lbl_errorMSG.setText("Select order to confirm"); }
    }

    /**
     * enteredConfirmDeliveryBtn - function for making shadow for button
     * @param event - entering the button with the mouse
     */
    @FXML
    void enteredConfirmDeliveryBtn(MouseEvent event) { conn.enteredButton(btn_confirmDelivery);}

    /**
     * enteredLogoutBtn - function for making shadow for button
     * @param event - entering the button with the mouse
     */
    @FXML
    void enteredLogoutBtn(MouseEvent event) { conn.enteredButton(btn_logout);}

    @FXML
    void enteredOrderDetailsBtn(MouseEvent event) {conn.enteredButton(btn_orderDetails); }


    /**
     * leavedConfirmDeliveryBtn - function ti hide shadow for button
     * @param event - leaving the button with the mouse
     */
    @FXML
    void leavedConfirmDeliveryBtn(MouseEvent event) { conn.leavedButton(btn_confirmDelivery);}

    /**
     * leavedLogoutBtn - function to hide shadow for button
     * @param event - leaving the button with the mouse
     */
    @FXML
    void leavedLogoutBtn(MouseEvent event) { conn.leavedButton(btn_logout);}

    @FXML
    void leavedOrderDetailsBtn(MouseEvent event) {conn.leavedButton(btn_orderDetails); }


    /**
     * setColumns - this function set up the columns in the table view before values are putted in
     */
    private void setColumns() {
        col_orderID.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        col_deliveryDate.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
        col_shop.setCellValueFactory(new PropertyValueFactory<>("Shop"));
        DeliveryMenuTable.setOnMouseClicked(click -> {
            try{
            if (click.getClickCount() == 1) {
                @SuppressWarnings("rawtypes")
                TablePosition pos = DeliveryMenuTable.getSelectionModel().getSelectedCells().get(0); //TablePosition
                int row = pos.getRow();
                valOrder = String.valueOf(col_orderID.getCellData(row));}}
                catch (Exception e){ lbl_errorMSG.setText("Select order to confirm");}}); // get value of row in table and value of delivery number
    }

    /**
     *  getTableFromDB - this function calls "setColumns' to set up the columns
     *  then sends Message to server to ask for data from server
     *  it takes the values from cashed MSG from server to put them in table view
     */
    private void getTableFromDB() {
        this.setColumns();
        Message msg = new Message();
        CachedRowSet msgFromServer;
        ObservableList<Delivery> deliverylist = FXCollections.observableArrayList();
        msg.setCommand("deliveryOrders");
        msg.setMsg(ClientController.userLoginData.getUserid());
        conn.send(msg);
        while(conn.awaitResponse);
        msgFromServer = (CachedRowSet)(conn.messageFromServer.getMsg());
        try {
            while (msgFromServer.next()) {
                    Delivery delivery =  new Delivery(
                    msgFromServer.getInt("deliveryGuyID"),
                    msgFromServer.getInt("orderNumber"),
                            msgFromServer.getDouble("price"),
                    msgFromServer.getString("shop"),
                    msgFromServer.getTimestamp("date"),
                    msgFromServer.getString("confirmed"));
                deliverylist.add(delivery);
            }}
        catch (SQLException e) { System.out.println("Error read data from server " + e);}
        DeliveryMenuTable.setItems(deliverylist);
    }

    /**
     * confirmDelivery - function to confirm delivery in DB when Button Pressed
     * Asking server to change some data In DB
     */
    private void confirmDelivery() {
        Message msg = new Message();  Alert alert = new Alert(Alert.AlertType.INFORMATION);
        int[] orderData = new int[2];
        CachedRowSet msgFromServer;
        String userphnum = "",mail= "",fname="",lname="";
        orderData[0] = ClientController.userLoginData.getUserid();
        orderData[1] = Integer.parseInt(valOrder);
        msg.setCommand("confirmDelivery");
        msg.setMsg((Object)orderData);
        conn.send(msg);
        while(conn.awaitResponse);
        msgFromServer= (CachedRowSet) (conn.messageFromServer.getMsg());
        try { while (msgFromServer.next()) {
                fname = msgFromServer.getString("firstname");
                lname = msgFromServer.getString("lastname");
                userphnum = msgFromServer.getString("phonenumber");
                mail = msgFromServer.getString("email");
            }}
        catch (SQLException e) { System.out.println("Error read data from server " + e);}
        alert.setTitle("Confirmation Details");
        alert.setHeaderText("Delivery Confirmed!");
        fname =fname +" "+lname;
        alert.setContentText("Confirmation message has been sent to user \n" +"User Details : "+fname+"\nPhone number: "+ userphnum +"\nEmail: "+mail );
        alert.showAndWait().ifPresent(rs -> { if (rs == ButtonType.OK) { System.out.println("deliver Confirmed"); }});
        lbl_errorMSG.setText("Delivery Confirmed Successfully!");
        initialize();
    }
}
