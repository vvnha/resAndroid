package com.example.dacn.ui.user;

import java.io.Serializable;

public class User  implements Serializable {
    private int id;
    private String name;
    private String mail;
    private String phone;
    private int positionID;

    public User(int id, String name, String mail, String phone, int positionID) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.phone = phone;
        this.positionID = positionID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getPositionID() {
        return positionID;
    }

    public void setPositionID(int positionID) {
        this.positionID = positionID;
    }

}
