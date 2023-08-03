package com.example.ticketbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.Objects;

public class ForgotPassword extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    TextInputLayout phoneNumber;

    Button btn_forgotPasswordNext;

    String userID;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Initialize();
        Buttons();
    }

    private void Initialize() {

        countryCodePicker = findViewById(R.id.countryCodePicker);

        countryCodePicker.setDefaultCountryUsingNameCode("IN");
        countryCodePicker.resetToDefaultCountry();
        phoneNumber = findViewById(R.id.phoneNumber);

        btn_forgotPasswordNext = findViewById(R.id.btn_forgotPasswordNext);
    }

    private void Buttons() {

        btn_forgotPasswordNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String matchingPhoneNo = phoneNumber.getEditText().getText().toString().trim();

                Query checkUser = FirebaseDatabase.getInstance().getReference("Users");

                checkUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot userSnapshot : snapshot.getChildren()){



                            if (userSnapshot.child("Phone").getValue().equals(matchingPhoneNo)){
                                userID = userSnapshot.getKey();

                                Toast.makeText(ForgotPassword.this, ""+userID, Toast.LENGTH_SHORT).show();
                            }
                            else{

                            }




                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                String _getUserEnteredPhoneNumber = Objects.requireNonNull(phoneNumber.getEditText()).getText().toString().trim();
                String _phoneNo = countryCodePicker.getSelectedCountryCodeWithPlus() + _getUserEnteredPhoneNumber;

//                Intent intentVerifyOTP = new Intent(getApplicationContext(), VerifyOTP.class);
//
//
//                intentVerifyOTP.putExtra("phoneNo", _phoneNo);
//
//                startActivity(intentVerifyOTP);

            }
        });

    }
}