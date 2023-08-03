package com.example.ticketbus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.Objects;

public class CreateBusPassActivity extends AppCompatActivity {

    Button btn_continue;

    ImageButton image_btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bus_pass);

        initialize();
        buttons();
    }

    private void buttons() {

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), BusPassCard.class));
                finish();
            }
        });

        image_btn_back.setOnClickListener(view -> finish());
    }

    private void initialize() {

        btn_continue = findViewById(R.id.btn_card_continue);
        image_btn_back = findViewById(R.id.btn_back);
    }
}