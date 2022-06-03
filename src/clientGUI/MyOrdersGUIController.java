package clientGUI;

import client.ClientController;
import client.NewPopUpWindowFrameController;
import commonClasses.Message;
import commonClasses.Order;
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

/**
 *
 *  Controller class for MyOrdersGUI.fxml
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */

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
    private TableColumn<Order, String> col_confirmed;

    @FXML
    private Label lbl_cancelation;

    private ObservableList<Order> ordersList; //list of orders of this customer
    private Order selectedOrder; //order selected in table
    private static MyOrdersGUIController myOrdersControl; //controller of this class

    /**
     * initializes MyOrders window
     */
    @FXML
    public void initialize() {
        lbl_cancelation.setText("");
        myOrdersControl = this; //save controller
        setOrdersTable(); //set orders in table
    }

    /**
     * "Back" button clicked, returns to customer main menu
     * @param event click on buttom "Back"
     */
    @FXML
    void clickedBackBtn(MouseEvent event) {
        Stage thisStage = (Stage) btn_back.getScene().getWindow();
        thisStage.hide(); //hide this window
        ClientController.savedWindows.getClientGUIWindow().show(); //show customer main menu
        CustomerGUIController.controller.initialize(); //reload user data
    }

    /**
     * "Cancel order" button clicked, shows popUp CancelOrderPOPUP.fxml
     * @param event mouse click on "Cancel order" button
     * @throws Exception javafx exception when creates new window CancelOrderPOPUP.fxml
     */
    @FXML
    void clickedCancelOrder(MouseEvent event) throws Exception {
        lbl_cancelation.setText("");
        selectedOrder = tbl_orders.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) { //if no order selected
            lbl_cancelation.setText("Please select order by clicking on its row in the table");
        } else { //create popup
            NewPopUpWindowFrameController popupWindow = new NewPopUpWindowFrameController("CancelOrderPOPUP");
            popupWindow.start(new Stage());
        }
    }

    /**
     *
     * @param event mouse click on "Order details" button, shows order details in popup window
     * @throws Exception javafx exception when creates new window OrderDetailsPOPUP.fxml
     */
    @FXML
    void clickedOrderDetailsBtn(MouseEvent event) throws Exception {
        lbl_cancelation.setText("");
        selectedOrder = tbl_orders.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) //if no order selected
            lbl_cancelation.setText("Please select order by clicking on its row in the table");
        else { //create popup
            NewPopUpWindowFrameController orderDetailsPopUp = new NewPopUpWindowFrameController("OrderDetailsPOPUP");
            orderDetailsPopUp.start(new Stage());
        }
    }

    /**
     * load orders for this customer from DB and put in table
     */
    public void setOrdersTable() {
        //set columns:
        col_id.setCellValueFactory(new PropertyValueFactory<Order, Integer>("orderNumber"));
        col_price.setCellValueFactory(new PropertyValueFactory<Order, Double>("price"));
        col_shop.setCellValueFactory(new PropertyValueFactory<Order, String>("shop"));
        col_orderDate.setCellValueFactory(new PropertyValueFactory<Order, Timestamp>("orderDate"));
        col_deliveryDate.setCellValueFactory(new PropertyValueFactory<Order, Timestamp>("deliveryDate"));
        col_status.setCellValueFactory(new PropertyValueFactory<Order, String>("status"));
        col_confirmed.setCellValueFactory(new PropertyValueFactory<Order, String>("confirmed"));

        ordersList = FXCollections.observableArrayList();
        Message msg = new Message(); //request for DB to get orders list
        msg.setCommand("getOrders");
        msg.setMsg(ClientController.userLoginData.getUserid());

        ClientController.getClientController().send(msg);
        while (ClientController.awaitResponse) ;
        CachedRowSet msgFromServer = (CachedRowSet) (ClientController.messageFromServer).getMsg(); //get table from DB

        try {
            while (msgFromServer.next()) { //create new order and add it to the list
                Order order = new Order(msgFromServer.getInt("orderNumber")
                        , msgFromServer.getDouble("price")
                        , msgFromServer.getString("greetingCard")
                        , msgFromServer.getString("dOrder")
                        , msgFromServer.getString("shop")
                        , msgFromServer.getTimestamp("deliveryDate")
                        , msgFromServer.getTimestamp("orderDate")
                        , msgFromServer.getString("status")
                        ,msgFromServer.getString("confirmed"));
                ordersList.add(order);
            }
        } catch (SQLException e) {
            System.out.println("error loading orders data " + e);
            e.printStackTrace();
        }

        tbl_orders.setItems(ordersList); //set orders in table
        tbl_orders.refresh();
    }

    /**
     * return controller of this class
     * @return controller of this class
     */
    public static MyOrdersGUIController getMyOrdersControl() {
        return myOrdersControl;
    }

    /**
     * create cancellation request for selected order and send it to the server
     */
    public void cancelOrder() {
        lbl_cancelation.setText("");
        if (selectedOrder != null) { //if no order selected
            if (selectedOrder.getStatus().equals("pending cancellation")) { //if request already exists
                lbl_cancelation.setText("Cancellation request already exists for order " + selectedOrder.getOrderNumber());
            }else
                if (selectedOrder.getStatus().equals("cancelled")){
                    lbl_cancelation.setText("Order "+ selectedOrder.getOrderNumber() +" is already cancelled ");
                }
                    else {
                lbl_cancelation.setText("Cancellation request created for order number " + selectedOrder.getOrderNumber());
                Message msgToServer = new Message();
                msgToServer.setCommand("CreateCancellationRequest");
                msgToServer.setMsg(selectedOrder.getOrderNumber()); //add order number to request
                ClientController.getClientController().send(msgToServer);
                lbl_cancelation.setText("Cancellation request created for order number " + selectedOrder.getOrderNumber());
            }
        }
        setOrdersTable(); //refresh table
    }

    /**
     * returns order selected in table
     * @return order selected in table
     */
    public Order getSelectedOrder() {
        return selectedOrder;
    }

    /**
     * "Back" button entered, changes color of button
     * @param event mouse entered
     */
    @FXML
    void enteredBackBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_back);
    }
    /**
     * "Cancel order" button entered, changes color of button
     * @param event mouse entered
     */
    @FXML
    void enteredCancelOrderBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_cancelOrder);
    }
    /**
     * "Order details" button entered, changes color of button
     * @param event mouse entered
     */
    @FXML
    void enteredOrderDetailsBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_orderDetails);
    }
    /**
     * "Back" button exited, changes color of button
     * @param event mouse entered
     */
    @FXML
    void leavedBackBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_back);
    }
    /**
     * "Cancel order" button exited, changes color of button
     * @param event mouse entered
     */
    @FXML
    void leavedCancelOrderBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_cancelOrder);
    }
    /**
     * "Order details" button exited, changes color of button
     * @param event mouse entered
     */
    @FXML
    void leavedOrderDetailsBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_orderDetails);
    }


}
