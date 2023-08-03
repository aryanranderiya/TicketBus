package com.example.ticketbus.Interface;

import androidx.recyclerview.widget.RecyclerView;

public interface CallBackItemTouch {

    void onSwiped(RecyclerView.ViewHolder viewHolder, int adapterPosition);
}
