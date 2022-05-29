package clientGUI;

import client.ClientController;
import client.NewWindowFrameController;
import clientClasses.Message;
import clientClasses.OrderToConfirm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.sql.rowset.CachedRowSet;
import java.net.URL;
import java.sql.Timestamp;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

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

    @FXML
    void clickedBackBtn(MouseEvent event) throws Exception {
        new NewWindowFrameController("ShopManagerGUI").start(new Stage());
        Stage stage = (Stage)errorLbl.getScene().getWindow();
        stage.hide();
    }

    @FXML
    void clickedConfirmBtn(MouseEvent event) {
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
        else
            stringShownInErrorLabel = String.format("Order %d is confirmed", savedOrderID);

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
}
