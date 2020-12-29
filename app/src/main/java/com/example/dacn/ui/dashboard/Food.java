package com.example.dacn.ui.dashboard;

public class Food {
    private int foodID;
    private String foodName;
    private String img;
    private String price;
    private String ingres;
    private float rating;
    private int hits;
    private int parentID;

    public Food(int foodID, String foodName, String img, String price, String ingres, float rating, int hits, int parentID) {
        this.foodID = foodID;
        this.foodName = foodName;
        this.img = img;
        this.price = price;
        this.ingres = ingres;
        this.rating = rating;
        this.hits = hits;
        this.parentID = parentID;
    }

    public int getFoodID() {
        return foodID;
    }

    public void setFoodID(int foodID) {
        this.foodID = foodID;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getIngres() {
        return ingres;
    }

    public void setIngres(String ingres) {
        this.ingres = ingres;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }
}
