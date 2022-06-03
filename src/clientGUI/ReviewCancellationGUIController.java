package clientGUI;

import client.ClientController;
import client.NewWindowFrameController;
import commonClasses.CancellationRequest;
import commonClasses.Message;
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
    private ObservableList<CancellationRequest> refreshTable;
    private double refund=0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //*********
        // Question to yousef : do we need to ****show all the orders data here, or just the orders from specific branch that the shop manager is responsible for ????
        //        //*******
        errorLbl.setText("");
        orderIDcol.setCellValueFactory(new PropertyValueFactory<>("orderID"));
        firstNamecol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNamecol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        statuscol.setCellValueFactory(new PropertyValueFactory<>("status"));
        pricecol.setCellValueFactory(new PropertyValueFactory<>("price"));
        DeliveryDateCol.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
        requestDateCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        cancellationList = FXCollections.observableArrayList();

        CachedRowSet cachedMsg = null; //row from db with user login data

        //*********
        //1.send msg to server

        Message msg = new Message(); //msg to be sent to server
        msg.setCommand("reviewCancellation");
        msg.setMsg("aa");
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

        if (savedOrderID==0)
            stringShownInErrorLabel = "Please select order !";
        else
            stringShownInErrorLabel = String.format("Order %d is deleted", savedOrderID);

        errorLbl.setText(stringShownInErrorLabel);


        while (cc.awaitResponse) ; //wait for data from server
        if(cc.messageFromServer.getCommand().equals("refund")) {
            double refund = (double)cc.messageFromServer.getMsg();
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