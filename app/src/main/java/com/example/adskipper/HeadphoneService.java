package com.example.adskipper;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class HeadphoneService extends Service {

    private HeadphoneButtonHelper headphoneButtonHelper;
    private HeadphoneReceiver headphoneReceiver;

    @Override
    public void onCreate(){

        super.onCreate();
        headphoneButtonHelper = new HeadphoneButtonHelper(this);
        headphoneReceiver = new HeadphoneReceiver();
        headphoneReceiver.setHeadphoneButtonHelper(headphoneButtonHelper);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(headphoneReceiver, new IntentFilter(Intent.ACTION_MEDIA_BUTTON), Context.RECEIVER_NOT_EXPORTED);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getAction() != null) {

            String action = intent.getAction();

            if (action.equals("START")) {
                startForegroundService();
            }
            else if (action.equals("STOP")) {
                stopForegroundService();
            }
        }
        return START_NOT_STICKY;
    }

    @SuppressLint("ForegroundServiceType")
    private void startForegroundService() {

        Notification notification = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = buildNotification();
        }

        startForeground(1, notification);
    }

    private void stopForegroundService() {
        stopForeground(true);
        stopSelf();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification buildNotification() {

        String channelId = "channel_id";
        String channelName = "Channel Name";

        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new Notification.Builder(this, channelId)
                .setContentTitle("Headphone Service")
                .setContentText("Listening for headphone events")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentIntent(pendingIntent)
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(headphoneReceiver);
    }
}

