package com.example.ticketbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.ticketbus.Encryption.Decode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Wallet extends AppCompatActivity {

    LinearLayout cardHolderLayout;
    DatabaseReference reference;
    private FirebaseUser currentUser;

    BottomSheetDialog bottomSheetDialogWallet;
    String UserID="";
    private String userName;

    Button btn_addBalance, btn_enableCard;

    String cardHolder, cardNumber, cardBalance;
    TextView txt_cardHolder, txt_cardNumber, txt_cardBalance, txt_trialsLeft;

    String encodedeUserCardPin;

    DialogPlus addBalanceDialog;

    LinearLayout trails;
    int trialsLeft = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        initialize();
        buttons();
        loadDatabase();

         addBalanceDialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.add_balance_dialog))
                .setContentBackgroundResource(Color.TRANSPARENT)
                .setGravity(Gravity.CENTER)
                .create();
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

        cardHolderLayout = findViewById(R.id.cardHolderLayout);

        btn_enableCard = findViewById(R.id.btn_enableCard);

        txt_cardNumber = findViewById(R.id.txt_cardnumber);
        txt_cardHolder = findViewById(R.id.txt_cardHolder);
        txt_cardBalance = findViewById(R.id.txt_cardBalance);

        btn_addBalance = findViewById(R.id.btn_addBalance);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        UserID = currentUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(UserID);
    }

    private void buttons() {

        btn_enableCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btn_enableCard.getText().toString().equals("Enable")){
                    Toast.makeText(Wallet.this, "Enter Card Pin to Enable card", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(Wallet.this, "Enter Card Pin to Disable card", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_addBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialogWallet = new BottomSheetDialog(Wallet.this, R.style.BottomSheetDialogTheme);
                View v = getLayoutInflater().inflate(R.layout.bottom_sheet_wallet_layout, null);
                bottomSheetDialogWallet.setContentView(v);

                bottomSheetDialogWallet.show();

                reference.child("Wallet").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        encodedeUserCardPin = snapshot.child("CardPin").getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                trails = bottomSheetDialogWallet.findViewById(R.id.trials);
                txt_trialsLeft = bottomSheetDialogWallet.findViewById(R.id.txt_trialsLeft);

                Button btn_continue;
                btn_continue = bottomSheetDialogWallet.findViewById(R.id.btn_continueVerify);
                btn_continue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PinView verify_card_pin;

                        String userCardPin = Decode.decode(encodedeUserCardPin);

                        verify_card_pin = bottomSheetDialogWallet.findViewById(R.id.verify_card_pin);
                        String enteredCardPin = verify_card_pin.getText().toString();

                        if (enteredCardPin.equals(userCardPin)){

                            bottomSheetDialogWallet.dismiss();

                            View addBalanceDialogView = addBalanceDialog.getHolderView();
                            addBalanceDialog.show();

                            EditText edt_balance = addBalanceDialogView.findViewById(R.id.edt_balance);
                            Button btn_confirm = addBalanceDialogView.findViewById(R.id.btn_balanceConfirm);



                            btn_confirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if (trialsLeft!=0){
                                        if (!edt_balance.getText().toString().isEmpty()){

                                            int addBalance = Integer.parseInt(edt_balance.getText().toString());
                                            int currentBalance = Integer.parseInt(cardBalance);
                                            int newBalance = currentBalance + addBalance;

                                            final Map<String, Object> newBalanceMap = new HashMap<>();
                                            newBalanceMap.put("CardBalance", newBalance);
                                            reference.child("Wallet").updateChildren(newBalanceMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()){
                                                        Toast.makeText(getApplicationContext(), "Balance Added Successfully", Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        Toast.makeText(Wallet.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                                    }
                                                    addBalanceDialog.dismiss();
                                                    bottomSheetDialogWallet.dismiss();
                                                }
                                            });

                                        }else{
                                            Toast.makeText(Wallet.this, "Enter valid balance to add", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                        }
                        else {
                            trails.setVisibility(View.VISIBLE);
                            if (trialsLeft > 0){
                                txt_trialsLeft.setText(""+trialsLeft);
                            }
                            if (trialsLeft <= 0){
                                trialsLeft = 0;
                                txt_trialsLeft.setText(""+trialsLeft);
                                btn_continue.setEnabled(false);
                                bottomSheetDialogWallet.dismiss();
                                Toast.makeText(Wallet.this, "Card Disabled", Toast.LENGTH_SHORT).show();
                                cardHolderLayout.setAlpha(0.5f);
                                btn_addBalance.setEnabled(false);

                                btn_enableCard.setText("Enable");
                            }

                            Toast.makeText(Wallet.this, "Pin Invalid", Toast.LENGTH_SHORT).show();
                            trialsLeft--;
                        }
                    }
                });

                bottomSheetDialogWallet.setCanceledOnTouchOutside(true);
            }
        });
    }

    private void loadDatabase() {
        reference.child("Wallet").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cardHolder = snapshot.child("CardHolder").getValue().toString();
                cardBalance = snapshot.child("CardBalance").getValue().toString();
                cardNumber = snapshot.child("CardNumber").getValue().toString();

                setText();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setText() {

        txt_cardHolder.setText(cardHolder);
        txt_cardBalance.setText("₹ "+cardBalance);
        txt_cardNumber.setText(cardNumber);
    }
}