package com.example.ticketbus.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ticketbus.ActiveTicketFragment;
import com.example.ticketbus.ExpiredTicket;

public class MyFragmentAdapter extends FragmentStateAdapter {



    public MyFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1){
            return new ExpiredTicket();
        }
        return new ActiveTicketFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}