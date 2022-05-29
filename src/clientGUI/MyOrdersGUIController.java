package clientGUI;

import client.ClientController;
import client.NewPopUpWindowFrameController;
import clientClasses.Message;
import clientClasses.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class MyOrdersGUIController {

    @FXML
    private ImageView btn_back;

    @FXML
    private TableView<Order> tbl_orders;

    @FXML
    private ImageView btn_cancelOrder;

    @FXML
    private ImageView btn_orderDetails;

    @FXML
    private ImageView btn_confirmDelivery;

    @FXML
    private TableColumn<Order, Integer> col_id;

    @FXML
    private TableColumn<Order, Double> col_price;

    @FXML
    private TableColumn<Order, String> col_shop;

    @FXML
    private TableColumn<Order, Timestamp> col_orderDate;

    @FXML
    private TableColumn<Order, Timestamp> col_deliveryDate;

    @FXML
    private TableColumn<Order, String> col_status;

    @FXML
    private Label lbl_cancelation;

    private ObservableList<Order> ordersList;
    private Order selectedOrder;
    private static MyOrdersGUIController myOrdersControl;

    @FXML
    public void initialize() {
        lbl_cancelation.setText("");
        myOrdersControl = this;
        setOrdersTable();
    }

    @FXML
    void clickedBackBtn(MouseEvent event) {
        Stage thisStage = (Stage) btn_back.getScene().getWindow();
        thisStage.hide();
        ClientController.savedWindows.getClientGUIWindow().show();
    }

    @FXML
    void clickedCancelOrder(MouseEvent event) throws Exception {
        selectedOrder = tbl_orders.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            lbl_cancelation.setText("Please select order by clicking on its row in the table");
            return;
        }
        NewPopUpWindowFrameController popupWindow = new NewPopUpWindowFrameController("CancelOrderPOPUP");
        popupWindow.start(new Stage());
    }

    @FXML
    void clickedOrderDetailsBtn(MouseEvent event) {

    }

    @FXML
    void clickedConfirmDeliveryBtn(MouseEvent event) {

    }

    public void setOrdersTable() {
        col_id.setCellValueFactory(new PropertyValueFactory<Order, Integer>("orderNumber"));
        col_price.setCellValueFactory(new PropertyValueFactory<Order, Double>("price"));
        col_shop.setCellValueFactory(new PropertyValueFactory<Order, String>("shop"));
        col_orderDate.setCellValueFactory(new PropertyValueFactory<Order, Timestamp>("orderDate"));
        col_deliveryDate.setCellValueFactory(new PropertyValueFactory<Order, Timestamp>("deliveryDate"));
        col_status.setCellValueFactory(new PropertyValueFactory<Order, String>("status"));

        ordersList = FXCollections.observableArrayList();
        Message msg = new Message();
        msg.setCommand("getOrders");
        msg.setMsg(ClientController.userLoginData.getUserid());

        ClientController.getClientController().send(msg);
        while (ClientController.awaitResponse) ;
        CachedRowSet msgFromServer = (CachedRowSet) (ClientController.messageFromServer).getMsg();

        try {
            while (msgFromServer.next()) {
                Order order = new Order(msgFromServer.getInt("orderNumber"), msgFromServer.getDouble("price"),
                        msgFromServer.getString("greetingCard"), msgFromServer.getString("color"),
                        msgFromServer.getString("dOrder"), msgFromServer.getString("shop"),
                        msgFromServer.getTimestamp("deliveryDate"), msgFromServer.getTimestamp("orderDate"), msgFromServer.getString("status"));
                ordersList.add(order);
            }
        } catch (SQLException e) {
            System.out.println("error loading orders data " + e);
            e.printStackTrace();
        }
        tbl_orders.setItems(ordersList);
    }


    public static MyOrdersGUIController getMyOrdersControl() {
        return myOrdersControl;
    }

    public void cancelOrder() {
        lbl_cancelation.setText("");
        if (selectedOrder != null) {
            if (selectedOrder.getStatus().equals("pending cancellation")) {
                lbl_cancelation.setText("Cancellation request already exists for order " + selectedOrder.getOrderNumber());
            } else {
                lbl_cancelation.setText("Cancellation request created for order number " + selectedOrder.getOrderNumber());
                Message msgToServer = new Message();
                msgToServer.setCommand("CreateCancellationRequest");
                msgToServer.setMsg(selectedOrder.getOrderNumber());
                ClientController.getClientController().send(msgToServer);
            }
        }
        setOrdersTable();
    }


    @FXML
    void enteredBackBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_back);
    }

    @FXML
    void enteredCancelOrderBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_cancelOrder);
    }

    @FXML
    void enteredOrderDetailsBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_orderDetails);
    }

    @FXML
    void enteredConfirmDeliveryBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_confirmDelivery);
    }

    @FXML
    void leavedBackBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_back);
    }

    @FXML
    void leavedCancelOrderBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_cancelOrder);
    }

    @FXML
    void leavedOrderDetailsBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_orderDetails);
    }

    @FXML
    void leavedConfirmDeliveryBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_confirmDelivery);
    }

}
