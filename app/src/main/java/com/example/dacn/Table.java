package com.example.dacn;

public class Table {
    private int numberTable;
    private boolean status;

    public Table(int numberTable, boolean status) {
        this.numberTable = numberTable;
        this.status = status;
    }

    public int getNumberTable() {
        return numberTable;
    }

    public void setNumberTable(int numberTable) {
        this.numberTable = numberTable;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
