package com.example.ticketbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ticketbus.Adapters.BusAdapter;
import com.example.ticketbus.Model.BusItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BusSearch extends AppCompatActivity {

    private TextView txt_fromLoc, txt_toLoc;
    private String fromLocation,toLocation;
    DatabaseReference databaseReference;

    private List<BusItem> busList;
    private BusAdapter busAdapter;

    private LinearLayout layout;

    private RecyclerView recyclerView;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_search);

        Initialize();
        FirebaseDataRetrieve();

        Intent i = getIntent();
        fromLocation = i.getStringExtra("fromLocation");
        toLocation = i.getStringExtra("toLocation");

        txt_fromLoc.setText(fromLocation);
        txt_toLoc.setText(toLocation);

        busList = new ArrayList<>();

        busAdapter = new BusAdapter(busList, getApplicationContext());

        recyclerView.setAdapter(busAdapter);




        }

    private void FirebaseDataRetrieve() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                busList.clear();
                if(snapshot.exists()){
                    for (DataSnapshot fromToSnapshot : snapshot.getChildren()){

                        if (fromToSnapshot.getKey().equals(fromLocation+toLocation)){
                            for (DataSnapshot busNoSnapshot : fromToSnapshot.getChildren()){

                                if (busNoSnapshot.exists()){
                                    BusItem busItem = busNoSnapshot.getValue(BusItem.class);
                                    busList.add(busItem);
                                }
                                else {
//                                                Toast.makeText(getActivity(), "No Bus Found", Toast.LENGTH_SHORT).show();
                                    break;
                                }

                            }
                        }
                    }
                    busAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void Initialize() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        txt_fromLoc = findViewById(R.id.txt_fromLoc);
        txt_toLoc = findViewById(R.id.txt_toLoc);

        layout = findViewById(R.id.FDLLM);

        recyclerView = findViewById(R.id.Bus_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        databaseReference = FirebaseDatabase.getInstance().getReference("Buses");
    }
}