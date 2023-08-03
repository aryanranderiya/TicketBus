package com.example.ticketbus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;

public class TicketOptions extends AppCompatActivity {

    String fromLoc, toLoc;
    int totalPrice, unitTicketPrice;
    TextView txtAdult, txtChild;

    int adultCount = 1, childCount = 0;

    Button btn_proceed;

    BottomSheetDialog bottomSheetDialog;

    ImageButton btn_adultRemove, btn_adultAdd, btn_childRemove, btn_childAdd;

    TextView TO_busNo, TO_price, TO_fromLoc, TO_toLoc, TO_startTime, TO_endTime;

    View sheetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_options);

        initialize();


        TO_busNo.setText(getIntent().getStringExtra("busNo"));
        TO_price.setText("₹ "+getIntent().getStringExtra("price"));
        TO_fromLoc.setText(getIntent().getStringExtra("fromLoc"));
        TO_toLoc.setText(getIntent().getStringExtra("toLoc"));
        TO_startTime.setText(getIntent().getStringExtra("startTime"));
        TO_endTime.setText(getIntent().getStringExtra("endTime"));

        fromLoc = getIntent().getStringExtra("fromLoc");
        toLoc = getIntent().getStringExtra("toLoc");

        unitTicketPrice = Integer.parseInt(getIntent().getStringExtra("price"));
        totalPrice = unitTicketPrice;

        setDisable(btn_adultRemove);
        setDisable(btn_childRemove);

        Buttons();
    }

    private void Buttons() {

        btn_adultAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                adultCount++;

                txtAdult.setText(""+adultCount);

                checkCount();
                checkPrice();
            }
        });

        btn_adultRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adultCount--;

                txtAdult.setText(""+adultCount);

                checkCount();
                checkPrice();

            }
        });

        btn_childAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                childCount++;

                txtChild.setText(""+childCount);

                checkCount();
                checkPrice();

            }
        });

        btn_childRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                childCount--;

                txtChild.setText(""+childCount);

                checkCount();
                checkPrice();

            }
        });

        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog = new BottomSheetDialog(TicketOptions.this, R.style.BottomSheetDialogTheme);
                 View v = getLayoutInflater().inflate(R.layout.bottom_sheet_payment_layout, null);
                bottomSheetDialog.setContentView(v);

                TextView txt_adultCount, txt_childCount, txt_totalPassenger, txt_totalPrice;

                txt_adultCount = bottomSheetDialog.findViewById(R.id.txt_adultCount);
                txt_childCount = bottomSheetDialog.findViewById(R.id.txt_childCount);
                txt_totalPassenger = bottomSheetDialog.findViewById(R.id.txt_totalPassengers);
                txt_totalPrice = bottomSheetDialog.findViewById(R.id.txt_totalPrice);

                txt_adultCount.setText(""+adultCount);
                txt_childCount.setText(""+childCount);
                txt_totalPassenger.setText(""+(adultCount+childCount));
                txt_totalPrice.setText("₹ "+totalPrice);

                bottomSheetDialog.show();

                //button dialog proceed
                Button btn_Bottomproceed;
                btn_Bottomproceed = bottomSheetDialog.findViewById(R.id.btn_proceed);


                btn_Bottomproceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int t_adultCount, t_childCount, t_totalPassenger, t_totalPrice;

                        t_adultCount = adultCount;
                        t_childCount = childCount;
                        t_totalPassenger = t_adultCount + t_childCount;
                        t_totalPrice = totalPrice;

                        Intent ticketDetails = new Intent(getApplicationContext(),Payment.class);
                        ticketDetails.putExtra("ticketPrice", t_totalPrice);

                        ticketDetails.putExtra("totalPassenger", t_totalPassenger);
                        ticketDetails.putExtra("adultCount", t_adultCount);
                        ticketDetails.putExtra("childCount", t_childCount);
                        ticketDetails.putExtra("totalPrice", t_totalPrice);
                        ticketDetails.putExtra("fromLoc", fromLoc);
                        ticketDetails.putExtra("toLoc", toLoc);

                        startActivity(ticketDetails);

                        finish();
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.setCanceledOnTouchOutside(true);
            }
        });
    }

    private void checkPrice() {

        int adultTotal = adultCount*unitTicketPrice;
        int childTotal = childCount*(unitTicketPrice/ 2);
        totalPrice = (adultTotal+childTotal);

        TO_price.setText("₹ "+totalPrice);
    }

    private void checkCount() {
        if (adultCount == 1){
            setDisable(btn_adultRemove);
        }
        else{
            setEnable(btn_adultRemove);
        }

        if (adultCount == 5){
            setDisable(btn_adultAdd);
        }
        else{
            setEnable(btn_adultAdd);
        }

        if (childCount == 0){
            setDisable(btn_childRemove);
        }
        else{
            setEnable(btn_childRemove);
        }

        if (childCount == 5){
            setDisable(btn_childAdd);
        }
        else{
            setEnable(btn_childAdd);
        }
    }

    private void initialize() {
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

        TO_busNo = findViewById(R.id.selectedBusNumber);
        TO_price = findViewById(R.id.selectedTicketPrice);
        TO_fromLoc = findViewById(R.id.selectedFromLoci);
        TO_toLoc = findViewById(R.id.selectedToLoci);
        TO_startTime = findViewById(R.id.selectedStartTime);
        TO_endTime = findViewById(R.id.selectedEndTime);

        txtAdult = findViewById(R.id.txtAdult);
        txtChild = findViewById(R.id.txtChild);

        txtAdult.setText(""+adultCount);
        txtChild.setText(""+childCount);

        btn_adultRemove = findViewById(R.id.btn_adultRemove);
        btn_adultAdd = findViewById(R.id.btn_adultAdd);
        btn_childRemove = findViewById(R.id.btn_childRemove);
        btn_childAdd = findViewById(R.id.btn_childAdd);

        btn_proceed = findViewById(R.id.btn_proceed);
    }

    private void setDisable(ImageButton imgButton) {
        imgButton.setEnabled(false);
        imgButton.setColorFilter(R.color.grey);
    }

    private void setEnable(ImageButton imgButton) {
        imgButton.setEnabled(true);
        imgButton.setBackgroundResource(R.drawable.main_button_bg_white);
        imgButton.setColorFilter(null);

    }
}