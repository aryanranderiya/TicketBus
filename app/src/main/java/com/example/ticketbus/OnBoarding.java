package com.example.ticketbus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.ticketbus.Adapters.SliderAdapter;

public class OnBoarding extends AppCompatActivity implements View.OnClickListener {

    // Variables
    ViewPager viewPager;
    LinearLayout dotsLayout;
    SliderAdapter sliderAdapter;
    TextView[] dots;
    Button letsGetStarted, next, back, skip;

    int currentPos;
    
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Hooks
        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        letsGetStarted = findViewById(R.id.btn_letsGetStarted);
        back = findViewById(R.id.btn_back);
        next = findViewById(R.id.btn_next);
        skip = findViewById(R.id.btn_skip);

        // Call adapter
        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        // Dots
        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);

        letsGetStarted.setOnClickListener(this);
        next.setOnClickListener(this);
        back.setOnClickListener(this);
        skip.setOnClickListener(this);

    }

    private void addDots(int position){

        currentPos = position;

        dots = new TextView[4];
        dotsLayout.removeAllViews();

        for(int i = 0; i < dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.grey));
            dotsLayout.addView(dots[i]);
        }

        if(dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.main_500));
        }
    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);

            if(position == 0) {
                letsGetStarted.setVisibility(View.INVISIBLE);
                back.setVisibility(View.INVISIBLE);
                next.setVisibility(View.VISIBLE);
            }
            else if(position == 1) {
                letsGetStarted.setVisibility(View.INVISIBLE);
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
            }
            else if(position == 2) {
                letsGetStarted.setVisibility(View.INVISIBLE);
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
            }
            else {
                letsGetStarted.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btn_letsGetStarted:
            case R.id.btn_skip:
                Intent iToLogin = new Intent(OnBoarding.this, LoginActivity.class);
                startActivity(iToLogin);
                finish();

                break;

            case R.id.btn_back:

                viewPager.setCurrentItem(currentPos - 1);
                break;

            case R.id.btn_next:

                viewPager.setCurrentItem(currentPos + 1);
        }
    }
}