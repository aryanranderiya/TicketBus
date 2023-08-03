package com.example.ticketbus.Interface;
import com.example.ticketbus.Model.IDs;
import com.example.ticketbus.Model.IDs;

import java.util.List;

public interface IFirebaseLoadDone {
    void onFirebaseLoadSuccess(List<IDs> LocationList);
    void onFirebaseLoadFailed(String Message);
}

