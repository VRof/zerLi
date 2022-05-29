package clientClasses;
import java.sql.Timestamp;
public class Delivery {

    private int deliveryGuyID;
    private int orderNumber;
    private String customerName;
    private String customerID;



    private String address;
    private double price;
    private String telephoneNumber;
    private Timestamp deliveryDate;
    private String confirmed;

    public Delivery(){}
    public Delivery(int deliveryGuyID, int orderNumber, String customerName, String customerID, String address, double price, String telephoneNumber, Timestamp deliveryDate, String confirmed) {
        this.deliveryGuyID = deliveryGuyID;
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.customerID = customerID;
        this.address = address;
        this.price = price;
        this.telephoneNumber = telephoneNumber;
        this.deliveryDate = deliveryDate;
        this.confirmed = confirmed;
    }
    public String getAddress() { return address;}

    public void setAddress(String address) {this.address = address; }
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
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
