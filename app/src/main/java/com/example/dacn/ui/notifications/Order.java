package com.example.dacn.ui.notifications;

public class Order {

    private int orderID;
    private int userID;
    private String total;
    private String orderDate;
    private String perNum;
    private String service;
    private String dateClick;

    public Order(int orderID, int userID, String total, String orderDate, String perNum, String service, String dateClick) {
        this.orderID = orderID;
        this.userID = userID;
        this.total = total;
        this.orderDate = orderDate;
        this.perNum = perNum;
        this.service = service;
        this.dateClick = dateClick;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getPerNum() {
        return perNum;
    }

    public void setPerNum(String perNum) {
        this.perNum = perNum;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDateClick() {
        return dateClick;
    }

    public void setDateClick(String dateClick) {
        this.dateClick = dateClick;
    }
}
