package clientClasses;

import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ItemInCatalog {
    private VBox itemInCatalogVBox;
    private int id;
    private String isBundle;
    private String itemColor;
    private float itemPrice;

    public ItemInCatalog(int id, ImageView itemImage, String itemName, float itemPrice,String itemColor, String isBundle) {
        this.id = id;
        this.isBundle = isBundle;
        this.itemPrice = itemPrice;
        this.itemColor = itemColor;
        Text lbl_itemName = new Text(itemName);
        lbl_itemName.setStyle("-fx-font-size: 15");
        Text lbl_itemPrice = new Text(itemPrice + " ₪");
        lbl_itemPrice.setStyle("-fx-font-size: 15");
        itemInCatalogVBox = new VBox(5);
        itemInCatalogVBox.getChildren().addAll(itemImage, lbl_itemName, lbl_itemPrice);
        itemInCatalogVBox.setSpacing(10);
        itemInCatalogVBox.setAlignment(Pos.CENTER);
        itemInCatalogVBox.setStyle("-fx-background-color:  #b6f2e0");
    }

    public String getItemColor() {
        return itemColor;
    }

    public float getItemPrice() {
        return itemPrice;
    }

    public int getId() {
        return id;
    }

    public boolean isBundle() {
        return isBundle.equals("true");
    }

    public VBox getItemInCatalogVBox() {
        return itemInCatalogVBox;
    }
}
