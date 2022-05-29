package clientGUI;
import client.ClientController;
import clientClasses.Delivery;
import clientClasses.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DeliveryGuyGUIController {

    @FXML
    private TableView<Delivery> DeliveryMenuTable;

    @FXML
    private TableColumn<Delivery, Integer> col_orderID;

    @FXML
    private TableColumn<Delivery, String> col_customerName;

    @FXML
    private TableColumn<Delivery, String> col_address;

    @FXML
    private TableColumn<Delivery, Timestamp> col_deliveryDate;

    @FXML
    private TableColumn<Delivery, String> col_phone;

    @FXML
    private TableColumn<Delivery, Double> col_price;

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

    private ClientController conn = ClientController.getClientController();
    private String valOrder = null;

    /**
     * GUI Init
     */
    @FXML
    public void initialize() {
        //this.setUpSQL(); // for later update delivery table !!
        lbl_intro.setText("Delivery Guy " + ClientController.userLoginData.getUserid());
        lbl_errorMSG.setText("");
        lbl_userName.setText("Hello " + ClientController.userLoginData.getUsername());
        this.getTableFromDB();
    }

    /**
     * setUpSQL -
     */
    private void setUpSQL() {
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
        Stage stage = (Stage)lbl_userName.getScene().getWindow();
        conn.logout(stage);
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


    /**
     * setColumns - this function set up the columns in the table view before values are putted in
     */
    private void setColumns() {
        col_orderID.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        col_customerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        col_address.setCellValueFactory(new PropertyValueFactory<>("address"));
        col_phone.setCellValueFactory(new PropertyValueFactory<>("telephoneNumber"));
        col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        col_deliveryDate.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
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
                    msgFromServer.getString("customerName"),
                    msgFromServer.getString("customerID"),
                    msgFromServer.getString("address"),
                    msgFromServer.getDouble("price"),
                    msgFromServer.getString("telephoneNumber"),
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
        Message msg = new Message();
        int[] orderData = new int[2];
        orderData[0] = ClientController.userLoginData.getUserid();
        orderData[1] = Integer.parseInt(valOrder);
        msg.setCommand("confirmDelivery");
        msg.setMsg((Object)orderData);
        conn.send(msg);
        while(conn.awaitResponse);
        lbl_errorMSG.setText("Delivery Confirmed Successfully!");
        initialize();
    }
}
