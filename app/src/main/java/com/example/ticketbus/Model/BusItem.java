package com.example.ticketbus.Model;

public class BusItem {

    private String BusNo, TicketPrice, FromLocation, ToLocation, StartTime, EndTime;

    public BusItem() {
    }

    public BusItem(String busNo, String ticketPrice, String fromLocation, String toLocation, String startTime, String endTime) {
        BusNo = busNo;
        TicketPrice = ticketPrice;
        FromLocation = fromLocation;
        ToLocation = toLocation;
        StartTime = startTime;
        EndTime = endTime;
    }

    public String getBusNo() {
        return BusNo;
    }

    public void setBusNo(String busNo) {
        BusNo = busNo;
    }

    public String getTicketPrice() {
        return TicketPrice;
    }

    public void setTicketPrice(String ticketPrice) {
        TicketPrice = ticketPrice;
    }

    public String getFromLocation() {
        return FromLocation;
    }

    public void setFromLocation(String fromLocation) {
        FromLocation = fromLocation;
    }

    public String getToLocation() {
        return ToLocation;
    }

    public void setToLocation(String toLocation) {
        ToLocation = toLocation;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }
}
