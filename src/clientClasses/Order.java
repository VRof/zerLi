package clientClasses;

import java.sql.Timestamp;

public class Order {
    int orderNumber;
    double price;
    String bless;
    String color;
    String details;
    String shop;
    Timestamp orderDate;
    Timestamp deliveryDate;
    String status;

    public Order(int orderNumber, double price, String bless, String color, String details, String shop, Timestamp orderDate, Timestamp deliveryDate, String status) {
        this.orderNumber = orderNumber;
        this.price = price;
        this.bless = bless;
        this.color = color;
        this.details = details;
        this.shop = shop;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.status = status;
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

    public String getBless() {
        return bless;
    }

    public void setBless(String bless) {
        this.bless = bless;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public Timestamp getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Timestamp deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
