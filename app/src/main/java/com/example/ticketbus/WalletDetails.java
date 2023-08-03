package com.example.ticketbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chaos.view.PinView;
import com.example.ticketbus.Encryption.Encode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WalletDetails extends AppCompatActivity {

    DatabaseReference reference;
    private FirebaseUser currentUser;
    TextInputLayout edt_CardName;

    PinView cardPin, confirmCardPin;
    Button btn_cardCreate;
    String UserID="";
    private String userName;

    boolean valid = false;
    String v_cardPin, v_confirmCardPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_details);

        Initialize();
        Button();
        LoadDatabase();

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        cardPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 4){
                    v_cardPin = charSequence.toString();
                    valid = true;
                }
                else{
                    valid = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void Initialize() {

        edt_CardName = findViewById(R.id.edt_CardName);
        btn_cardCreate = findViewById(R.id.btn_cardCreate);

        cardPin = findViewById(R.id.card_pin);
        confirmCardPin = findViewById(R.id.confirm_card_pin);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        UserID = currentUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(UserID);
    }

    private void LoadDatabase() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userName = (String) snapshot.child("Name").getValue();

                edt_CardName.getEditText().setText(userName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Button() {

        btn_cardCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edt_CardName.getEditText().getText().toString().isEmpty()){
                    Toast.makeText(WalletDetails.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (valid){
                        if (cardPin.getText().toString().equals(confirmCardPin.getText().toString())){

                            String cardName = edt_CardName.getEditText().getText().toString();
                            v_cardPin = cardPin.getText().toString();
                            String encodedCardPin = Encode.encode(v_cardPin);
                            int balance = 0;

                            Long generateCardNumber = generateRandom(12);
                            String cardNumber = generateCardNumber.toString();

                            final Map<String, Object> wallet = new HashMap<>();
                            wallet.put("CardHolder", cardName);
                            wallet.put("CardNumber", cardNumber);
                            wallet.put("CardPin", encodedCardPin);
                            wallet.put("CardBalance", balance);

                            reference.child("Wallet").setValue(wallet).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(WalletDetails.this, "Card Created Sucessfully.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), Wallet.class));
                                    finish();
                                }
                            });


                        }
                        else{
                            Toast.makeText(WalletDetails.this, "Pin doesn't match", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(WalletDetails.this, "Enter Valid Pin", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

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

}