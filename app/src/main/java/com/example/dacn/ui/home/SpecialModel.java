package com.example.dacn.ui.home;

public class SpecialModel {
    private String docID;
    private String name;
    private String noOfTests;
    private String img;

    public SpecialModel(String docID, String name, String noOfTests, String img) {
        this.docID = docID;
        this.name = name;
        this.noOfTests = noOfTests;
        this.img = img;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNoOfTests() {
        return noOfTests;
    }

    public void setNoOfTests(String noOfTests) {
        this.noOfTests = noOfTests;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
