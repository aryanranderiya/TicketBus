package com.example.ticketbus.Model;

public class BusPassModel {

    private int image;
    private String title, type;

    public BusPassModel() {
    }

    public BusPassModel(int image, String title) {
        this.image = image;
        this.title = title;
    }

    public BusPassModel(int image, String title, String type) {
        this.image = image;
        this.title = title;
        this.type = type;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
