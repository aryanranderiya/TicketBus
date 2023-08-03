package com.example.ticketbus.Model;

public class CardModel {

    int CardBalance;
    String CardHolder, CardPin, CardNumber;
    boolean visibility;


    public CardModel() {
    }

    public CardModel(String cardHolder, String cardNumber, int cardBalance, String cardPin) {
        CardHolder = cardHolder;
        CardNumber = cardNumber;
        CardBalance = cardBalance;
        CardPin = cardPin;
        visibility = false;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public String getCardHolder() {
        return CardHolder;
    }

    public void setCardHolder(String cardHolder) {
        CardHolder = cardHolder;
    }

    public String getCardNumber() {
        return CardNumber;
    }

    public void setCardNumber(String cardNumber) {
        CardNumber = cardNumber;
    }

    public int getCardBalance() {
        return CardBalance;
    }

    public void setCardBalance(int cardBalance) {
        CardBalance = cardBalance;
    }

    public String getCardPin() {
        return CardPin;
    }

    public void setCardPin(String cardPin) {
        CardPin = cardPin;
    }
}
