package clientGUI;

import client.ClientController;
import client.NewWindowFrameController;
import clientClasses.RegistrationTable;
import commonClasses.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
/**
 * ApproveCustomersGUIController class is responsible for allowing each shop manager to approve customers,
 *  by giving them username and password, and clicking approve customer button they will be recognized in
 *  the system and login to their accounts.
 *
 * <p> Project Name: Zer-Li (Flowers store in Java) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */
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

    /**
     * initialize method sets the columns in the TableView
     * so that the manager can give a username and password
     * for customer and approve him.
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
        msg.setMsg("");
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

    /**
     * clickedApproveCustomerBtn method approves the customer when clicking Approve customer button,
     * and sends the selected customer data (new username password) to the server
     * in order to save them in DB also checks if username already there
     * @param event mouse click
     */
    @FXML
    void clickedApproveCustomerBtn(MouseEvent event) {
        int count=0;
        Message m = new Message();
        String savedUsername="",savedPassword="";
        int savedUserID = 0;
        try {
            savedUserID = manageUsers.getSelectionModel().getSelectedItem().getUserId();
            savedUsername = manageUsers.getSelectionModel().getSelectedItem().getUsername();
            savedPassword = manageUsers.getSelectionModel().getSelectedItem().getPassword();
            if(savedUsername == null || savedPassword==null || savedUsername.equals("") || savedPassword.equals("")){
                errorLbl.setText("Fields username or password is empty");
                return;
            }
            m.setCommand("checkUserName");
            m.setMsg(savedUsername);
            cc.send(m);
            while (cc.awaitResponse) ; //wait for data from server
            count = (int) (cc.messageFromServer.getMsg());
            if(count >=1){
                errorLbl.setText("Username already exsit please choose another");
                return;
            }
        } catch (Exception e) {
            System.out.println("null row selected");
        }
        CachedRowSet cachedMsg = null;
        m.setCommand("approveCustomer");
        Object [] data = {savedUserID,savedUsername,savedPassword};
        m.setMsg(data);
        cc.send(m);// send the row key (orderId) + username and password that the manager entered.
        String stringShownInErrorLabel = "";

        if (savedUserID==0)
            stringShownInErrorLabel = "Please select customer !";
        else {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setResizable(true);
            alert.setHeight(600);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(stringShownInErrorLabel);
            alert.setContentText("Approve registration message sent to customer "+manageUsers.getSelectionModel().getSelectedItem().getFirstName()+" " +manageUsers.getSelectionModel().getSelectedItem().getLastName()
                    +", phone number : "+manageUsers.getSelectionModel().getSelectedItem().getTelNum()+"\nEmail : "+
                    manageUsers.getSelectionModel().getSelectedItem().getEmail()+"\nYour request for registration is aprroved by manager\n" +
                    "Your username is : "+manageUsers.getSelectionModel().getSelectedItem().getUsername()+"\n" +
                    "Your password is : "+manageUsers.getSelectionModel().getSelectedItem().getPassword());
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
            stringShownInErrorLabel = String.format("customer %d is approved", savedUserID);
        }

        errorLbl.setText(stringShownInErrorLabel);


        while (cc.awaitResponse) ; //wait for data from server
        refresh();
    }

    /**
     * refresh method is responsible for refreshing the TableView after clicking approve customer button
     */
        public void refresh(){
            ObservableList<RegistrationTable>  selected,allTable;
            allTable = manageUsers.getItems();
            selected = manageUsers.getSelectionModel().getSelectedItems();
            try {
                selected.forEach(allTable::remove);
            }catch(NoSuchElementException e){
            }
        }
    /**
     * clickedBackBtn method directs the user to the previous GUI - ShopManagerGUI - when clicking back
     * @param event mouse clicked
     * @throws Exception error opening a window
     */
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

}
