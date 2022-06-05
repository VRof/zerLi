package clientGUI;

import client.ClientController;
import clientClasses.ItemInCatalog;
import commonClasses.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.sql.rowset.CachedRowSet;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
/**
 *
 *  Controller class for ViewCatalogGUI.fxml file
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */

public class ViewCatalogGUIController {

    @FXML
    private ImageView btn_back;

    @FXML
    private ScrollPane scrollPaneBundles;

    @FXML
    private ScrollPane scrollPaneItems;


    @FXML
    private Label lbl_loadingMsg;

    @FXML
    private ComboBox<String> cBox_price;

    @FXML
    private ComboBox<String> cBox_color;

    @FXML
    private ImageView btn_sort;



    private GridPane gridPaneBundles; //grid pane for bundles
    private GridPane gridPaneFlowers; //greid pane for flowers
    private ObservableList<ItemInCatalog> itemsList; //list of all items in catalog from DB

    /**
     * initialize comboBoxes
     */
    @FXML
    public void initialize() {
        setCatalog();
        String[] colors = {"all", "red", "blue", "yellow", "purple", "pink", "white"};
        String[] prices = {"all", "< 10 ₪", "< 15 ₪", "< 20 ₪", "< 30 ₪"};
        cBox_color.setItems(FXCollections.observableArrayList(colors));
        cBox_price.setItems(FXCollections.observableArrayList(prices));
        cBox_color.setValue("all");
        cBox_price.setValue("all");
    }

    /**
     * clicked on back button
     * @param event mouse click
     */
    @FXML
    void clickedBack(MouseEvent event) {
        Stage thisStage = (Stage) btn_back.getScene().getWindow();
        thisStage.hide();
        ClientController.savedWindows.getClientGUIWindow().show(); //show main menu for customer
        CustomerGUIController.controller.initialize(); //reload user data
    }

    /**
     * sort items by chosen requirements in comboBoxes
     * @param event mouse click
     */
    @FXML
    void clickedBtnSort(MouseEvent event) {
        String color = cBox_color.getValue();
        String priceStr = cBox_price.getValue();
        //convert string to float:
        float price = 0;
        if (priceStr.equals("< 10 ₪"))
            price = 10;
        else if (priceStr.equals("< 15 ₪"))
            price = 15;
        else if (priceStr.equals("< 20 ₪"))
            price = 20;
        else if (priceStr.equals("< 30 ₪"))
            price = 30;
        else if (priceStr.equals("all"))
            price = Float.MAX_VALUE;

        ObservableList<ItemInCatalog> newList = FXCollections.observableArrayList();
        for (ItemInCatalog item : itemsList) {
            if (item.isBundle() == false) {
                if (color.equals("all") && item.getItemPrice() < price)
                    newList.add(item);
                else if (item.getItemPrice() < price && color.equals(item.getItemColor()))
                    newList.add(item);
            }
        }
        gridPaneFlowers = new GridPane();
        int itemsGridPaneRow = 0;
        int itemsGridPaneCol = 0;
        for (ItemInCatalog item : newList) { //add items to gridPane
            gridPaneFlowers.add(item.getItemInCatalogVBox(), itemsGridPaneCol, itemsGridPaneRow);
            itemsGridPaneCol++;
            if (itemsGridPaneCol == 5) {//new row
                itemsGridPaneCol = 0;
                itemsGridPaneRow++;
            }
        }
        gridPaneFlowers.setPadding(new Insets(10, 10, 10, 10));
        gridPaneFlowers.setHgap(10);
        gridPaneFlowers.setVgap(10);
        gridPaneFlowers.setStyle("-fx-background-color:  #f7b77c");
        scrollPaneItems.setContent(gridPaneFlowers);
    }

    /**
     * thread method, get all items from database
     */
    private void setCatalog() {
        lbl_loadingMsg.setText("Loading catalog, please wait..."); //message to user that catalog is loading
        Platform.runLater(new Runnable() { //thread to build catalog without being "stuck" as it can be looked because of the loading time of catalog (pictures at most)
            @Override
            public void run() {
                Message msg = new Message();
                msg.setCommand("getCatalog");
                ClientController.getClientController().send(msg);
                while (ClientController.awaitResponse) ;
                itemsList = FXCollections.observableArrayList();
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
                        //--------------------------------------------------------------------------
                        //add item to the list of items:
                        ItemInCatalog item = new ItemInCatalog(cachedset.getInt("id"), itemPic, cachedset.getString("name"), cachedset.getFloat("price"), cachedset.getString("color"), cachedset.getString("isBundle"));
                        itemsList.add(item);
                    }
                } catch (Exception e) {
                    System.out.println("error getting catalog data from server " + e);
                    e.printStackTrace();
                }

                /*
                 * ----------------------------------------build catalog UI from items we received-------------------------------------*/
                gridPaneBundles = new GridPane();
                gridPaneFlowers = new GridPane();
                int bundleGridPaneRow = 0;
                int bundleGridPaneCol = 0;
                int itemsGridPaneRow = 0;
                int itemsGridPaneCol = 0;
                for (ItemInCatalog item : itemsList) {
                    if (item.isBundle()) {
                        gridPaneBundles.add(item.getItemInCatalogVBox(), bundleGridPaneCol, bundleGridPaneRow);
                        bundleGridPaneCol++;
                        if (bundleGridPaneCol == 5) {
                            bundleGridPaneCol = 0;
                            bundleGridPaneRow++;
                        }
                    } else {
                        gridPaneFlowers.add(item.getItemInCatalogVBox(), itemsGridPaneCol, itemsGridPaneRow);
                        itemsGridPaneCol++;
                        if (itemsGridPaneCol == 5) {
                            itemsGridPaneCol = 0;
                            itemsGridPaneRow++;
                        }
                    }
                }
                gridPaneFlowers.setPadding(new Insets(10, 10, 10, 10));
                gridPaneFlowers.setHgap(10);
                gridPaneFlowers.setVgap(10);
                gridPaneFlowers.setStyle("-fx-background-color:  #f7b77c");
                scrollPaneItems.setContent(gridPaneFlowers);

                gridPaneBundles.setPadding(new Insets(10, 10, 10, 10));
                gridPaneBundles.setHgap(10);
                gridPaneBundles.setVgap(10);
                gridPaneBundles.setStyle("-fx-background-color:  #f7b77c");
                scrollPaneBundles.setContent(gridPaneBundles);

                lbl_loadingMsg.setText(""); //end of load
            }
        });
    }

    @FXML
    void enteredBackBtn(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_back);
    }

    @FXML
    void leavedBackBtn(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_back);
    }

    @FXML
    void enteredBtnSort(MouseEvent event) {
        ClientController.getClientController().enteredButton(btn_sort);
    }

    @FXML
    void leavedBtnSort(MouseEvent event) {
        ClientController.getClientController().leavedButton(btn_sort);
    }

}
