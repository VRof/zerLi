package commonClasses;

import java.sql.Timestamp;

/**
 *
 *  order cancellation data class
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */

public class CancellationRequest {
    private int orderID;
    private String firstName;
    private String lastName;
    private String status;
    private double price;

    private Timestamp requestDate;
    private Timestamp DeliveryDate;
    private String shop;

    public CancellationRequest(int orderID, String firstName, String lastName, String status, double price, Timestamp DeliveryDate, Timestamp requestDate) {
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

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
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
