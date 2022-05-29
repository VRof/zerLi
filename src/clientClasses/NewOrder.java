package clientClasses;

import java.util.List;

public class NewOrder {

    private boolean isCustom;
    private String customBuildRequest;
    private ItemInNewOrder catalogItem;
    private List<ItemInNewOrder> itemsInCustomBuild;


    public NewOrder(ItemInNewOrder catalogItem){
        this.catalogItem = catalogItem;
        this.isCustom = false;
    }
    public NewOrder(String customBuildRequest,List<ItemInNewOrder> itemsInCustomBuild){
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

    public float getPrice(){
        float totalPrice = 0;
        if(!isCustom)
            return catalogItem.getItemPrice();
        else{
            for(ItemInNewOrder item : itemsInCustomBuild)
                totalPrice += item.getItemPrice();
        }
        return totalPrice;
    }
}
