package clientClasses;

import clientGUI.NewOrderGUIController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ItemInNewOrder {
    private VBox itemInNewOrderVBox;
    private int id;
    private String isBundle;
    private String itemColor;
    private String itemName;
    private float itemPrice;
    private Integer quantity = 0;
    private Label lbl_quantity;
    private boolean itemInCustom;

    public ItemInNewOrder(int id, ImageView itemImage, String itemName, float itemPrice, String itemColor, String isBundle, boolean itemInCustom) {
        this.id = id;
        this.isBundle = isBundle;
        this.itemPrice = itemPrice;
        this.itemColor = itemColor;
        this.itemName = itemName;
        this.itemInCustom = itemInCustom;
        Text lbl_itemName = new Text(itemName);
        lbl_itemName.setStyle("-fx-font-size: 15");
        Text lbl_itemPrice = new Text(itemPrice + " ₪");
        lbl_itemPrice.setStyle("-fx-font-size: 15");

        Button btn_minus = new Button("-");
        Button btn_plus = new Button("+");
        Button btn_minus10 = new Button("-10");
        Button btn_plus10 = new Button("+10");
        btn_minus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (quantity > 0) {
                    quantity--;
                    lbl_quantity.setText("" + quantity);
                }
                if (!itemInCustom)
                    updateOrderFromCatalog();
                else
                    updateCustomOrder();
            }
        });
        btn_plus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                quantity++;
                lbl_quantity.setText("" + quantity);
                if (!itemInCustom)
                    updateOrderFromCatalog();
                else
                    updateCustomOrder();
            }
        });
        btn_minus10.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (quantity > 9)
                    quantity -= 10;
                else
                    quantity = 0;
                lbl_quantity.setText("" + quantity);
                if (!itemInCustom)
                    updateOrderFromCatalog();
                else
                    updateCustomOrder();
            }
        });
        btn_plus10.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                quantity += 10;
                lbl_quantity.setText("" + quantity);
                if (!itemInCustom)
                    updateOrderFromCatalog();
                else
                    updateCustomOrder();
            }
        });


        btn_minus.setPrefSize(30, 20);
        btn_plus.setPrefSize(30, 20);
        btn_minus10.setPrefSize(40, 20);
        btn_plus10.setPrefSize(40, 20);
        lbl_quantity = new Label("0");

        HBox quantityHBox = new HBox();
        quantityHBox.getChildren().addAll(btn_minus10, btn_minus, lbl_quantity, btn_plus, btn_plus10);
        quantityHBox.setSpacing(5);
        quantityHBox.setAlignment(Pos.CENTER);

        itemInNewOrderVBox = new VBox();
        itemInNewOrderVBox.getChildren().addAll(itemImage, lbl_itemName, lbl_itemPrice, quantityHBox);
        itemInNewOrderVBox.setSpacing(10);
        itemInNewOrderVBox.setAlignment(Pos.CENTER);
        itemInNewOrderVBox.setStyle("-fx-background-color:  #b6f2e0");
    }

    private void updateOrderFromCatalog() {
        for (NewOrder catalogItemInOrder : NewOrderGUIController.controller.getItemsInNewOrderList()) {
            if (!catalogItemInOrder.isCustom() && catalogItemInOrder.getCatalogItem() == this) {
                if (quantity == 0) {
                    NewOrderGUIController.controller.getItemsInNewOrderList().remove(catalogItemInOrder);
                } else {
                    catalogItemInOrder.setCatalogItem(this);
                }
                NewOrderGUIController.controller.printFullOrder();
                return;
            }
        }
        if (quantity > 0)
            NewOrderGUIController.controller.getItemsInNewOrderList().add(new NewOrder(this));
        NewOrderGUIController.controller.printFullOrder();
    }

    @Override
    public boolean equals(Object other){
    return ((ItemInNewOrder)other).id == this.id;
    }

    private void updateCustomOrder() {
        for (NewOrder customBuildInOrder : NewOrderGUIController.controller.getItemsInNewOrderList()) {
            if(customBuildInOrder.isCustom() && customBuildInOrder.getCustomBuildRequest().equals(NewOrderGUIController.controller.getCustomRequest())){
                if(!customBuildInOrder.getItemsInCustomBuild().contains(this) && quantity>0)
                    customBuildInOrder.getItemsInCustomBuild().add(this);
                else if(customBuildInOrder.getItemsInCustomBuild().contains(this) && quantity==0)
                    customBuildInOrder.getItemsInCustomBuild().remove(this);
                NewOrderGUIController.controller.printFullOrder();
                return;
            }
        }
    }

    public String getItemColor() {
        return itemColor;
    }

    public float getItemPrice() {
        return itemPrice * quantity;
    }

    public int getId() {
        return id;
    }

    public boolean isBundle() {
        return isBundle.equals("true");
    }

    public VBox getItemInNewOrderVBoxVBox() {
        return itemInNewOrderVBox;
    }

    @Override
    public String toString() {
        String str = "";
        str = str + itemName + " ";
        str = str + "x" + quantity;
        str = str + " price: " + quantity * itemPrice + "₪";
        return str;

    }
}
