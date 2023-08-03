package com.example.ticketbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ticketbus.Adapters.PaymentAdapter;
import com.example.ticketbus.Encryption.Decode;
import com.example.ticketbus.Encryption.Encode;
import com.example.ticketbus.Model.CardModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class PaymentBusPass extends AppCompatActivity {

    EditText edt_paymentCardPin;
    StorageReference QRstorageReference;
    int currentBalance, ticketPrice, totalPassenger, adultCount, childCount;
    boolean isLocked = false;
    String encodedUserCardPin;
    String enteredPaymentPin;
    Button btn_pay, btn_notHavingCard;
    DatabaseReference reference;
    private FirebaseUser currentUser;

    String UserID="";
    private String userName;

    private List<CardModel> cardModelList;

    private PaymentAdapter paymentAdapter;
    RecyclerView payment_recyclerView;

    LinearLayout notHavingCard;

    //TicketDetails
    String fromLoc, toLoc;

    String encryptedMergedDetails;

    String booked_ticketPrice, booked_AdultCount, bookedChildCount, booked_TotalPassenger;

    String busPassPrice;
    DialogPlus dialogPlus;

    int purchasedBusPassPrice;

    ImageButton toolbar_bus_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_bus_pass);

        toolbar_bus_pass = findViewById(R.id.btn_back_payment_bus_pass);

        toolbar_bus_pass.setOnClickListener(view -> finish());

        Intent i = getIntent();
        busPassPrice = i.getStringExtra("busPassPrice");
        purchasedBusPassPrice = Integer.parseInt(busPassPrice.trim());


        dialogPlus = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.custom_loading_dialog))
                .setContentBackgroundResource(Color.TRANSPARENT)
                .setGravity(Gravity.CENTER)
                .create();



        notHavingCard = findViewById(R.id.notHavingCard);
        btn_notHavingCard = findViewById(R.id.btn_notHavingCard);

        btn_pay = findViewById(R.id.btn_bus_pass_pay);
        btn_pay.setEnabled(false);

        LocalBroadcastManager.getInstance(this).registerReceiver(isLockedReciever,
                new IntentFilter("isLocked"));

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("payment_card_pin"));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        UserID = currentUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(UserID);

        payment_recyclerView = findViewById(R.id.payment_recyclerView);

        payment_recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        cardModelList = new ArrayList<>();

        paymentAdapter = new PaymentAdapter(getApplicationContext() ,cardModelList);

        payment_recyclerView.setAdapter(paymentAdapter);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Wallet")){
                    fetchCard();
                }else{
                    createCard();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        //Getting values from Intent
//        adultCount = getIntent().getIntExtra("adultCount",1);
//        childCount = getIntent().getIntExtra("childCount",0);
//        totalPassenger = getIntent().getIntExtra("totalPassenger",1);
//        ticketPrice = getIntent().getIntExtra("ticketPrice",0);
//        fromLoc = getIntent().getStringExtra("fromLoc");
//        toLoc = getIntent().getStringExtra("toLoc");


        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String userCardPin = Decode.decode(encodedUserCardPin);

                if (isLocked){
                    if (!enteredPaymentPin.equals(userCardPin)){
                        Toast.makeText(PaymentBusPass.this, "Pin is not valid", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //pin valid
                        if (currentBalance > purchasedBusPassPrice){


                            int newBalance = currentBalance - purchasedBusPassPrice;

                            final Map<String, Object> updateBalance = new HashMap<>();
                            updateBalance.put("CardBalance", newBalance);

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(UserID);
                            reference.child("Wallet").updateChildren(updateBalance);

                            dialogPlus.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(getApplicationContext(), UserBusPass.class));
                                    finish();
                                }
                            },1000);

                        }
                        else{

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(UserID);
                            reference.child("BusPass").removeValue();

                            Toast.makeText(PaymentBusPass.this, "Insufficient Balance", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(PaymentBusPass.this, "Lock the pin", Toast.LENGTH_SHORT).show();
                }



            }
        });

    }



    private void createCard() {
        notHavingCard.setVisibility(View.VISIBLE);
        btn_notHavingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateWallet.class));
                finish();
            }
        });
    }



    private void fetchCard() {

        reference.child("Wallet").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cardModelList.clear();
                CardModel cardModel = snapshot.getValue(CardModel.class);
                cardModelList.add(cardModel);
                paymentAdapter.notifyDataSetChanged();

                encodedUserCardPin = snapshot.child("CardPin").getValue().toString();
                currentBalance = Integer.parseInt(snapshot.child("CardBalance").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            enteredPaymentPin = intent.getStringExtra("paymentPin");

            btn_pay.setEnabled(true);
        }
    };

    public BroadcastReceiver isLockedReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isLocked = intent.getBooleanExtra("isLocked", false);
        }
    };

    public static long generateRandom(int length) {
        Random random = new Random();
        char[] digits = new char[length];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < length; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        return Long.parseLong(new String(digits));
    }
}