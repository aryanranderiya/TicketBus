package com.example.ticketbus;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class SwipeDetector implements View.OnTouchListener {

    private GestureDetector gestureDetector;
    private OnSwipeListener onSwipeListener;
    private static final float MIN_DISTANCE_DP = 60;

    public SwipeDetector(Context context, OnSwipeListener onSwipeListener) {
        gestureDetector = new GestureDetector(context, new GestureListener());
        this.onSwipeListener = onSwipeListener;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();

            if (Math.abs(distanceX) > MIN_DISTANCE_DP) {
                if (distanceX > 0) {
                    onSwipeListener.onSwipeRightToLeft();
                } else {
                    onSwipeListener.onSwipeLeftToRight();
                }
                return true;
            }

            return false;
        }
    }

    public interface OnSwipeListener {
        void onSwipeLeftToRight();

        void onSwipeRightToLeft();
    }
}