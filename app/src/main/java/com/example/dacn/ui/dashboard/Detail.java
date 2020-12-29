package com.example.dacn.ui.dashboard;

public class Detail {
    private int detailID;
    private int orderID;
    private int foodID;
    private int qty;
    private String price;
    private String foodName;

    public Detail(int detailID, int orderID,  int foodID ,int qty, String price, String foodName) {
        this.detailID = detailID;
        this.orderID = orderID;
        this.foodID = foodID;
        this.qty = qty;
        this.price = price;
        this.foodName = foodName;
    }

    public Detail(int orderID, int foodID, int qty, String price, String foodName) {
        this.orderID = orderID;
        this.foodID = foodID;
        this.qty = qty;
        this.price = price;
        this.foodName = foodName;
    }

    public int getDetailID() {
        return detailID;
    }

    public void setDetailID(int detailID) {
        this.detailID = detailID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }


    public int getFoodID() {
        return foodID;
    }

    public void setFoodID(int foodID) {
        this.foodID = foodID;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }
}
