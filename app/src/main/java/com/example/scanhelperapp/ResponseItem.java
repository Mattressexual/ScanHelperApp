package com.example.scanhelperapp;

// Object to define all possible attributes of JSON Object returned in GET Request response.
public class ResponseItem {
    public String ean;
    public String title;
    public String upc;
    public String gtin;
    public String elid;
    public String description;
    public String brand;
    public String model;
    public String color;
    public String size;
    public String dimension;
    public String weight;
    public String category;
    public String currency;
    public double lowest_recorded_price;
    public double highest_recorded_price;
    public String user_data;

    public ResponseItem() {

    }

    // TODO: Make getters and setters? All public so it doesn't really matter, but still.
}
