package clientGUI;

import client.ClientController;
import client.NewWindowFrameController;
import clientClasses.Customer;
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
import java.util.ResourceBundle;

public class ManageCustomersGUIController implements Initializable {
    public ClientController cc = ClientController.getClientController();
    @FXML
    private ImageView backBtn;

    @FXML
    private TableColumn<Customer, String> emailCol;

    @FXML
    private Label errorLbl;

    @FXML
    private TableColumn<Customer, String> firstNameCol;

    @FXML
    private ImageView freezeBtn;

    @FXML
    private TableColumn<Customer, String> lastNameCol;

    @FXML
    private TableView<Customer> manageCustomers;

    @FXML
    private TableColumn<Customer, Double> balanceCol;
    @FXML
    private TableColumn<Customer, String> statusCol;

    @FXML
    private TableColumn<Customer, String> telNumCol;

    @FXML
    private ImageView unfreezeBtn;

    @FXML
    private TableColumn<Customer, Integer> userIDCol;

    private ObservableList<Customer> customersList;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userIDCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        telNumCol.setCellValueFactory(new PropertyValueFactory<>("telNum"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        customersList = FXCollections.observableArrayList();
        CachedRowSet cachedMsg = null;

        //***
        //1.send msg to server

        Message msg = new Message(); //msg to be sent to server
        msg.setCommand("manageCustomers");
        msg.setMsg("aa");
        cc.send(msg);

        //2.receive from server


        while (cc.awaitResponse) ; //wait for data from server
        cachedMsg = (CachedRowSet) (cc.messageFromServer.getMsg()); //message received from server (row from cancellationrequests table)

        //3. organize the msg from server in order to show it
        try {
            while (cachedMsg.next()) { //get user data
                Customer customer = new Customer();
                customer.setUserId(cachedMsg.getInt("userid"));
                customer.setFirstName(cachedMsg.getString("firstname"));
                customer.setLastName(cachedMsg.getString("lastname"));
                customer.setTelNum(cachedMsg.getString("phonenumber"));
                customer.setEmail(cachedMsg.getString("email"));
                customer.setBalance(cachedMsg.getDouble("balance"));
                customer.setStatus(cachedMsg.getString("status"));
                customersList.add(customer);
            }
        } catch (SQLException e) {
            System.out.println("Error read data from server " + e);
        }


        // THIS IS TO ADD THE DATA TO THE GUI

        manageCustomers.setItems(customersList);


    }





    @FXML
    void clickedBackBtn(MouseEvent event) throws Exception {
        new NewWindowFrameController("ShopManagerGUI").start(new Stage());
        Stage stage = (Stage) errorLbl.getScene().getWindow();
        stage.hide();
    }

    @FXML
    void clickedFreezeBtn(MouseEvent event) {
        Message m = new Message();
        int savedUserID = 0; String status="";
        try {
            savedUserID = manageCustomers.getSelectionModel().getSelectedItem().getUserId();
            status = manageCustomers.getSelectionModel().getSelectedItem().getStatus();
        } catch (Exception e) {
            System.out.println("null row selected");
        }
        m.setCommand("confirmFreeze");
        m.setMsg(savedUserID);
        cc.send(m);// send the row key (userid)
        String stringShownInErrorLabel;
        if (savedUserID==0)
            stringShownInErrorLabel = "Please select customer !";
        else if(status.equals("frozen"))
            stringShownInErrorLabel="Selected customer is already frozen!";
        else
            stringShownInErrorLabel = String.format("Customer with id = %d is frozen", savedUserID);
        errorLbl.setText(stringShownInErrorLabel);
        while (cc.awaitResponse) ; //wait for data from server
        this.initialize(null,null);
        }


    @FXML
    void clickedUnfreezeBtn(MouseEvent event) {
        Message m = new Message();
        int savedUserID = 0; String status="";
        try {
            savedUserID = manageCustomers.getSelectionModel().getSelectedItem().getUserId();
            status = manageCustomers.getSelectionModel().getSelectedItem().getStatus();
        } catch (Exception e) {
            System.out.println("null row selected");
        }
        m.setCommand("confirmUnfreeze");
        m.setMsg(savedUserID);
        cc.send(m);// send the row key (userid)
        String stringShownInErrorLabel;
        if (savedUserID==0)
            stringShownInErrorLabel = "Please select customer !";
        else if(status.equals("active"))
            stringShownInErrorLabel="Selected customer is already active!";
        else
            stringShownInErrorLabel = String.format("Customer with id = %d is active", savedUserID);
        errorLbl.setText(stringShownInErrorLabel);
        while (cc.awaitResponse) ; //wait for data from server
        this.initialize(null,null);
    }

    @FXML
    void enteredBackBtn(MouseEvent event) {cc.enteredButton(backBtn);}
    @FXML
    void enteredFreezeBtn(MouseEvent event) {cc.enteredButton(freezeBtn);}
    @FXML
    void enteredUnfreezeBtn(MouseEvent event) {cc.enteredButton(unfreezeBtn);}
    @FXML
    void leavedBackBtn(MouseEvent event) {cc.leavedButton(backBtn);}
    @FXML
    void leavedFreezeBtn(MouseEvent event) {cc.leavedButton(freezeBtn);}
    @FXML
    void leavedUnfreezeBtn(MouseEvent event) {cc.leavedButton(unfreezeBtn); }

}
