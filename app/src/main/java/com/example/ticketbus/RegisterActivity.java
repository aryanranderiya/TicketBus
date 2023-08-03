package com.example.ticketbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements TextWatcher {

    private TextInputLayout edt_name, edt_email, edt_password, edt_confirmPass, edt_phone;

    private Button btn_register, btn_toLogin;

    private String Name, Email, Password, confirmPassword, Phone;

    private FirebaseAuth auth;

    private FirebaseUser currentUser;

    private ProgressDialog progressDialog;

    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Initialize();
        Buttons();
    }
    private void Initialize() {

        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();

        edt_name = findViewById(R.id.edt_rName);
        edt_phone = findViewById(R.id.edt_rPhone);
        edt_email = findViewById(R.id.edt_rEmail);
        edt_password = findViewById(R.id.edt_rPassword);
        edt_confirmPass = findViewById(R.id.edt_rConfPassword);

        btn_register = findViewById(R.id.btn_register);
        btn_toLogin = findViewById(R.id.btn_toLogIn);

        edt_name.getEditText().addTextChangedListener(this);
        edt_email.getEditText().addTextChangedListener(this);
        edt_phone.getEditText().addTextChangedListener(this);
        edt_password.getEditText().addTextChangedListener(this);
        edt_confirmPass.getEditText().addTextChangedListener(this);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void Buttons() {

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                setError();

                if (flag){
                        progressDialog.setTitle("Registering");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    try {
                        SignUp(Email, Password);
                    } catch (FirebaseAuthException e) {
                        Toast.makeText(RegisterActivity.this, "Oops! Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btn_toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private void SignUp(String email, String password) throws FirebaseAuthException {

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    Objects.requireNonNull(auth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Please verify your email address", Toast.LENGTH_SHORT).show();
                                RegisterUser(Phone, Name, Email);
                            }else{
                                Toast.makeText(RegisterActivity.this, "Wrong email address", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                if (!task.isSuccessful()) {

                    Toast.makeText(RegisterActivity.this, "Email already in use!!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void RegisterUser(String Phone, String Name, String Email) {

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        String Current_Uid = currentUser.getUid();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child(Current_Uid);

        HashMap<String, String> user = new HashMap<>();
        user.put("Name", Name);
        user.put("Phone", Phone);
        user.put("Email", Email);

        database.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Failed to Register", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        setError();
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        setError();
        edt_password.setErrorIconDrawable(null);
        edt_confirmPass.setErrorIconDrawable(null);
    }

    @Override
    public void afterTextChanged(Editable editable) {
        setError();

    }

    public void setError() {

        Name = edt_name.getEditText().getText().toString();
        Email = edt_email.getEditText().getText().toString();
        Phone = edt_phone.getEditText().getText().toString();
        Password = edt_password.getEditText().getText().toString();
        confirmPassword = edt_confirmPass.getEditText().getText().toString();

         if (edt_name.hasFocus()) {
                if (Name.isEmpty()) {
                    edt_name.setError("Please enter name!");
                    flag = false;
                } else {
                    edt_name.setError(null);
                    flag=true;
                }
            }else {
             flag = false;
            }
            if (edt_phone.hasFocus()) {
                if (Phone.length() != 10 || Phone.isEmpty()) {
                    edt_phone.setError("Please Enter valid Phone Number!");
                    flag = false;
                } else {
                    edt_phone.setError(null);
                    flag=true;
                }
            }
            else {
                flag = false;
            }
            if (edt_email.hasFocus()) {
                if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches() || Email.isEmpty()) {
                    edt_email.setError("Please Enter Valid E-Mail Address!");
                    flag = false;
                }else {
                    edt_email.setError(null);
                    flag=true;
                }
            }
            else {
                flag = false;
            }
            if (edt_password.hasFocus()) {
                if (Password.length() < 8 || Password.isEmpty()) {
                    edt_password.setError("Password must be at least 8 characters!");
                    flag = false;
                } else {
                    edt_password.setError(null);
                    flag=true;
                }
            }else {
                flag = false;
            }

            if (edt_confirmPass.hasFocus()) {
                if (confirmPassword.isEmpty() || (!(confirmPassword.equals(Password)))) {
                    edt_confirmPass.setError("Password does not match!");
                    flag = false;
                } else {
                    edt_confirmPass.setError(null);
                    flag=true;
                }
            }else {
                flag = false;
            }

        }

    }







