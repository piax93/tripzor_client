package com.ifalot.tripzor.utils;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class SwipeBack implements GestureDetector.OnGestureListener {

    protected static final int SWIPE_THRESHOLD = 100;
    protected static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private Activity context;

    public SwipeBack(Activity context){
        this.context = context;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;

        float diffY = e2.getY() - e1.getY();
        float diffX = e2.getX() - e1.getX();
        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    // onSwipeRight
                    context.onBackPressed();
                }
            }
            result = true;
        }

        return result;
    }

    @Override public boolean onDown(MotionEvent e) { return false; }
    @Override public void onShowPress(MotionEvent e) {}
    @Override public boolean onSingleTapUp(MotionEvent e) { return false; }
    @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }
    @Override public void onLongPress(MotionEvent e) {}

}
