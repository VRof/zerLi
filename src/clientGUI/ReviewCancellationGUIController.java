package clientGUI;

import client.ClientController;
import client.NewWindowFrameController;
import clientClasses.CancellationRequest;
import clientClasses.Message;
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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

/**
 * ReviewCancellationGUIController class is responsible for allowing the shop manager to
 * confirm a cancellation request sent by a customer and updating the balance of the customer
 * if he is refunded upon to an order cancellation, the shop manager needs to select the order
 * he wants to approve its cancellation and then clicking confirm cancellation button.
 *
 * <p> Project Name: Zer-Li (Flowers store in Java) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */
public class ReviewCancellationGUIController implements Initializable {


    public ClientController cc = ClientController.getClientController();
    @FXML
    private TableView<CancellationRequest> cancellationRequests;

    @FXML
    private TableColumn<CancellationRequest, Integer> orderIDcol;

    @FXML
    private TableColumn<CancellationRequest, String> firstNamecol;

    @FXML
    private TableColumn<CancellationRequest, String> lastNamecol;

    @FXML
    private TableColumn<CancellationRequest, String> statuscol;

    @FXML
    private TableColumn<CancellationRequest, Double> pricecol;

    @FXML
    private Label errorLbl;
    @FXML
    private Label overAllRefund;

    @FXML
    private ImageView backBtn;

    @FXML
    private ImageView confirmCancellationBtn;
    @FXML
    private TableColumn<CancellationRequest, Timestamp> requestDateCol;

    @FXML
    private TableColumn<CancellationRequest, Timestamp> DeliveryDateCol;

    private ObservableList<CancellationRequest> cancellationList;
    private double refund=0;

    /**
     * initialize method shows a TableView to the manager with all the cancellation requests
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
        orderIDcol.setCellValueFactory(new PropertyValueFactory<>("orderID"));
        firstNamecol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNamecol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        statuscol.setCellValueFactory(new PropertyValueFactory<>("status"));
        pricecol.setCellValueFactory(new PropertyValueFactory<>("price"));
        DeliveryDateCol.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
        requestDateCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        cancellationList = FXCollections.observableArrayList();

        CachedRowSet cachedMsg;
        //*********
        //1.send msg to server
        Message msg = new Message(); //msg to be sent to server
        msg.setCommand("reviewCancellation");
        msg.setMsg("");
        cc.send(msg);
        //2.receive from server
        while (cc.awaitResponse) ; //wait for data from server
        cachedMsg = (CachedRowSet) (cc.messageFromServer.getMsg()); //message received from server (row from cancellationrequests table)
        //3. organize the msg from server in order to show it
        try {
            while (cachedMsg.next()) { //get user data
                CancellationRequest cnlRequest = new CancellationRequest();
                cnlRequest.setOrderID(cachedMsg.getInt("orderID"));
                cnlRequest.setFirstName(cachedMsg.getString("firstName"));
                cnlRequest.setLastName(cachedMsg.getString("lastName"));
                cnlRequest.setStatus(cachedMsg.getString("status"));
                cnlRequest.setPrice(cachedMsg.getDouble("price"));
                cnlRequest.setRequestDate(cachedMsg.getTimestamp("requestDate"));
                cnlRequest.setDeliveryDate(cachedMsg.getTimestamp("deliveryDate"));
                cancellationList.add(cnlRequest);
            }
        } catch (SQLException e) {
            System.out.println("Error read data from server " + e);
        }
        // THIS IS TO ADD THE DATA TO THE GUI
        cancellationRequests.setItems(cancellationList);
    }

    /**
     * clickedConfirmCancellationBtn method sends a message to the server - when selecting order and
     * clicking confirm cancellation button - in order to update that the order is cancelled in the DB,
     * then it shows the manager the total refund after the cancellation.
     * @param event
     */
    @FXML
    void clickedConfirmCancellationBtn(MouseEvent event) {
        Message m = new Message();
        int savedOrderID = 0;
        try {
            savedOrderID = cancellationRequests.getSelectionModel().getSelectedItem().getOrderID();
        } catch (Exception e) {
            System.out.println("null row selected");
        }
        CachedRowSet cachedMsg = null;
        m.setCommand("confirmCancellation");
        m.setMsg(savedOrderID);
        cc.send(m);// send the row key (orderId)
        String stringShownInErrorLabel;

        if (savedOrderID == 0)
            stringShownInErrorLabel = "Please select order !";
        else
            stringShownInErrorLabel = String.format("Order %d is deleted", savedOrderID);

        errorLbl.setText(stringShownInErrorLabel);


        while (cc.awaitResponse) ; //wait for data from server
        if (cc.messageFromServer.getCommand().equals("refund")) {
            double refund = (double) cc.messageFromServer.getMsg();
            overAllRefund.setText("Refunded :" + refund);
            refresh();
        }
    }
    public void refresh(){
       ObservableList<CancellationRequest>  selected,allTable;
        allTable = this.cancellationRequests.getItems();
        selected = this.cancellationRequests.getSelectionModel().getSelectedItems();
        try {
            selected.forEach(allTable::remove);
        }catch(NoSuchElementException e){
        }
    }

    /**
     * clickedBackBtn method directs the manager to the previous GUI - ShopManagerGUI - when clicking back button.
     * @param mouseEvent
     * @throws Exception
     */
    public void clickedBackBtn(MouseEvent mouseEvent) throws Exception {
        new NewWindowFrameController("ShopManagerGUI").start(new Stage());
        Stage stage = (Stage) errorLbl.getScene().getWindow();
        stage.hide();
    }

    public void enteredBackBtn(MouseEvent mouseEvent) {
        cc.enteredButton(backBtn);
    }

    public void leavedBackBtn(MouseEvent mouseEvent) {
        cc.leavedButton(backBtn);
    }

    public void enteredConfirmCancellationBtn(MouseEvent mouseEvent) {
        cc.enteredButton(confirmCancellationBtn);
    }

    public void leavedConfirmCancellationBtn(MouseEvent mouseEvent) {
        cc.leavedButton(confirmCancellationBtn);
    }

}