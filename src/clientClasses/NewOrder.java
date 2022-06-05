package clientClasses;

import javafx.scene.layout.GridPane;

import java.util.List;

/**
 *
 *  new order, item in new order, item can be custom(list of items from catalog) or item from catalog,
 *  if it's custom, saves all items in itemsInCustomBuild list,if not saves item in catalogItem
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */

public class NewOrder {

    private boolean isCustom;
    private String customBuildRequest;
    private ItemInNewOrder catalogItem;
    private List<ItemInNewOrder> itemsInCustomBuild;
    private GridPane itemsGrid; //save FridPane of custom build

    public NewOrder(ItemInNewOrder catalogItem){
        this.catalogItem = catalogItem;
        this.isCustom = false;
    }
    public NewOrder(GridPane itemsGrid, String customBuildRequest,List<ItemInNewOrder> itemsInCustomBuild){
        this.itemsGrid = itemsGrid;
        this.customBuildRequest = customBuildRequest;
        this.itemsInCustomBuild = itemsInCustomBuild;
        this.isCustom = true;
    }
    public boolean isCustom() {
        return isCustom;
    }
    public String getCustomBuildRequest() {
        return customBuildRequest;
    }

    public ItemInNewOrder getCatalogItem() {
        return catalogItem;
    }

    public List<ItemInNewOrder> getItemsInCustomBuild() {
        return itemsInCustomBuild;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    public void setCatalogItem(ItemInNewOrder catalogItem) {
        this.catalogItem = catalogItem;
    }

    public GridPane getItemsGrid() {
        return itemsGrid;
    }

    public double getPrice(){
        float totalPrice = 0;
        if(!isCustom)
            return catalogItem.getItemPrice() * catalogItem.getQuantity();
        else{ //if custom
            for(ItemInNewOrder item : itemsInCustomBuild) //get price of each item in list
                totalPrice += item.getItemPrice()* item.getQuantity();
        }
        return totalPrice;
    }
}
