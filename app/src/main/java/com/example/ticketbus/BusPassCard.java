package com.example.ticketbus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.ticketbus.Adapters.BusPassAdapter;
import com.example.ticketbus.Model.BusPassModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;


public class BusPassCard extends AppCompatActivity {

    RecyclerView recyclerView;
    BusPassAdapter busPassAdapter;

    List<BusPassModel> busPassModels;
//    String busPass[] = {"1\nMonth", "3\n Month", "6\nMonth", "12\nMonth"};

    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_pass_card);

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

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        busPassModels = new ArrayList<>();
        busPassModels.add(new BusPassModel(R.drawable.one_month_card, "1\nMonth","1 Month"));
        busPassModels.add(new BusPassModel(R.drawable.three_month_card, "3\nMonth","3 Month"));
        busPassModels.add(new BusPassModel(R.drawable.six_month_card, "6\nMonth","6 Month"));
        busPassModels.add(new BusPassModel(R.drawable.twelve_month_card, "12\nMonth","12 Month"));

        busPassAdapter = new BusPassAdapter(busPassModels, this);
        recyclerView.setAdapter(busPassAdapter);
        recyclerView.setPadding(100,0,100,0);

        ScrollingPagerIndicator recyclerIndicator = findViewById(R.id.indicator);
        recyclerIndicator.attachToRecyclerView(recyclerView);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RecyclerView.ViewHolder viewHoler = recyclerView.findViewHolderForAdapterPosition(pos);
                RelativeLayout rl1 = viewHoler.itemView.findViewById(R.id.rL1);
                rl1.animate().setDuration(100).scaleX(1).scaleY(1).setInterpolator(new AccelerateInterpolator()).start();
            }
        },100);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                View v = snapHelper.findSnapView(layoutManager);
                pos = layoutManager.getPosition(v);

                RecyclerView.ViewHolder viewHoler = recyclerView.findViewHolderForAdapterPosition(pos);
                RelativeLayout rl1 = viewHoler.itemView.findViewById(R.id.rL1);

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    rl1.animate().setDuration(100).scaleX(1).scaleY(1).setInterpolator(new AccelerateInterpolator()).start();
                }else{
                    rl1.animate().setDuration(100).scaleX(0.9f).scaleY(0.9f).setInterpolator(new AccelerateInterpolator()).start();

                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }
}