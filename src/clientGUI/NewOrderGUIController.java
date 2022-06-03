package clientGUI;

import client.ClientController;
import client.NewPopUpWindowFrameController;
import client.NewWindowFrameController;
import clientClasses.ItemInNewOrder;
import clientClasses.NewOrder;
import commonClasses.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.sql.rowset.CachedRowSet;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 *  Controller class for new order window
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */
public class NewOrderGUIController {

    @FXML
    private ImageView btn_back;

    @FXML
    private ScrollPane scrollPaneItems_OurItems;

    @FXML
    private ComboBox<String> cBox_price_OurItems;

    @FXML
    private ComboBox<String> cBox_color_OurItems;

    @FXML
    private ComboBox<String> cBox_type_OurItems;

    @FXML
    private AnchorPane anchorPane_CreateOrEdit;

    @FXML
    private TextField txt_createCustom;

    @FXML
    private ImageView btn_chooseItems_Custom;

    @FXML
    private ComboBox<String> cBox_editItems_Custom;

    @FXML
    private ImageView btn_editCustom;

    @FXML
    private Label lbl_msg;

    @FXML
    private ImageView btn_proceedWithTheOrder;

    @FXML
    private Label lbl_orderPrice;

    @FXML
    private TextArea txt_myOrder;

    @FXML
    private AnchorPane anchorPaneItems_Custom;

    @FXML
    private ScrollPane scrollPaneItems_Custom;

    @FXML
    private ComboBox<String> cBox_price_Custom;

    @FXML
    private ComboBox<String> cBox_color_Custom;

    @FXML
    private ComboBox<String> cBox_type_Custom;

    @FXML
    private ImageView btn_editCustomOrCreateAnotherOne_Custom;

    @FXML
    private Tab tab_custom;

    @FXML
    private Label lbl_firstOrder;

    @FXML
    private ImageView btn_removeCustom;

    @FXML
    private ImageView btn_help;

    private ObservableList<ItemInNewOrder> itemsListInOurItemsTab; //items in "our items tab"
    private ObservableList<ItemInNewOrder> itemsListInCustomTab; //items in the first custom build, if there is more, items will be cloned to another list dynamically
    private List<NewOrder> itemsInNewOrderList = new ArrayList<>(); //items in this order (all items user added to order)
    public static NewOrderGUIController controller; //controller of this class
    private String fullOrder; //string representation of order
    private float fullPrice; //price of the order
    private String customRequest; //last saved user customization request (bouquet, ...)

    /**
     * initializes new order window
     */
    @FXML
    public void initialize() {
        controller = this;
        lbl_firstOrder.setText("");
        if (ClientController.userData.isFirstOrder()) //check if customer have discount for first order
            lbl_firstOrder.setText("Price after first order 20% discount:");
        setItems(); //create items greed
        //add values to checkBoxes:
        String[] colors = {"all", "red", "blue", "yellow", "purple", "pink", "white"};
        String[] prices = {"all", "< 10 ₪", "< 15 ₪", "< 20 ₪", "< 30 ₪", "< 100 ₪", "< 150 ₪", "< 200 ₪"};
        String[] types = {"all", "bundles", "flowers"};
        cBox_color_Custom.setItems(FXCollections.observableArrayList(colors));
        cBox_color_OurItems.setItems(FXCollections.observableArrayList(colors));
        cBox_price_Custom.setItems(FXCollections.observableArrayList(prices));
        cBox_price_OurItems.setItems(FXCollections.observableArrayList(prices));
        cBox_type_Custom.setItems(FXCollections.observableArrayList(types));
        cBox_type_OurItems.setItems(FXCollections.observableArrayList(types));
        cBox_color_Custom.setValue("all");
        cBox_color_OurItems.setValue("all");
        cBox_price_Custom.setValue("all");
        cBox_price_OurItems.setValue("all");
        cBox_type_Custom.setValue("all");
        cBox_type_OurItems.setValue("all");
    }

    /**
     * gets items data from server and creates items grid pane
     */
    private void setItems() {
        lbl_msg.setText("Loading items from catalog, please wait..."); //message to user that catalog is loading
        Platform.runLater(new Runnable() { //thread to build catalog without being "stuck" as it can be looked because of the loading time of catalog (pictures at most)
            @Override
            public void run() {
                Message msg = new Message();
                msg.setCommand("getCatalog");
                ClientController.getClientController().send(msg);
                while (ClientController.awaitResponse) ;
                itemsListInOurItemsTab = FXCollections.observableArrayList();
                CachedRowSet cachedset = (CachedRowSet) (ClientController.messageFromServer.getMsg());

                try {
                    while (cachedset.next()) {
                        String imagePath = cachedset.getString("imagePath");
                        //-------request picture from server by it path on it----------------------
                        Message getpic = new Message();
                        getpic.setCommand("getItemPicture");
                        getpic.setMsg(imagePath);
                        ClientController.getClientController().send(getpic);
                        while (ClientController.awaitResponse) ;
                        //-------------------------------------------------------------------------

                        //----convert picture, set it size to 200x200 pixels-----------------------
                        byte[] bytearr = (byte[]) ClientController.messageFromServer.getMsg();
                        ByteArrayInputStream bis = new ByteArrayInputStream(bytearr);
                        BufferedImage bimage = ImageIO.read(bis);
                        Image convertedImage = SwingFXUtils.toFXImage(bimage, null);
                        ImageView itemPic = new ImageView(convertedImage);
                        itemPic.setFitHeight(200);
                        itemPic.setFitWidth(200);
                        ImageView itemPicCopy = new ImageView(convertedImage);
                        itemPicCopy.setFitWidth(200);
                        itemPicCopy.setFitHeight(200);
                        //--------------------------------------------------------------------------
                        //add item to the list of items:
                        ItemInNewOrder item = new ItemInNewOrder(cachedset.getInt("id"), itemPic, cachedset.getString("name"), cachedset.getFloat("price"), cachedset.getString("color"), cachedset.getString("isBundle"), false);
                        itemsListInOurItemsTab.add(item);
                    }
                } catch (Exception e) {
                    System.out.println("error getting catalog data from server " + e);
                    e.printStackTrace();
                }
                itemsListInCustomTab = copyItemsList(); //copy items for custom tab
                // * ----------------------------------------build catalog UI from items we received-------------------------------------*/
                GridPane gridPaneOurItems = getItemsAsGridPane(itemsListInOurItemsTab);
                GridPane gridPaneCustom = getItemsAsGridPane(itemsListInCustomTab);
                scrollPaneItems_OurItems.setContent(gridPaneOurItems);
                scrollPaneItems_Custom.setContent(gridPaneCustom);
                lbl_msg.setText(""); //end of load
            }
        });//end of thread
    }

    /**
     * creates grid pane from itemsList (5 columns)
     *
     * @param itemsList list of items in new GridPane
     * @return GridPane of items from items list
     */
    private GridPane getItemsAsGridPane(ObservableList<ItemInNewOrder> itemsList) {
        GridPane itemsGridPane = new GridPane();
        int itemsGridPaneRow = 0;
        int itemsGridPaneCol = 0;
        for (ItemInNewOrder item : itemsList) {
            itemsGridPane.add(item.getItemInNewOrderVBoxVBox(), itemsGridPaneCol, itemsGridPaneRow);
            itemsGridPaneCol++;
            if (itemsGridPaneCol == 5) { //new row
                itemsGridPaneCol = 0;
                itemsGridPaneRow++;
            }
        }
        itemsGridPane.setPadding(new Insets(10, 10, 10, 10));
        itemsGridPane.setHgap(10);
        itemsGridPane.setVgap(10);
        itemsGridPane.setStyle("-fx-background-color:  #f7b77c");
        return itemsGridPane;
    }

    /**
     * create items list with all items from server with quantity = 0 in it, marked as "custom"
     *
     * @return full items list with quantity = 0 in it (initial items list), isCustom = true
     */
    private ObservableList<ItemInNewOrder> copyItemsList() {
        ObservableList<ItemInNewOrder> newItemsList = FXCollections.observableArrayList();
        for (ItemInNewOrder item : itemsListInOurItemsTab) {
            ImageView imageCopy = new ImageView(item.getItemImage().getImage()); //create copy of image because javafx is not allowing 2 same ImageViews
            imageCopy.setFitHeight(200);
            imageCopy.setFitWidth(200);
            ItemInNewOrder newItem = new ItemInNewOrder(item.getId(), imageCopy, item.getItemName(), item.getItemPrice(), item.getItemColor(), item.getIsBundle(), true); //create copy of item
            newItemsList.add(newItem);
        }
        return newItemsList;
    }

    /**
     * print full order data in txt_myOrder TextField
     */
    public void printFullOrder() {
        fullPrice = 0; //full order price
        fullOrder = ""; //order text
        for (NewOrder items : itemsInNewOrderList) {
            if (!items.isCustom()) { //if not custom, just add the item
                fullOrder = fullOrder + items.getCatalogItem() + "\n";
                fullPrice += items.getPrice();
            } else { //if custom
                fullOrder = fullOrder + "Custom: " + items.getCustomBuildRequest() + " ,list of items:\n";
                for (ItemInNewOrder itemInCustom : items.getItemsInCustomBuild()) { //add all the items from custom NewOrder items list
                    fullOrder = fullOrder + "\t" + itemInCustom + "\n";
                    fullPrice += itemInCustom.getItemPrice() * itemInCustom.getQuantity();
                }
            }
        }
        if (ClientController.userData.isFirstOrder()) //check if this order is the first one, set 20% discount if yes
            fullPrice *= 0.8;
        fullPrice = fullPrice*100;
        fullPrice = Math.round(fullPrice);
        fullPrice = fullPrice /100;
        fullOrder += "-----------------------------\n";
        if (ClientController.userData.isFirstOrder()) //add text for first order
            fullOrder += "After 20% first order discount:\n";
        fullOrder += "Order price: " + fullPrice + "₪\n";
        txt_myOrder.setText(fullOrder);
        lbl_orderPrice.setText("Order price: " + fullPrice + "₪");
    }

    /**
     * returns full order as list
     * @return List of items in order
     */
    public List<NewOrder> getItemsInNewOrderList() {
        return itemsInNewOrderList;
    }

    /**
     * returns last saved custom request of customer
     * @return String last saved custom request
     */
    public String getCustomRequest() {
        return customRequest;
    }

    /**
     * back button clicked method
     * @param event mouse click on button back
     * @throws Exception javafx exception when initializes new window "CustomerGUI.fxml"
     */
    @FXML
    void clickedBack(MouseEvent event) throws Exception {
        Stage thisStage = (Stage) btn_back.getScene().getWindow();
        thisStage.hide();
        //reset order details and payment windows if was used:
        if(ClientController.savedWindows.getPaymentWindow()!=null)
            ClientController.savedWindows.setPaymentWindow(null);
        if(ClientController.savedWindows.getOrderDetailsWindow()!=null)
            ClientController.savedWindows.setOrderDetailsWindow(null);
        //----------------------------------------------------
        NewWindowFrameController customerWindow = new NewWindowFrameController("CustomerGUI");
        customerWindow.start(new Stage());
        CustomerGUIController.controller.initialize(); //reload user  data
    }

    /**
     * ComboBox sort by color in custom tab value changed, sort items according to new value
     * @param event new value chosen
     */
    @FXML
    void changedCBox_color_Custom(ActionEvent event) {
        sortItems("Custom");
    }

    /**
     * ComboBox sort by color in "Our items" tab value changed, sort items according to new value
     * @param event
     */
    @FXML
    void changedCBox_color_OurItems(ActionEvent event) {
        sortItems("OurItems");
    }

    /**
     * ComboBox sort by price in custom tab value changed, sort items according to new value
     * @param event
     */
    @FXML
    void changedCBox_price_Custom(ActionEvent event) {
        sortItems("Custom");
    }

    /**
     * ComboBox sort by price in "Our items" tab value changed, sort items according to new value
     * @param event
     */
    @FXML
    void changedCBox_price_OurItems(ActionEvent event) {
        sortItems("OurItems");
    }

    /**
     * ComboBox sort by type(bundle/flower) in custom tab value changed, sort items according to new value
     * @param event
     */
    @FXML
    void changedCBox_type_Custom(ActionEvent event) {
        sortItems("Custom");
    }

    /**
     * ComboBox sort by type(bundle/flower) in "Our items" tab value changed, sort items according to new value
     * @param event
     */
    @FXML
    void changedCBox_type_OurItems(ActionEvent event) {
        sortItems("OurItems");
    }

    /**
     * sort items as it requested in ComboBoxes
     *
     * @param selectedTab in which tab items will be sorted "OurItems" for catalog items, "Custom" for custom order
     */
    private void sortItems(String selectedTab) {
        String color = "";
        String priceStr = "";
        String type = "";
        //get data from comboboxes:
        if (selectedTab.equals("OurItems")) {
            color = cBox_color_OurItems.getValue();
            priceStr = cBox_price_OurItems.getValue();
            type = cBox_type_OurItems.getValue();
        } else if (selectedTab.equals("Custom")) {
            color = cBox_color_Custom.getValue();
            priceStr = cBox_price_Custom.getValue();
            type = cBox_type_Custom.getValue();
        }
        //convert string price to int
        float price = 0;
        if (priceStr.equals("< 10 ₪"))
            price = 10;
        else if (priceStr.equals("< 15 ₪"))
            price = 15;
        else if (priceStr.equals("< 20 ₪"))
            price = 20;
        else if (priceStr.equals("< 30 ₪"))
            price = 30;
        else if (priceStr.equals("< 100 ₪"))
            price = 100;
        else if (priceStr.equals("< 150 ₪"))
            price = 150;
        else if (priceStr.equals("< 200 ₪"))
            price = 200;
        else if (priceStr.equals("all"))
            price = Float.MAX_VALUE;

        ObservableList<ItemInNewOrder> newList = FXCollections.observableArrayList(); //empty list to add items while sorting
        List<ItemInNewOrder> itemslist = null; //items list in selected tab
        if (selectedTab.equals("OurItems"))
            itemslist = itemsListInOurItemsTab;
        else if (selectedTab.equals("Custom")) { //if custom tab
            itemslist = copyItemsList(); //create new initial items list
            for (NewOrder order : itemsInNewOrderList) { //replace items in newlist by the items in custom list selected
                if (order.isCustom() && order.getCustomBuildRequest().equals(customRequest)) {
                    List<ItemInNewOrder> itemsToReplace = order.getItemsInCustomBuild();
                    for (int i = 0; i < itemsToReplace.size(); i++) {
                        itemslist.set(itemslist.indexOf(itemsToReplace.get(i)), itemsToReplace.get(i));
                    }
                }
            }

        }
        //sort items, add to the new list if needed:
        for (ItemInNewOrder item : itemslist) {
            if (type.equals("all")) {
                if (color.equals("all") && item.getItemPrice() < price)
                    newList.add(item);
                else if (item.getItemPrice() < price && color.equals(item.getItemColor()))
                    newList.add(item);
            } else if (type.equals("bundles") && item.isBundle() == true) {
                if (color.equals("all") && item.getItemPrice() < price)
                    newList.add(item);
                else if (item.getItemPrice() < price && color.equals(item.getItemColor()))
                    newList.add(item);
            } else if (type.equals("flowers") && item.isBundle() == false) {
                if (color.equals("all") && item.getItemPrice() < price)
                    newList.add(item);
                else if (item.getItemPrice() < price && color.equals(item.getItemColor()))
                    newList.add(item);
            }
        }

        GridPane newGridPane = getItemsAsGridPane(newList); //set items in gridPane
        //add to relevant tab:
        if (selectedTab.equals("OurItems"))
            scrollPaneItems_OurItems.setContent(newGridPane);
        else if (selectedTab.equals("Custom"))
            scrollPaneItems_Custom.setContent(newGridPane);
    }

    /**
     * return price of order
     * @return price of items in order
     */
    public double getFullPrice() {
        return fullPrice;
    }

    /**
     * return items in order as string
     * @return items in order as string
     */
    public String getFullOrder() {
        return fullOrder;
    }

    /**
     * choose items button in custom tab controller
     * @param event mouse click on choose items button
     */
    @FXML
    void clickedChooseItems_Custom(MouseEvent event) {
        lbl_msg.setText("");
        if (txt_createCustom.getText().equals("")) //check if request is empty
            lbl_msg.setText("Please describe custom order requirements in the text field above");
        else { //if request is set
            customRequest = txt_createCustom.getText(); //save last request
            for (NewOrder order : itemsInNewOrderList) { //search for same custom request
                if (order.isCustom() && order.getCustomBuildRequest().equals(customRequest)) {
                    lbl_msg.setText("Item with the same request exists, please change it, e.g. \"bouquet 2\"");
                    return;
                }
            }
            cBox_editItems_Custom.getItems().add(customRequest); //save request in "edit items" ComboBox
            ObservableList<ItemInNewOrder> newItemsList = copyItemsList(); //create new list of items
            GridPane newCustomGrid = getItemsAsGridPane(newItemsList); //set new items as gridpane
            NewOrder newCustomOrder = new NewOrder(newCustomGrid, customRequest, new ArrayList<ItemInNewOrder>()); //create new custom build in order
            itemsInNewOrderList.add(newCustomOrder); //add new custom build to order list
            scrollPaneItems_Custom.setContent(newCustomGrid);
            tab_custom.setContent(anchorPaneItems_Custom);
        }
    }

    /**
     * "EditCustomOrCreateAnotherOne" button controller, sets back create or edit menu
     * @param event mouse click on button "EditCustomOrCreateAnotherOne"
     */
    @FXML
    void clickedEditCustomOrCreateAnotherOneBtn(MouseEvent event) {
        txt_createCustom.setText(""); //empty previous request
        tab_custom.setContent(anchorPane_CreateOrEdit); //set back create or edit menu
    }

    /**
     * "Edit" button controller, opens custom build so customer can change it
     * @param event mouse click on "edit" button
     */
    @FXML
    void clickedEditbtn_Custom(MouseEvent event) {
        lbl_msg.setText("");
        if (cBox_editItems_Custom.getValue() == null) { //if custom build isn't chosen
            lbl_msg.setText("Please select custom item to edit");
        } else { //if chosen
            for (NewOrder order : itemsInNewOrderList) { //search for custom build in order items
                if (order.isCustom() && order.getCustomBuildRequest().equals(cBox_editItems_Custom.getValue())) {
                    txt_createCustom.setText("");
                    customRequest = order.getCustomBuildRequest(); //set customRequest to one customer edits
                    //reset sort comboBoxes, set items grid of this custom and sort it for "all"
                    cBox_type_Custom.setValue("all");
                    cBox_price_Custom.setValue("all");
                    cBox_color_Custom.setValue("all");
                    scrollPaneItems_Custom.setContent(order.getItemsGrid());
                    tab_custom.setContent(anchorPaneItems_Custom);
                    sortItems("Custom");
                }
            }
        }
    }

    /**
     * "remove" button in custom build controller, removes custom build from order
     * @param event mouse click on "remove" button
     */
    @FXML
    void clickedRemoveCustom(MouseEvent event) {
        lbl_msg.setText("");
        if (cBox_editItems_Custom.getValue() == null) { //if custom build isn't selected
            lbl_msg.setText("Please choose order to remove");
        } else { //if selected
            Iterator<NewOrder> iterator = itemsInNewOrderList.iterator(); //iterator for order list so we can remove it
            while (iterator.hasNext()) { //search for custom build in order
                NewOrder order = iterator.next();
                if (order.isCustom() && order.getCustomBuildRequest().equals(cBox_editItems_Custom.getValue())) {
                    cBox_editItems_Custom.getItems().remove(order.getCustomBuildRequest()); //remove custom from "edit" comboBox
                    iterator.remove(); //remove custom from order
                    printFullOrder(); //refresh order
                }
            }
        }
    }

    /**
     * "proceed with the order" button controller, starts new "OrderDetails.fxml" window
     * or if this window is hidden (button back from OrderDetails), shows it.
     * @param event mouse click on "proceed with the order" button
     * @throws Exception javafx exception when trying to set "OrderDetails.fxml" window
     */
    @FXML
    void clickedProceedWithTheOrderBtn(MouseEvent event) throws Exception {
        printFullOrder();
        lbl_msg.setText("");
        Stage thisStage = (Stage) btn_back.getScene().getWindow();
        if (fullPrice == 0) //if no items in order
            lbl_msg.setText("No items in order");
        else if (ClientController.savedWindows.getOrderDetailsWindow() != null) { //if no saved "OrderDetails" window
            ClientController.savedWindows.setNewOrderWindow(thisStage); //save this window
            thisStage.hide(); //hide this window
            ClientController.savedWindows.getOrderDetailsWindow().show();
            OrdersDetailsGUIController.controller.printOrderWithDeliveryDetails(); //update order details in new window
            // (it needed because if back button was pressed in OrderDetails window .show() doesn't call to initialize() method again)
        } else {
            ClientController.savedWindows.setNewOrderWindow(thisStage); //save this window
            thisStage.hide(); //hide this window
            NewWindowFrameController orderDetailsWindow = new NewWindowFrameController("OrderDetailsGUI");
            orderDetailsWindow.start(new Stage());
        }
    }

    /**
     * "Help" button controller, shows popup with help
     * @param event mouse click on "Help" button
     * @throws Exception javafx exception when setting new "NewOrderHelpPOPUP,fxml" window
     */
    @FXML
    void clickedHelp(MouseEvent event) throws Exception {
        NewPopUpWindowFrameController helpPopUp = new NewPopUpWindowFrameController("NewOrderHelpPOPUP");
        helpPopUp.start(new Stage());
    }

    /**
     * "Help" button entered, changes color of button
     * @param event mouse entered
     */
    @FXML
    void enteredHelpBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_help);
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
     * "Choose items" button entered, changes color of button
     * @param event mouse entered
     */
    @FXML
    void enteredChooseItems_Custom(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_chooseItems_Custom);

    }
    /**
     * "Edit custom or create another one" button entered, changes color of button
     * @param event mouse entered
     */
    @FXML
    void enteredEditCustomOrCreateAnotherOneBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_editCustomOrCreateAnotherOne_Custom);

    }
    /**
     * Help button entered, changes color of button
     * @param event mouse entered
     */
    @FXML
    void enteredEditbtn_Custom(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_editCustom);

    }
    /**
     * "Proceed with the order" button entered, changes color of button
     * @param event mouse entered
     */
    @FXML
    void enteredProceedWithTheOrderBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_proceedWithTheOrder);
    }
    /**
     * "Remove" button entered, changes color of button
     * @param event mouse entered
     */
    @FXML
    void enteredRemoveCustomBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_removeCustom);
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
     * "Choose items" button exited, changes color of button
     * @param event mouse entered
     */
    @FXML
    void leavedChooseItems_Custom(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_chooseItems_Custom);

    }
    /**
     * "Edit custom or create another one" button exited, changes color of button
     * @param event mouse entered
     */
    @FXML
    void leavedEditCustomOrCreateAnotherOneBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_editCustomOrCreateAnotherOne_Custom);

    }
    /**
     * "Edit" button exited, changes color of button
     * @param event mouse entered
     */
    @FXML
    void leavedEditbtn_Custom(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_editCustom);

    }
    /**
     * "Proceed with the payment" button exited, changes color of button
     * @param event mouse entered
     */
    @FXML
    void leavedProceedWithTheOrderBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_proceedWithTheOrder);
    }
    /**
     * "Remove" button exited, changes color of button
     * @param event mouse entered
     */
    @FXML
    void leavedRemoveCustomBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_removeCustom);
    }
    /**
     * "Help" button exited, changes color of button
     * @param event mouse entered
     */
    @FXML
    void leavedHelpBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_help);
    }
}
