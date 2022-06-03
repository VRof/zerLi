package clientGUI;

import client.ClientController;
import client.NewWindowFrameController;
import clientClasses.ItemInNewOrder;
import clientClasses.Message;
import clientClasses.NewOrder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
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
import java.util.List;

public class NewOrderGUIController {

    @FXML
    private ImageView btn_back;

    @FXML
    private AnchorPane anchorPane_OurItems;

    @FXML
    private ScrollPane scrollPaneItems_OurItems;

    @FXML
    private ComboBox<String> cBox_price_OurItems;

    @FXML
    private ComboBox<String> cBox_color_OurItems;

    @FXML
    private ImageView btn_sort_OurItems;

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
    private ImageView btn_Sort_Custom;

    @FXML
    private ComboBox<String> cBox_type_Custom;

    @FXML
    private ImageView btn_editCustomOrCreateAnotherOne_Custom;

    @FXML
    private Tab tab_custom;

    @FXML
    private Label lbl_msgDiscount;

    private ObservableList<ItemInNewOrder> itemsListInOurItemsTab;
    private ObservableList<ItemInNewOrder> itemsListInCustomTab;
    private List<NewOrder> itemsInNewOrderList = new ArrayList<>();
    public static NewOrderGUIController controller;
    private String fullOrder;
    private float fullPrice;
    private String customRequest;

    @FXML
    public void initialize() {
        controller = this;
        setItems();
        String[] colors = {"all", "red", "blue", "yellow", "purple", "pink", "white"};
        String[] prices = {"all", "< 10 ₪", "< 15 ₪", "< 20 ₪", "< 30 ₪", "< 100 ₪", "< 150 ₪", "< 200 ₪"};
        String[] types = {"all", "bundles", "flowers"};
        lbl_msgDiscount.setText("");
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
        float num1 = getDiscount();
        if (num1 != 0.0){ lbl_msgDiscount.setText(num1* 100 +"% Discount Available On All Items!"); }
    }
    private float getDiscount(){
        Message msg = new Message();
        msg.setCommand("ifDiscount");
        msg.setMsg("");
        ClientController.getClientController().send(msg);
        while(ClientController.getClientController().awaitResponse);
        float  msgFromServer = (float)(ClientController.getClientController().messageFromServer.getMsg());
        return msgFromServer;
    }
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
                itemsListInCustomTab = FXCollections.observableArrayList();
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
                        ItemInNewOrder itemCopy = new ItemInNewOrder(cachedset.getInt("id"), itemPicCopy, cachedset.getString("name"), cachedset.getFloat("price"), cachedset.getString("color"), cachedset.getString("isBundle"), true);
                        itemsListInOurItemsTab.add(item);
                        itemsListInCustomTab.add(itemCopy);
                    }
                } catch (Exception e) {
                    System.out.println("error getting catalog data from server " + e);
                    e.printStackTrace();
                }

                /*
                 * ----------------------------------------build catalog UI from items we received-------------------------------------*/
                GridPane gridPaneOurItems = getItemsAsGridPane(itemsListInOurItemsTab);
                GridPane gridPaneCustom = getItemsAsGridPane(itemsListInCustomTab);
                scrollPaneItems_OurItems.setContent(gridPaneOurItems);
                scrollPaneItems_Custom.setContent(gridPaneCustom);
                lbl_msg.setText(""); //end of load
            }
        });
    }

    private GridPane getItemsAsGridPane(ObservableList<ItemInNewOrder> itemsList){
        GridPane itemsGridPane = new GridPane();
        int itemsGridPaneRow = 0;
        int itemsGridPaneCol = 0;
        for (ItemInNewOrder item : itemsList) {
            itemsGridPane.add(item.getItemInNewOrderVBoxVBox(), itemsGridPaneCol, itemsGridPaneRow);
            itemsGridPaneCol++;
            if (itemsGridPaneCol == 5) {
                itemsGridPaneCol = 0;
                itemsGridPaneRow++;
            }
        }
        itemsGridPane.setPadding(new Insets(10, 10, 10, 10));
        itemsGridPane.setHgap(10);
        itemsGridPane.setVgap(10);
        itemsGridPane.setStyle("-fx-background-color:  #f7b77c");
        return  itemsGridPane;
    }

    public void printFullOrder() {
        fullPrice = 0;
        fullOrder = "";
        for (NewOrder items : itemsInNewOrderList) {
            if (!items.isCustom()) {
                fullOrder = fullOrder + items.getCatalogItem() + "\n";
                fullPrice += items.getPrice();
            } else {
                fullOrder = fullOrder + "Custom: " +  items.getCustomBuildRequest() + " ,list of items:\n";
                for (ItemInNewOrder itemInCustom : items.getItemsInCustomBuild()) {
                    fullOrder = fullOrder + "\t" + itemInCustom + "\n";
                    fullPrice +=itemInCustom.getItemPrice();
                }
            }
        }
        txt_myOrder.setText(fullOrder);
        lbl_orderPrice.setText("Order price: " + fullPrice + "₪");
    }

    public List<NewOrder> getItemsInNewOrderList() {
        return itemsInNewOrderList;
    }

    public String getCustomRequest() {
        return customRequest;
    }

    @FXML
    void clickedBack(MouseEvent event) throws Exception {
        Stage thisStage = (Stage) btn_back.getScene().getWindow();
        thisStage.hide();
        NewWindowFrameController customerWindow = new NewWindowFrameController("CustomerGUI");
        customerWindow.start(new Stage());}

    @FXML
    void clickedBtnSort_Custom(MouseEvent event) { sortItems("Custom"); }

    @FXML
    void clickedBtnSort_OurItems(MouseEvent event) { sortItems("OurItems"); }

    private void sortItems(String selectedTab) {
        String color = "";
        String priceStr = "";
        String type = "";
        if (selectedTab.equals("OurItems")) {
            color = cBox_color_OurItems.getValue();
            priceStr = cBox_price_OurItems.getValue();
            type = cBox_type_OurItems.getValue();
        } else if (selectedTab.equals("Custom")) {
            color = cBox_color_Custom.getValue();
            priceStr = cBox_price_Custom.getValue();
            type = cBox_type_Custom.getValue();
        }

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

        ObservableList<ItemInNewOrder> newList = FXCollections.observableArrayList();
        List<ItemInNewOrder> itemslist = null;
        if(selectedTab.equals("OurItems"))
            itemslist = itemsListInOurItemsTab;
        else if(selectedTab.equals("Custom"))
            itemslist = itemsListInCustomTab;
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
        GridPane newGridPane = new GridPane();
        int newGridPaneRow = 0;
        int newGridPaneCol = 0;
        for (ItemInNewOrder item : newList) {
            newGridPane.add(item.getItemInNewOrderVBoxVBox(), newGridPaneCol, newGridPaneRow);
            newGridPaneCol++;
            if (newGridPaneCol == 5) {
                newGridPaneCol = 0;
                newGridPaneRow++;
            }
        }
        newGridPane.setPadding(new Insets(10, 10, 10, 10));
        newGridPane.setHgap(10);
        newGridPane.setVgap(10);
        newGridPane.setStyle("-fx-background-color:  #f7b77c");
        if (selectedTab.equals("OurItems"))
            scrollPaneItems_OurItems.setContent(newGridPane);
        else if (selectedTab.equals("Custom"))
            scrollPaneItems_Custom.setContent(newGridPane);
    }

    public float getFullPrice() {
        return fullPrice;
    }

    public String getCreateCustomTxt() {
        return txt_createCustom.getText();
    }

    @FXML
    void clickedChooseItems_Custom(MouseEvent event) {
        lbl_msg.setText("");
        if (txt_createCustom.getText().equals(""))
            lbl_msg.setText("Please describe custom order requirements in the text field above");
        else {
            itemsInNewOrderList.add(new NewOrder(txt_createCustom.getText(), new ArrayList<>()));
            customRequest = txt_createCustom.getText();
            cBox_editItems_Custom.getItems().add(customRequest);
            tab_custom.setContent(anchorPaneItems_Custom);
        }
    }

    @FXML
    void clickedEditCustomOrCreateAnotherOneBtn(MouseEvent event) {
        ObservableList<ItemInNewOrder> list = FXCollections.observableArrayList();
        tab_custom.setContent(anchorPane_CreateOrEdit);
    }

    @FXML
    void clickedEditbtn_Custom(MouseEvent event) {

    }

    @FXML
    void clickedProceedWithTheOrderBtn(MouseEvent event) {
        lbl_msg.setText("");
        if (fullPrice == 0)
            lbl_msg.setText("No items in order");
        else {

        }
    }

    @FXML
    void enteredBackBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_back);
    }

    @FXML
    void enteredBtnSort_Custom(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_Sort_Custom);
    }

    @FXML
    void enteredBtnSort_OurItems(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_sort_OurItems);
    }

    @FXML
    void enteredChooseItems_Custom(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_chooseItems_Custom);
    }

    @FXML
    void enteredEditCustomOrCreateAnotherOneBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_editCustomOrCreateAnotherOne_Custom);
    }

    @FXML
    void enteredEditbtn_Custom(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_editCustom);

    }

    @FXML
    void enteredProceedWithTheOrderBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_proceedWithTheOrder);
    }

    @FXML
    void leavedBackBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_back);

    }

    @FXML
    void leavedBtnSort_Custom(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_Sort_Custom);

    }

    @FXML
    void leavedBtnSort_OurItems(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_sort_OurItems);

    }

    @FXML
    void leavedChooseItems_Custom(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_chooseItems_Custom);

    }

    @FXML
    void leavedEditCustomOrCreateAnotherOneBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_editCustomOrCreateAnotherOne_Custom);

    }

    @FXML
    void leavedEditbtn_Custom(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_editCustom);

    }

    @FXML
    void leavedProceedWithTheOrderBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_proceedWithTheOrder);
    }
}
