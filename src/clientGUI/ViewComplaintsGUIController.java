package clientGUI;

import client.ClientController;
import clientClasses.Complaint;
import clientClasses.Delivery;
import commonClasses.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

import static client.ClientController.savedWindows;

public class ViewComplaintsGUIController {

    @FXML
    private TableView<Complaint> tbl_complaints;

    @FXML
    private TableColumn<Complaint, Integer> col_complaintId;

    @FXML
    private TableColumn<Complaint, String> col_done;

    @FXML
    private TableColumn<Complaint, Timestamp> col_insertedTime;

    @FXML
    private ImageView btn_done;

    @FXML
    private ImageView btn_sendRefund;

    @FXML
    private ImageView btn_viewDetails;

    @FXML
    private ImageView btn_back;
    @FXML
    private Label lbl_error;
    @FXML
    private TextField txt_amount;

    private ClientController conn = ClientController.getClientController();
    private  String valOrder;
    String  RefundAmount;


    public void initialize() {
        lbl_error.setText("");
        this.getTableFromDB();
    }
    @FXML
    void clickedDoneBtn(MouseEvent event) { this.complaintDone(); }

    /**
     * clickedBackBtn - back to main window
     * @param event mouse click
     */
    @FXML
    void clickedBackBtn(MouseEvent event) {
        Stage stage = ((Stage)lbl_error.getScene().getWindow());
        stage.close();
        Stage window = savedWindows.getUserWindow();
        LoginGUIController.loginController.initialize();
        window.show();
    }

    @FXML
    void clickedSendRefundBtn(MouseEvent event) throws IOException {
        Message msg = new Message();
        String data[] = new String[2];
        if (txt_amount.getText().equals("") || RefundAmount.equals("")){
            lbl_error.setText("Please enter refund amount!");
            return;
        }
        data[0] = valOrder; data[1] = RefundAmount;
        msg.setCommand("addRefundCustomerServes");
        msg.setMsg(data);
        conn.sendToServer(msg);
        lbl_error.setText("Refund added to client balance");
    }

    /**
     *
     * @param event
     */
    @FXML
    void changeRefund(KeyEvent event) {
        txt_amount.getText();
        RefundAmount = "";
        if (!event.getCharacter().matches("[0-9]+")) {
            lbl_error.setText("Amount must be a number!");
        } else { RefundAmount = txt_amount.getText();}
    }
    /**
     * clickedViewDetailsBtn - view complaint details
     * @param event - entering the button with the mouse
     */
    @FXML
    void clickedViewDetailsBtn(MouseEvent event) { Message msg = new Message() ;
        int val;
        Alert alert;
        try{
            val = Integer.parseInt(valOrder);
            alert= new Alert(Alert.AlertType.INFORMATION);
            msg.setCommand("getComplaintDetails");
            msg.setMsg(val);
            conn.send(msg);
            CachedRowSet msgFromServer;
            String details = "";
            while(conn.awaitResponse);
            msgFromServer= (CachedRowSet) (conn.messageFromServer.getMsg());
            try { while (msgFromServer.next()) { details = msgFromServer.getString("complaintText");}}
            catch (SQLException e) { System.out.println("Error read data from server " + e);}
            alert.setTitle("complaint Details");
            alert.setHeaderText("complaint number: " + valOrder);
            alert.setContentText(details);
            alert.showAndWait().ifPresent(rs -> { if (rs == ButtonType.OK) { System.out.println("complaint done!"); }});}
         catch (Exception e) { lbl_error.setText("Select complaint to end"); }
    }

    /**
     *  enteredViewDetailsBtn - function for making shadow for button
     *  @param event - entering the button with the mouse
     */
    @FXML
    void enteredDoneBtn(MouseEvent event) {conn.enteredButton(btn_done); }

    /**
     *  enteredViewDetailsBtn - function for making shadow for button
     *  @param event - entering the button with the mouse
     */
    @FXML
    void enteredSendRefundBtn(MouseEvent event) { conn.enteredButton(btn_sendRefund); }

    /**
     *  enteredViewDetailsBtn - function for making shadow for button
     *  @param event - entering the button with the mouse
     */
    @FXML
    void enteredViewDetailsBtn(MouseEvent event) {conn.enteredButton(btn_viewDetails); }

    /**
     *  enteredBackBtn - function for making shadow for button
     *  @param event - entering the button with the mouse
     */
    @FXML
    void enteredBackBtn(MouseEvent event) { conn.enteredButton(btn_back); }

    /**
     *  leavedDoneBtn - function to hide shadow for button
     *  @param event - leaving the button with the mouse
     */
    @FXML
    void leavedDoneBtn(MouseEvent event) {conn.leavedButton(btn_done); }

    /**
     *  leavedSendRefundBtn - function to hide shadow for button
     *  @param event - leaving the button with the mouse
     */
    @FXML
    void leavedSendRefundBtn(MouseEvent event) { conn.leavedButton(btn_sendRefund); }

    /**
     *  leavedBackBtn - function to hide shadow for button
     *  @param event - leaving the button with the mouse
     */
    @FXML
    void leavedBackBtn(MouseEvent event) { conn.leavedButton(btn_back);}

    /**
     *  leavedViewDetailsBtn - function to hide shadow for button
     *  @param event - leaving the button with the mouse
     */
    @FXML
    void leavedViewDetailsBtn(MouseEvent event) { conn.leavedButton(btn_viewDetails);}

    private void setColumns() {
        col_complaintId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        col_done.setCellValueFactory(new PropertyValueFactory<>("done"));
        col_insertedTime.setCellValueFactory(new PropertyValueFactory<>("Time"));
        tbl_complaints.setOnMouseClicked(click -> {
            try{
                if (click.getClickCount() == 1) {
                    @SuppressWarnings("rawtypes")
                    TablePosition pos = tbl_complaints.getSelectionModel().getSelectedCells().get(0); //TablePosition
                    int row = pos.getRow();
                    valOrder= String.valueOf(col_complaintId.getCellData(row));
                }}
            catch (Exception e){ lbl_error.setText("Select complaint to end!");}}); // get value of row in table and value of delivery number
    }

    private void getTableFromDB() {
        this.setColumns();
        Message msg = new Message();
        CachedRowSet msgFromServer;
        ObservableList<Complaint> deliverylist = FXCollections.observableArrayList();
        msg.setCommand("complaintsTable");
        msg.setMsg(ClientController.userLoginData.getUserid());
        conn.send(msg);
        while(conn.awaitResponse);
        msgFromServer = (CachedRowSet)(conn.messageFromServer.getMsg());
        try {
            while (msgFromServer.next()) {
                Complaint complaint =  new Complaint((
                        msgFromServer.getInt("complaintid")),
                        msgFromServer.getString("complaintDone"),
                        msgFromServer.getTimestamp("date"));
                deliverylist.add(complaint);
            }}
        catch (SQLException e) { System.out.println("Error read data from server " + e);}
        tbl_complaints.setItems(deliverylist);
    }

    private void complaintDone() {
        Message msg = new Message();  Alert alert = new Alert(Alert.AlertType.INFORMATION);
        int[] orderData = new int[2];
        CachedRowSet msgFromServer;
        String userphnum = "",mail= "",fname="",lname="";
        try{
        orderData[0] = ClientController.userLoginData.getUserid();
        orderData[1] = Integer.parseInt(valOrder);
        msg.setCommand("complaintDone");
        msg.setMsg(orderData);
        conn.send(msg);
        while(conn.awaitResponse);
        alert.setTitle("Complaint!");
        alert.setHeaderText("Complaint Done!");
        fname =fname +" "+lname;
        alert.setContentText("A message has been sent to user via email and phone!");
        alert.showAndWait().ifPresent(rs -> { if (rs == ButtonType.OK) { System.out.println("Complaint Done");}});
        lbl_error.setText("Complaint Done successfully!");
        initialize();}
        catch (Exception e) { lbl_error.setText("Select complaint to end!");}
    }
}
