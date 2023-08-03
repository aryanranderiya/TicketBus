package com.example.ticketbus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.example.ticketbus.Interface.IFirebaseLoadDone;
import com.example.ticketbus.Model.IDs;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements IFirebaseLoadDone{

    ChipNavigationBar chipNavigationBar;

    private List<IDs> iDs;

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
        setContentView(R.layout.activity_home);

        Initialize();
        BottomMenu();
    }

    private void Initialize() {

        chipNavigationBar = findViewById(R.id.bottom_nav_menu);
        chipNavigationBar.setItemSelected(R.id.bottom_nav_dashboard, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, new DashboardFragment()).commit();
    }

    private void BottomMenu() {

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {

                switch (i){

                    case R.id.bottom_nav_dashboard:
                        loadfrag(new DashboardFragment(), true);
                        break;


                    case R.id.bottom_nav_ticket:
                        loadfrag(new TicketFragment(), false);
                        break;

                    case R.id.bottom_nav_profile:
                        loadfrag(new ProfileFragment(), false);
                        break;
                }
            }
        });
    }

    private void loadfrag(Fragment fragment, boolean flag) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (flag==true){
            ft.add(R.id.main_fragment_container,fragment);
        }
        else{
            ft.replace(R.id.main_fragment_container,fragment);
        }
        ft.commit();

    }


    @Override
    public void onFirebaseLoadSuccess(List<IDs> LocationList) {

    }

    @Override
    public void onFirebaseLoadFailed(String Message) {

    }
}