package clientClasses;

import javafx.scene.layout.GridPane;

import java.util.List;

public class NewOrder {

    private boolean isCustom;
    private String customBuildRequest;
    private ItemInNewOrder catalogItem;
    private List<ItemInNewOrder> itemsInCustomBuild;
    private GridPane itemsGrid;

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

    public void setCustomBuildRequest(String customBuildRequest) {
        this.customBuildRequest = customBuildRequest;
    }

    public void setCatalogItem(ItemInNewOrder catalogItem) {
        this.catalogItem = catalogItem;
    }

    public void setItemsInCustomBuild(List<ItemInNewOrder> itemsInCustomBuild) {
        this.itemsInCustomBuild = itemsInCustomBuild;
    }

    public GridPane getItemsGrid() {
        return itemsGrid;
    }

    public double getPrice(){
        float totalPrice = 0;
        if(!isCustom)
            return catalogItem.getItemPrice() * catalogItem.getQuantity();
        else{
            for(ItemInNewOrder item : itemsInCustomBuild)
                totalPrice += item.getItemPrice()* item.getQuantity();
        }
        return totalPrice;
    }
}
