package clientClasses;

import java.sql.Timestamp;

/**
 *
 *  Order confirmation data
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */

public class OrderToConfirm {
    private int orderID;
    private String firstName;
    private String lastName;

    private Timestamp deliveryDate;
    private String phone;
    private double price;
    private String orderDetails;

    public OrderToConfirm(int orderID, String firstName, String lastName, Timestamp deliveyDate, String phone, double price) {
        this.orderID = orderID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.deliveryDate = deliveyDate;
        this.phone = phone;
        this.price = price;
    }

    public OrderToConfirm() {

    }

    public String getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(String orderDetails) {
        this.orderDetails = orderDetails;
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

    public Timestamp getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Timestamp deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
