package com.example.adskipper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class HeadphoneReceiver extends BroadcastReceiver {

    private HeadphoneButtonHelper headphoneButtonHelper;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {

            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {

                int keyCode = event.getKeyCode();

                if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {
                    headphoneButtonHelper.simulateClick(500, 1000); // Adjust coordinates as necessary
                }
            }
        }
    }

    public void setHeadphoneButtonHelper(HeadphoneButtonHelper helper) {
        this.headphoneButtonHelper = helper;
    }
}


