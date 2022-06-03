package clientGUI;

import client.ClientController;
import client.NewWindowFrameController;
import clientClasses.Message;
import clientClasses.OrderToConfirm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.sql.rowset.CachedRowSet;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
/**
 * ReviewOrdersGUIController class is responsible for allowing the shop manager to
 * confirm an order made by a customer that belongs to his shop, he/she does that by
 * selecting the order and then clicking confirm order button.
 *
 * <p> Project Name: Zer-Li (Flowers store in Java) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */
public class ReviewOrdersGUIController implements Initializable {

    public ClientController cc = ClientController.getClientController();

    @FXML
    private TableView<OrderToConfirm> ordersTable;

    @FXML
    private TableColumn<OrderToConfirm, Integer> orderIDCol;

    @FXML
    private TableColumn<OrderToConfirm, String> firstNameCol;

    @FXML
    private TableColumn<OrderToConfirm, String> lastNameCol;

    @FXML
    private TableColumn<OrderToConfirm, Timestamp> deliveryDateCol;

    @FXML
    private TableColumn<OrderToConfirm, String> phoneCol;

    @FXML
    private TableColumn<OrderToConfirm, Double> priceCol;

    @FXML
    private Label errorLbl;

    @FXML
    private ImageView backBtn;

    @FXML
    private ImageView confirmBtn;


    private ObservableList<OrderToConfirm> ordersToConfirmList;

    /**
     * initialize method shows the manager orders that he needs to confirm
     * @param location
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorLbl.setText("");
        orderIDCol.setCellValueFactory(new PropertyValueFactory<>("orderID"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        deliveryDateCol.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        ordersToConfirmList = FXCollections.observableArrayList();
        CachedRowSet cachedMsg = null;
        Message msg = new Message(); //msg to be sent to server
        msg.setCommand("reviewOrdersToConfirm");
        msg.setMsg("aa");
        cc.send(msg);

        while (cc.awaitResponse) ; //wait for data from server
        cachedMsg = (CachedRowSet) (cc.messageFromServer.getMsg()); //message received from server (row from cancellationrequests table)

        if(cc.messageFromServer.getCommand().equals("tableForOrdersToConfirm")){
            try{
                while(cachedMsg.next()){
                    OrderToConfirm ordToCon = new OrderToConfirm();
                    ordToCon.setOrderID(cachedMsg.getInt("orderNumber"));
                    ordToCon.setFirstName(cachedMsg.getString("firstName"));
                    ordToCon.setLastName(cachedMsg.getString("lastName"));
                    ordToCon.setDeliveryDate(cachedMsg.getTimestamp("deliveryDate"));
                    ordToCon.setPhone(cachedMsg.getString("phonenumber"));
                    ordToCon.setPrice(cachedMsg.getDouble("price"));
                    ordersToConfirmList.add(ordToCon);
                }
            }catch(Exception e){

            }
            ordersTable.setItems(ordersToConfirmList);
        }


    }

    /**
     * clickedBackBtn method directs the manager to the previous GUI -ShopManagerGUI- when he presses the back button
     * @param event
     * @throws Exception
     */
    @FXML
    void clickedBackBtn(MouseEvent event) throws Exception {
        new NewWindowFrameController("ShopManagerGUI").start(new Stage());
        Stage stage = (Stage)errorLbl.getScene().getWindow();
        stage.hide();
    }

    /**
     * clickedConfirmBtn method is responsible for that when the manager selects an order and
     * clicks confirm button, the order will be confirmed,
     * it does so by sending the server in order to update DB, after that it deletes
     * the confirmed order from the TableView
     * @param event
     */
    @FXML
    void clickedConfirmBtn(MouseEvent event) throws SQLException {
        Message m = new Message();
        int savedOrderID = 0;
        try {
            savedOrderID = ordersTable.getSelectionModel().getSelectedItem().getOrderID();
        } catch (Exception e) {
            savedOrderID = 0;
        }
        CachedRowSet cachedMsg = null;
        m.setCommand("confirmOrder");
        m.setMsg(savedOrderID);
        cc.send(m);// send the row key (orderId)
        String stringShownInErrorLabel;

        if (savedOrderID==0)
            stringShownInErrorLabel = "Please select order !";
        else {
            stringShownInErrorLabel = String.format("Order %d is confirmed", savedOrderID);
            m.setCommand("getInfo");
            m.setMsg(savedOrderID);
            cc.send(m);// send the row key (orderId)
            while (cc.awaitResponse);
            CachedRowSet cachedMsg1 = (CachedRowSet) (cc.messageFromServer.getMsg());
    String s1="",s2="";
/*add email*/while(cachedMsg1.next()){
              s1=cachedMsg1.getString("phonenumber");
             s2=cachedMsg1.getString("email");
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setResizable(true);
            alert.setHeight(600);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(stringShownInErrorLabel);
            alert.setContentText("Confirmation message sent to customer "+ordersTable.getSelectionModel().getSelectedItem().getFirstName()+" " +ordersTable.getSelectionModel().getSelectedItem().getLastName()
                    +", phone number: "+s1+" Email : "+s2+"\nOrder :"+savedOrderID+" is confirmed by the manager.");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        }

        errorLbl.setText(stringShownInErrorLabel);
        while (cc.awaitResponse) ; //wait for data from server
            refresh();



    }
    public void refresh(){
        ObservableList<OrderToConfirm>  selected,allTable;
        allTable = this.ordersTable.getItems();
        selected = this.ordersTable.getSelectionModel().getSelectedItems();
        try {
            selected.forEach(allTable::remove);
        }catch(NoSuchElementException e){
        }
    }

    @FXML
    void enteredBackBtn(MouseEvent event) {
        cc.enteredButton(backBtn);
    }

    @FXML
    void enteredConfirmBtn(MouseEvent event) {
        cc.enteredButton(confirmBtn);
    }

    @FXML
    void leavedBackBtn(MouseEvent event) {
        cc.leavedButton(backBtn);
    }

    @FXML
    void leavedConfirmBtn(MouseEvent event) {
     cc.leavedButton(confirmBtn);
    }


}
