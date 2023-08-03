package com.example.ticketbus;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.ticketbus.Adapters.TicketAdapter;
import com.example.ticketbus.Model.TicketModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExpiredTicket extends Fragment {

    Context context;
    private RecyclerView expiredTicketRecycler;
    private TicketAdapter expiredTicketAdapter;
    private List<TicketModel> ticketList;
    DatabaseReference reference;
    private FirebaseUser currentUser;
    String UserID="";
    LinearLayout layout, layoutNoTickets;
    private View view;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_expired_ticket, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        UserID = currentUser.getUid();

        ticketList = new ArrayList<>();
        layout = view.findViewById(R.id.expiredTicketLLM);
        layoutNoTickets = view.findViewById(R.id.layoutNoTickets);
        expiredTicketRecycler = view.findViewById(R.id.expiredTicketRecycler);

        expiredTicketRecycler.setLayoutManager(new LinearLayoutManager(context));

        expiredTicketAdapter = new TicketAdapter(context, ticketList);

        expiredTicketRecycler.setAdapter(expiredTicketAdapter);

        expiredTicketRecycler.setAlpha(0.5f);

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(UserID).child("TicketsBooked");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ticketList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    TicketModel ticketModel = dataSnapshot.getValue(TicketModel.class);
                    boolean isValidated = (boolean) dataSnapshot.child("IsValidated").getValue();

                    if (isValidated){
//                            Toast.makeText(context, ""+isValidated, Toast.LENGTH_SHORT).show();
                        ticketList.add(ticketModel);
                    }
                }
                expiredTicketAdapter.notifyDataSetChanged();
                checkTickets();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void checkTickets() {

        if (ticketList.size() == 0){
            expiredTicketRecycler.setVisibility(View.GONE);
            layoutNoTickets.setVisibility(View.VISIBLE);
        }
        else{
            expiredTicketRecycler.setVisibility(View.VISIBLE);
            layoutNoTickets.setVisibility(View.GONE);
        }
    }
}