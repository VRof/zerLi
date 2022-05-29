package clientGUI;

import client.ClientController;
import client.NewWindowFrameController;
import clientClasses.Message;
import clientClasses.RegistrationTable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.sql.rowset.CachedRowSet;
import java.net.URL;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

public class ApproveCustomersGUIController implements Initializable {
    public ClientController cc = ClientController.getClientController();

    @FXML
    private ImageView backBtn;
    @FXML
    private ImageView approveCustomerBtn;

    @FXML
    private TableColumn<RegistrationTable, String> emailCol;

    @FXML
    private Label errorLbl;
    @FXML
    private TableColumn<RegistrationTable, String> usernameCol;
    @FXML
    private TableColumn<RegistrationTable, String> passwordCol;
    @FXML
    private TableColumn<RegistrationTable, String> firstNameCol;

    @FXML
    private TableColumn<RegistrationTable, String> lastNameCol;

    @FXML
    private TableView<RegistrationTable> manageUsers;

    @FXML
    private TableColumn<RegistrationTable, String> telNumCol;

    @FXML
    private TableColumn<RegistrationTable, String> typeCol;

    @FXML
    private TableColumn<RegistrationTable, Integer> userIDCol;
    private ObservableList<RegistrationTable> UsersList;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userIDCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        telNumCol.setCellValueFactory(new PropertyValueFactory<>("telNum"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        UsersList = FXCollections.observableArrayList();
        CachedRowSet cachedMsg;
        //***
        //1.send msg to server
        Message msg = new Message(); //msg to be sent to server
        msg.setCommand("manageUsers");
        msg.setMsg("aa");
        cc.send(msg);

        //2.receive from server


        while (cc.awaitResponse) ; //wait for data from server
        cachedMsg = (CachedRowSet) (cc.messageFromServer.getMsg()); //message received from server

        //3. organize the msg from server in order to show it
        try {
            while (cachedMsg.next()) { //get user data
                RegistrationTable rt = new RegistrationTable();
                rt.setUserId(cachedMsg.getInt("userid"));
                rt.setFirstName(cachedMsg.getString("firstname"));
                rt.setLastName(cachedMsg.getString("lastname"));
                rt.setTelNum(cachedMsg.getString("telephoneNumber"));
                rt.setEmail(cachedMsg.getString("email"));
                rt.setType(cachedMsg.getString("type"));
                UsersList.add(rt);
            }
        } catch (SQLException e) {
            System.out.println("Error read data from server " + e);
        }
        // THIS IS TO ADD THE DATA TO THE GUI
        manageUsers.setItems(UsersList);

        //
        // *** EDIT ***
        //
        usernameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        usernameCol.setOnEditCommit(
                (TableColumn.CellEditEvent<RegistrationTable, String> t) ->
                        (t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setUsername(t.getNewValue())
        );
        passwordCol.setCellFactory(TextFieldTableCell.forTableColumn());
        passwordCol.setOnEditCommit(
                (TableColumn.CellEditEvent<RegistrationTable, String> t) ->
                        (t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setPassword(t.getNewValue())
        );
    }

    @FXML
    void clickedApproveCustomerBtn(MouseEvent event) {
        Message m = new Message();
        //RegistrationTable r=new RegistrationTable();
        String savedUsername="",savedPassword="";
        int savedUserID = 0;
        try {
            savedUserID = manageUsers.getSelectionModel().getSelectedItem().getUserId();
            savedUsername = manageUsers.getSelectionModel().getSelectedItem().getUsername();
            savedPassword = manageUsers.getSelectionModel().getSelectedItem().getPassword();
        } catch (Exception e) {
            System.out.println("null row selected");
        }
        CachedRowSet cachedMsg = null;
        m.setCommand("approveCustomer");
        Object [] data = {savedUserID,savedUsername,savedPassword};
        m.setMsg(data);
        cc.send(m);// send the row key (orderId) + username and password that the manager entered.
        String stringShownInErrorLabel;

        if (savedUserID==0)
            stringShownInErrorLabel = "Please select customer !";
        else
            stringShownInErrorLabel = String.format("customer %d is approved", savedUserID);

        errorLbl.setText(stringShownInErrorLabel);


        while (cc.awaitResponse) ; //wait for data from server
        refresh();
    }
        public void refresh(){
            ObservableList<RegistrationTable>  selected,allTable;
            allTable = manageUsers.getItems();
            selected = manageUsers.getSelectionModel().getSelectedItems();
            try {
                selected.forEach(allTable::remove);
            }catch(NoSuchElementException e){
            }
        }

    @FXML
    void clickedBackBtn(MouseEvent event) throws Exception {
        new NewWindowFrameController("ShopManagerGUI").start(new Stage());
        Stage stage = (Stage) errorLbl.getScene().getWindow();
        stage.hide();
    }

    @FXML
    void enteredBackBtn(MouseEvent event) {cc.enteredButton(backBtn);}
    @FXML
    void leavedBackBtn(MouseEvent event) {cc.leavedButton(backBtn);}
    @FXML
    void enteredApproveCustomerBtn(MouseEvent event) {cc.enteredButton(approveCustomerBtn);}
    @FXML
    void leavedApproveCustomerBtn(MouseEvent event) {cc.leavedButton(approveCustomerBtn);}


//
//    public void editType(TableColumn.CellEditEvent<RegistrationTable, String> registrationTableStringCellEditEvent) {
//
//    }
}
