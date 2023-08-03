package com.example.ticketbus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.ticketbus.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class BusPassInformation extends AppCompatActivity {

    // Declare UI components
    Button proceed,btnbuy;
    TextView title,validperiod,price,discountedprice, passprice, passvalidity, txtoriginalprice, benefit1,benefit2,benefit3,benefit4,benefit5,benefit6,benefit7,benefit8,benefit9;
    ConstraintLayout bpcolourbackground;
    BottomSheetDialog bottomSheetDialog;
    View bottomSheetView,view;
    ImageView bottomsheetscrolldown;
    ImageButton btnBusPassBack;

    // Declare variables
    public long daysLeft;
    public int timeint;
    public String formattedLocalDate, str_validityDateTime, userId="",dbvalidity,dbprice,dbdiscountedprice,sbenefit1,sbenefit2,sbenefit3,sbenefit4,sbenefit5,sbenefit6,sbenefit7,sbenefit8,sbenefit9,buspasstype;
    public DateTimeFormatter formatter = null;
    private String busPassID, str_expiryDateTime, QRCode;

    // Declare Firebase components
    FirebaseUser currentUser;
    DatabaseReference dbreference, dbreference2;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    // Declare handlers and runnable
    public Handler mHandler;
    public Runnable mRunnable;

    // Declare date and time variables
    public LocalDateTime expiryLocalDate;
    public LocalDateTime localDateTime;

    // Declare progress dialog for loading
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_pass_information);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        Initialise();

        Intent intent = getIntent();
        String passType = intent.getStringExtra("pass_type");

        //regenerate();

        switch (passType) {
            case "1 Month":
                month1();
                break;
            case "3 Month":
                month3();
                break;
            case "6 Month":
                month6();
                break;
            case "12 Month":
                month12();
                break;
        }
    }

    public void regenerate() {
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                time();
                updateBusPassData();
                mHandler.postDelayed(this, 4000);
            }
        };
        mHandler.postDelayed(mRunnable, 0);
    }

    public void bind() {
        bottomSheetDialog = new BottomSheetDialog(view.getContext());
        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog_proceed_layout, findViewById(R.id.parentcardviewbottomsheet));
        bottomSheetDialog.setContentView(bottomSheetView);
        passprice=bottomSheetView.findViewById(R.id.passprice);
        passvalidity=bottomSheetView.findViewById(R.id.passvalidity);
        benefit1 = bottomSheetView.findViewById(R.id.benefit1);
        benefit2 = bottomSheetView.findViewById(R.id.benefit2);
        benefit3 = bottomSheetView.findViewById(R.id.benefit3);
        benefit4 = bottomSheetView.findViewById(R.id.benefit4);
        benefit5 = bottomSheetView.findViewById(R.id.benefit5);
        benefit6 = bottomSheetView.findViewById(R.id.benefit6);
        benefit7 = bottomSheetView.findViewById(R.id.benefit7);
        benefit8 = bottomSheetView.findViewById(R.id.benefit8);
        benefit9 = bottomSheetView.findViewById(R.id.benefit9);
        bottomsheetscrolldown = bottomSheetView.findViewById(R.id.bottomsheetscrolldown);
        btnbuy = bottomSheetView.findViewById(R.id.btnbuy);

        bottomsheetscrolldown.setOnClickListener(view -> bottomSheetDialog.dismiss());

        buy();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void Initialise() {

        dbreference = FirebaseDatabase.getInstance().getReference().child("BusPass");
        firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        user= com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog = new ProgressDialog(BusPassInformation.this);
        userId = currentUser.getUid();;
        title=findViewById(R.id.passtitle);
        proceed=findViewById(R.id.btnproceed);
        validperiod=findViewById(R.id.validperiod);
        price=findViewById(R.id.price);
        discountedprice=findViewById(R.id.discountedprice);
        bpcolourbackground=findViewById(R.id.bpcolourbackground);
        btnBusPassBack=findViewById(R.id.btnBusPassBack);
        txtoriginalprice = findViewById(R.id.txtoriginalprice);

        btnBusPassBack.setOnClickListener(view -> finish());

        dbreference2 = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(userId)
                .child("BusPass");

        long randomid = generateRandom(7);
        busPassID = "TBP-" + randomid;
    }

    public void time() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            localDateTime =LocalDateTime.now();

            formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss", Locale.ENGLISH);

            formattedLocalDate = localDateTime.format(formatter);
            expiryLocalDate = localDateTime.plusDays(timeint);

            daysLeft = getDaysLeft();
        }
    }

    private void month1() {
        buspasstype="1Month";
        timeint=31;
        time();
        progressDialog.setTitle("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        dbreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                dbvalidity = Objects.requireNonNull(snapshot.child("1Month").child("Validity").getValue()).toString();
                dbprice = Objects.requireNonNull(snapshot.child("1Month").child("Price").getValue()).toString();
                sbenefit1= Objects.requireNonNull(snapshot.child("1Month").child("Benefits").child("Benefit1").getValue()).toString();
                sbenefit2= Objects.requireNonNull(snapshot.child("1Month").child("Benefits").child("Benefit2").getValue()).toString();
                sbenefit3= Objects.requireNonNull(snapshot.child("1Month").child("Benefits").child("Benefit3").getValue()).toString();
                sbenefit4= Objects.requireNonNull(snapshot.child("1Month").child("Benefits").child("Benefit4").getValue()).toString();
                sbenefit5= Objects.requireNonNull(snapshot.child("1Month").child("Benefits").child("Benefit5").getValue()).toString();
                title.setText(R.string.onemonthpass);
                discountedprice.setText("");
                validperiod.setText(dbvalidity);
                txtoriginalprice.setText("");
                price.setText(String.format("₹ %s", dbprice));
                progressDialog.dismiss();
                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_buspass_gradient_1month);
                bpcolourbackground.setBackground(drawable);
                proceed.setBackground(drawable);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
        proceed.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view1) {
                view=view1;
                bind();
                benefit1.setText(sbenefit1);
                benefit2.setText(sbenefit2);
                benefit3.setText(sbenefit3);
                benefit4.setText(sbenefit4);
                benefit5.setText(sbenefit5);
                benefit6.setVisibility(View.GONE);
                benefit7.setVisibility(View.GONE);
                benefit8.setVisibility(View.GONE);
                benefit9.setVisibility(View.GONE);
                benefit6.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                benefit7.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                benefit8.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                benefit9.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                passprice.setText(String.format("₹ %s", dbprice));
                passvalidity.setText(dbvalidity);

                Drawable drawable1 = ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_buspass_gradient_1month);
                btnbuy.setBackground(drawable1);
                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_buspass_gradient_1month);
                bpcolourbackground.setBackground(drawable);
                proceed.setBackground(drawable);
                bottomSheetDialog.show();
            }
        });
    }

    private void month3() {
        buspasstype="3Month";
        timeint=93;
        time();
        progressDialog.setTitle("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        dbreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                dbvalidity = Objects.requireNonNull(snapshot.child("3Month").child("Validity").getValue()).toString();
                dbprice = Objects.requireNonNull(snapshot.child("3Month").child("Price").getValue()).toString();
                sbenefit1= Objects.requireNonNull(snapshot.child("3Month").child("Benefits").child("Benefit1").getValue()).toString();
                sbenefit2= Objects.requireNonNull(snapshot.child("3Month").child("Benefits").child("Benefit2").getValue()).toString();
                sbenefit3= Objects.requireNonNull(snapshot.child("3Month").child("Benefits").child("Benefit3").getValue()).toString();
                sbenefit4= Objects.requireNonNull(snapshot.child("3Month").child("Benefits").child("Benefit4").getValue()).toString();
                sbenefit5= Objects.requireNonNull(snapshot.child("3Month").child("Benefits").child("Benefit5").getValue()).toString();
                sbenefit6= Objects.requireNonNull(snapshot.child("3Month").child("Benefits").child("Benefit6").getValue()).toString();
                sbenefit7= Objects.requireNonNull(snapshot.child("3Month").child("Benefits").child("Benefit7").getValue()).toString();
                dbdiscountedprice = Objects.requireNonNull(snapshot.child("3Month").child("DiscountedFromPrice").getValue()).toString();
                discountedprice.setText(String.format("₹ %s", dbdiscountedprice));
                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_buspass_gradient_3month);
                bpcolourbackground.setBackground(drawable);
                proceed.setBackground(drawable);
                title.setText(R.string.threemonthpass);
                validperiod.setText(dbvalidity);
                price.setText(String.format("₹ %s", dbprice));
                progressDialog.dismiss();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                view=view1;
                bind();
                benefit1.setText(sbenefit1);
                benefit2.setText(sbenefit2);
                benefit3.setText(sbenefit3);
                benefit4.setText(sbenefit4);
                benefit5.setText(sbenefit5);
                benefit6.setText(sbenefit6);
                benefit7.setText(sbenefit7);
                benefit8.setVisibility(View.GONE);
                benefit9.setVisibility(View.GONE);
                benefit8.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                benefit9.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                passprice.setText(String.format("₹ %s", dbprice));
                passvalidity.setText(dbvalidity);
                Drawable drawable1 = ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_buspass_gradient_3month);
                btnbuy.setBackground(drawable1);
                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_buspass_gradient_3month);
                bpcolourbackground.setBackground(drawable);
                proceed.setBackground(drawable);
                bottomSheetDialog.show();
            }
        });
    }

    private void month6() {
        buspasstype="6Month";
        timeint=186;
        time();
        progressDialog.setTitle("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        dbreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                dbvalidity = Objects.requireNonNull(snapshot.child("6Month").child("Validity").getValue()).toString();
                dbprice = Objects.requireNonNull(snapshot.child("6Month").child("Price").getValue()).toString();
                sbenefit1= Objects.requireNonNull(snapshot.child("6Month").child("Benefits").child("Benefit1").getValue()).toString();
                sbenefit2= Objects.requireNonNull(snapshot.child("6Month").child("Benefits").child("Benefit2").getValue()).toString();
                sbenefit3= Objects.requireNonNull(snapshot.child("6Month").child("Benefits").child("Benefit3").getValue()).toString();
                sbenefit4= Objects.requireNonNull(snapshot.child("6Month").child("Benefits").child("Benefit4").getValue()).toString();
                sbenefit5= Objects.requireNonNull(snapshot.child("6Month").child("Benefits").child("Benefit5").getValue()).toString();
                sbenefit6= Objects.requireNonNull(snapshot.child("6Month").child("Benefits").child("Benefit6").getValue()).toString();
                sbenefit7= Objects.requireNonNull(snapshot.child("6Month").child("Benefits").child("Benefit7").getValue()).toString();
                sbenefit8= Objects.requireNonNull(snapshot.child("6Month").child("Benefits").child("Benefit8").getValue()).toString();
                dbdiscountedprice = Objects.requireNonNull(snapshot.child("6Month").child("DiscountedFromPrice").getValue()).toString();
                discountedprice.setText(String.format("₹ %s", dbdiscountedprice));
                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_buspass_gradient_6month);
                bpcolourbackground.setBackground(drawable);
                proceed.setBackground(drawable);
                title.setText(R.string.sixmonthpass);
                validperiod.setText(dbvalidity);
                price.setText(String.format("₹ %s", dbprice));
                progressDialog.dismiss();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                view=view1;
                bind();
                benefit1.setText(sbenefit1);
                benefit2.setText(sbenefit2);
                benefit3.setText(sbenefit3);
                benefit4.setText(sbenefit4);
                benefit5.setText(sbenefit5);
                benefit6.setText(sbenefit6);
                benefit7.setText(sbenefit7);
                benefit8.setText(sbenefit8);
                benefit9.setVisibility(View.GONE);
                benefit9.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                passprice.setText(String.format("₹ %s", dbprice));
                passvalidity.setText(dbvalidity);
                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_buspass_gradient_6month);
                bpcolourbackground.setBackground(drawable);
                proceed.setBackground(drawable);
                Drawable drawable1 = ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_buspass_gradient_6month);
                btnbuy.setBackground(drawable1);
                bottomSheetDialog.show();
            }
        });
    }

    private void month12() {
        buspasstype="12Month";
        timeint=365;
        time();
        progressDialog.setTitle("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        dbreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dbvalidity = Objects.requireNonNull(snapshot.child("12Month").child("Validity").getValue()).toString();
                dbprice = Objects.requireNonNull(snapshot.child("12Month").child("Price").getValue()).toString();
                sbenefit1= Objects.requireNonNull(snapshot.child("12Month").child("Benefits").child("Benefit1").getValue()).toString();
                sbenefit2= Objects.requireNonNull(snapshot.child("12Month").child("Benefits").child("Benefit2").getValue()).toString();
                sbenefit3= Objects.requireNonNull(snapshot.child("12Month").child("Benefits").child("Benefit3").getValue()).toString();
                sbenefit4= Objects.requireNonNull(snapshot.child("12Month").child("Benefits").child("Benefit4").getValue()).toString();
                sbenefit5= Objects.requireNonNull(snapshot.child("12Month").child("Benefits").child("Benefit5").getValue()).toString();
                sbenefit6= Objects.requireNonNull(snapshot.child("12Month").child("Benefits").child("Benefit6").getValue()).toString();
                sbenefit7= Objects.requireNonNull(snapshot.child("12Month").child("Benefits").child("Benefit7").getValue()).toString();
                sbenefit8= Objects.requireNonNull(snapshot.child("12Month").child("Benefits").child("Benefit8").getValue()).toString();
                sbenefit9= Objects.requireNonNull(snapshot.child("12Month").child("Benefits").child("Benefit9").getValue()).toString();
                dbdiscountedprice = Objects.requireNonNull(snapshot.child("12Month").child("DiscountedFromPrice").getValue()).toString();
                discountedprice.setText(String.format("₹ %s", dbdiscountedprice));
                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_buspass_gradient_12month);
                bpcolourbackground.setBackground(drawable);
                proceed.setBackground(drawable);
                title.setText(R.string.oneyearpass);
                validperiod.setText(dbvalidity);
                price.setText(String.format("₹ %s", dbprice));
                progressDialog.dismiss();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                view=view1;
                bind();
                benefit1.setText(sbenefit1);
                benefit2.setText(sbenefit2);
                benefit3.setText(sbenefit3);
                benefit4.setText(sbenefit4);
                benefit5.setText(sbenefit5);
                benefit6.setText(sbenefit6);
                benefit7.setText(sbenefit7);
                benefit8.setText(sbenefit8);
                benefit9.setText(sbenefit9);
                passprice.setText(String.format("₹ %s", dbprice));
                passvalidity.setText(dbvalidity);

                Drawable drawable1 = ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_buspass_gradient_12month);
                btnbuy.setBackground(drawable1);
                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_buspass_gradient_12month);
                bpcolourbackground.setBackground(drawable);
                proceed.setBackground(drawable);
                bottomSheetDialog.show();
            }
        });
    }

    public void buy(){
        btnbuy.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), PaymentBusPass.class);
                intent.putExtra("busPassPrice", dbprice);
                startActivity(intent);
                updateBusPassData();
                bottomSheetDialog.dismiss();
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateBusPassData() {

        str_expiryDateTime = expiryLocalDate.format(formatter);
        str_validityDateTime = localDateTime.format(formatter);

        QRCode = String.join(",", "TicketBusPass", busPassID, userId, buspasstype, str_expiryDateTime, str_validityDateTime);

        Map<String, Object> busPassInfo = new HashMap<>();
        busPassInfo.put("BusPassId", busPassID);
        busPassInfo.put("BusPassType", buspasstype);
        busPassInfo.put("ExpiryDateTime", str_expiryDateTime);
        busPassInfo.put("ValidDateTime", str_validityDateTime);
        busPassInfo.put("DaysLeftTillExpiry", daysLeft);
        busPassInfo.put("BusPassQRCode", QRCode);
        busPassInfo.put("isExpired", false);
        dbreference2.setValue(busPassInfo);
    }

    public static long generateRandom(int length) {
        Random random = new Random();
        char[] digits = new char[length];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < length; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        return Long.parseLong(new String(digits));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public long getDaysLeft() {
        Duration duration = Duration.between(localDateTime.toLocalDate().atStartOfDay(), expiryLocalDate.toLocalDate().atStartOfDay());
        long daysLeft=Math.abs(duration.toDays());

        return Math.abs(duration.toDays());
    }

}
