package com.example.ticketbus.Model;

public class TicketModel {

    int AdultCount, ChildCount, TotalPassenger, TicketPrice;
    boolean IsValidated;
    String BookingDate, ExpireDate, FromLocation, ToLocation, TicketID, ValidUpto;

    public TicketModel() {
    }

    public TicketModel(int adultCount, int childCount, int totalPassenger, int ticketPrice, boolean isValidated, String bookingDate, String expireDate, String fromLocation, String toLocation, String ticketID, String validUpto) {
        AdultCount = adultCount;
        ChildCount = childCount;
        TotalPassenger = totalPassenger;
        TicketPrice = ticketPrice;
        IsValidated = isValidated;
        BookingDate = bookingDate;
        ExpireDate = expireDate;
        FromLocation = fromLocation;
        ToLocation = toLocation;
        TicketID = ticketID;
        ValidUpto = validUpto;
    }

    public int getAdultCount() {
        return AdultCount;
    }

    public void setAdultCount(int adultCount) {
        AdultCount = adultCount;
    }

    public int getChildCount() {
        return ChildCount;
    }

    public void setChildCount(int childCount) {
        ChildCount = childCount;
    }

    public int getTotalPassenger() {
        return TotalPassenger;
    }

    public void setTotalPassenger(int totalPassenger) {
        TotalPassenger = totalPassenger;
    }

    public int getTicketPrice() {
        return TicketPrice;
    }

    public void setTicketPrice(int ticketPrice) {
        TicketPrice = ticketPrice;
    }

    public boolean isValidated() {
        return IsValidated;
    }

    public void setValidated(boolean validated) {
        IsValidated = validated;
    }

    public String getBookingDate() {
        return BookingDate;
    }

    public void setBookingDate(String bookingDate) {
        BookingDate = bookingDate;
    }

    public String getExpireDate() {
        return ExpireDate;
    }

    public void setExpireDate(String expireDate) {
        ExpireDate = expireDate;
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

    public String getTicketID() {
        return TicketID;
    }

    public void setTicketID(String ticketID) {
        TicketID = ticketID;
    }

    public String getValidUpto() {
        return ValidUpto;
    }

    public void setValidUpto(String validUpto) {
        ValidUpto = validUpto;
    }


}
