package com.example.ticketbus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketbus.Databases.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;


public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String email, password;

    Boolean loggedIn = false;


    SharedPreferences onBoardingScreen;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        SessionManager sessionManager = new SessionManager(SplashScreen.this, SessionManager.SESSION_REMEMBERME);
        if (sessionManager.checkRememberMe()){


            HashMap<String, String> rememberMeDetails = sessionManager.getRememberMeEmailPasswordFromSession();
            email = rememberMeDetails.get(SessionManager.KEY_SESSIONEMAIL);
            password = rememberMeDetails.get(SessionManager.KEY_SESSIONPASSWORD);
            mAuth = FirebaseAuth.getInstance();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    email = "";
                    password = "";
                    loggedIn = true;
                }
            });
        }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    onBoardingScreen = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
                    boolean isFirstTime = onBoardingScreen.getBoolean("firstTime", true);

                    if(isFirstTime) {

                        Intent iToOnBoarding = new Intent(getApplicationContext(), OnBoarding.class);
                        startActivity(iToOnBoarding);
                        finish();

                        SharedPreferences.Editor editor = onBoardingScreen.edit();
                        editor.putBoolean("firstTime", false);
                        editor.commit();
                    }
                    else {
                        if (loggedIn) {
                            Intent iToHome = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(iToHome);
                            finish();
                        } else {
                            Intent iToSignup = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(iToSignup);
                            finish();
                        }
                    }
                }
            },4928);
    }
}