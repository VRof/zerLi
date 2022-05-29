package clientClasses;

import java.sql.Timestamp;

public class CancellationRequest {
    private int orderID;
    private String firstName;
    private String lastName;
    private String status;
    private double price;

    private Timestamp requestDate;
    private Timestamp DeliveryDate;

    public CancellationRequest(int orderID, String firstName, String lastName, String status, double price,Timestamp DeliveryDate, Timestamp requestDate) {
        this.orderID = orderID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.price = price;
        this.DeliveryDate= DeliveryDate;
        this.requestDate= requestDate;
    }

    public CancellationRequest() {
    }


    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }

    public Timestamp getDeliveryDate() {
        return DeliveryDate;
    }

    public void setDeliveryDate(Timestamp DeliveryDate) {
        this.DeliveryDate = DeliveryDate;
    }
}
