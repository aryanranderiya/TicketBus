package com.example.ticketbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.ticketbus.Databases.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements TextWatcher {

    private TextInputLayout edt_loginEmail, edt_loginPassword;
    private Button btn_login, btn_toRegister, btn_forgotPassword;

    private MaterialCheckBox cb_rememberMe;

    public static final String SHARED_PREFS = "sharedPrefs";

    private FirebaseAuth mAuth;

    private String Name, LoginEmail, LoginPassword;

    private ProgressDialog progressDialog, loadingBar;

    Dialog dialog;
    boolean flag=false;

    Drawable emailStartIcon;

    Drawable wrappedEmailStartIcon;

    DialogPlus dialogPlus;

    boolean rememberMeState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Initialize();
        Buttons();

    }
    private void Initialize() {

        mAuth = FirebaseAuth.getInstance();

        edt_loginEmail = findViewById(R.id.edt_loginEmail);
        edt_loginPassword = findViewById(R.id.edt_loginPassword);

        edt_loginPassword.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());

        btn_login = findViewById(R.id.btn_login);
        btn_toRegister = findViewById(R.id.btn_toRegister);
        btn_forgotPassword = findViewById(R.id.btn_forgotPassword);
        cb_rememberMe = findViewById(R.id.cbRememberMe);

        progressDialog = new ProgressDialog(this);
        dialogPlus = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.custom_loading_dialog))
                .setContentBackgroundResource(Color.TRANSPARENT)
                .setGravity(Gravity.CENTER)
                .create();

        edt_loginEmail.getEditText().addTextChangedListener(this);
        edt_loginPassword.getEditText().addTextChangedListener(this);

        emailStartIcon = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.icon_email);
        wrappedEmailStartIcon = DrawableCompat.wrap(emailStartIcon);


    }

    private void Buttons() {

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Email = edt_loginEmail.getEditText().getText().toString();
                String Password = edt_loginPassword.getEditText().getText().toString();

                if (flag) {
                    progressDialog.setContentView(R.layout.custom_loading_dialog);
                    progressDialog.setTitle("Logging In");
                    progressDialog.setCancelable(false);
                    dialogPlus.show();
                    Login(Email, Password);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please enter valid details!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btn_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
            }
        });

        cb_rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()){
                    rememberMeState = true;
                } else if (!compoundButton.isChecked()) {
                    rememberMeState = false;
                }
            }
        });
    }

    private void Login(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    if (Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified()){
//                        progressDialog.dismiss();
                        dialogPlus.dismiss();

                        if (rememberMeState){
                            SessionManager sessionManager = new SessionManager(LoginActivity.this,SessionManager.SESSION_REMEMBERME);
                            sessionManager.createRememberMeSessionWithEmailPassword(email, password);
                        }

                        SessionManager sessionManager = new SessionManager(LoginActivity.this,SessionManager.SESSION_USERSESSION);
                        sessionManager.createLoginSessionWithEmailPassword(email, password);

                        Intent in = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(in);
                        finish();
                    }else{
//                        progressDialog.dismiss();
                        dialogPlus.dismiss();
                        Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                    }
                }else{
//                    progressDialog.dismiss();
                    dialogPlus.dismiss();
                    Toast.makeText(LoginActivity.this, "Wrong login credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        seterror();
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        seterror();
    }

    @Override
    public void afterTextChanged(Editable editable) {
        seterror();
    }

    public void seterror() {

        LoginEmail = edt_loginEmail.getEditText().getText().toString();
        LoginPassword = edt_loginPassword.getEditText().getText().toString();



        if (edt_loginEmail.hasFocus()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(LoginEmail).matches() || LoginEmail.isEmpty()) {
                edt_loginEmail.setError("Please Enter Valid E-Mail Address!");


            }else {
                edt_loginEmail.setError(null);

                flag=true;
            }
        }
        else {
            flag = false;
        }
        if (edt_loginPassword.hasFocus()) {
            if (LoginPassword.isEmpty()) {
                edt_loginPassword.setError("Password cannot be empty!");
                flag = false;
            }else {
                edt_loginPassword.setError(null);
                flag=true;
            }
        }else {
            flag = false;
        }


    }

}