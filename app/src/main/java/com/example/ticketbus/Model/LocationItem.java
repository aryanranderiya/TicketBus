package com.example.ticketbus.Model;

public class LocationItem {

    private String Location_pin, Place;

    public LocationItem() {
    }

    public LocationItem(String location_pin, String place) {
        Location_pin = location_pin;
        Place = place;
    }

    public String getLocation_pin() {
        return Location_pin;
    }

    public void setLocation_pin(String Location_pin) {
        this.Location_pin = Location_pin;
    }

    public String getPlace() {
        return Place;
    }

    public void setPlace(String Place) {
        this.Place = Place;
    }
}
