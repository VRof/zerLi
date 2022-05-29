package client;

import java.io.Serializable;

public class Quarter implements Serializable {


    private String shop;
    private String month1;
    private String month2;
    private String month3;
    private double result1=0;
    private double result2=0;
    private double result3=0;

    private int year;


    private int quarter;

    public Quarter() {
    }

    public Quarter(int year, int quarter,String shop){
        this.year=year;
        this.quarter=quarter;
        this.shop=shop;

    }

    public Quarter(String month1, String month2, String month3, double result1, double result2, double result3) {
        this.month1 = month1;
        this.month2 = month2;
        this.month3 = month3;
        this.result1 = result1;
        this.result2 = result2;
        this.result3 = result3;

    }

    public String getMonth1() {
        return month1;
    }

    public void setMonth1(String month1) {
        this.month1 = month1;
    }

    public String getMonth2() {
        return month2;
    }

    public void setMonth2(String month2) {
        this.month2 = month2;
    }

    public String getMonth3() {
        return month3;
    }

    public void setMonth3(String month3) {
        this.month3 = month3;
    }

    public double getResult1() {
        return result1;
    }

    public void setResult1(double result1) {
        this.result1 = result1;
    }

    public double getResult2() {
        return result2;
    }

    public void setResult2(double result2) {
        this.result2 = result2;
    }

    public double getResult3() {
        return result3;
    }

    public void setResult3(double result3) {
        this.result3 = result3;
    }
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
    public int getQuarter() {
        return quarter;
    }

    public void setQuarter(int quarter) {
        this.quarter = quarter;
    }
    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }


}
