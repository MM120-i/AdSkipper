package com.example.adskipper;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class HeadphoneButtonHelper {

    private Context context;
    private static final String TAG = "HeadphoneButtonHelper";

    public HeadphoneButtonHelper(Context context) {
        this.context = context;
    }

    public void simulateClick(int x, int y) {

        Log.d(TAG, "Simulating click at coordinates : (" + x + ", " + y + ")");

        View view = ((MainActivity) context).getWindow().getDecorView();
        long downTime = System.currentTimeMillis();
        long eventTime = System.currentTimeMillis() + 100;
        int metaState = 0;

        MotionEvent motionEventDown = MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEvent.ACTION_DOWN,
                x,
                y,
                metaState
        );

        MotionEvent motionEventUp = MotionEvent.obtain(
                downTime + 100,
                eventTime + 100,
                MotionEvent.ACTION_UP,
                x,
                y,
                metaState
        );

        view.dispatchTouchEvent(motionEventDown);
        view.dispatchTouchEvent(motionEventUp);

        motionEventDown.recycle();
        motionEventUp.recycle();
    }
}
