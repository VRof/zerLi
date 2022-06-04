package clientClasses;
import java.sql.Timestamp;
public class Delivery {

    private int deliveryGuyID;
    private int orderNumber;
    private double price;
    private Timestamp deliveryDate;
    private String confirmed;
    private String shop;

    public Delivery(int deliveryGuyID, int orderNumber,double price,String shop, Timestamp deliveryDate, String confirmed) {
        this.deliveryGuyID = deliveryGuyID;
        this.orderNumber = orderNumber;
        this.price = price;
        this.deliveryDate = deliveryDate;
        this.confirmed = confirmed;
        this.shop = shop;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }
    public int getDeliveryGuyID() {
        return deliveryGuyID;
    }

    public void setDeliveryGuyID(int deliveryGuyID) {
        this.deliveryGuyID = deliveryGuyID;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Timestamp getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Timestamp deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }



}
