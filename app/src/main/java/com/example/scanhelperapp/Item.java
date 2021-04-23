package com.example.scanhelperapp;


public class Item {
    private String name;
    private String barcode = "";
    public int quantity = 0;

    public Item() { }

    public Item(String name) { this.name = name; }

    public Item(String name, String barcode, int quantity) {
        this.name = name;
        this.barcode = barcode;
        this.quantity = quantity;
    }

    public String getName() { return this.name; }
    public String getBarcode() { return this.barcode; }
    public int getQuantity() { return this.quantity; }

    public void setName(String name) { this.name = name; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public void setQuantity(int quantity) { this.quantity = quantity; }


}
