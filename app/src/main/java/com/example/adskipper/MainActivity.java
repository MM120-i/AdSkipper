package com.example.adskipper;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button activateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activateButton = findViewById(R.id.activate_button);

        activateButton.setOnClickListener(v -> {

            if (isUsageStatsPermissionGranted()) {

                if (isYouTubeRunning()) {
                    startHeadphoneService(); // Start the HeadphoneService
                    Toast.makeText(MainActivity.this, "Feature Activated", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "YouTube is not running, open YouTube first", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                showPermissionDialog();
            }
        });

        if (!isUsageStatsPermissionGranted()) {
            showPermissionDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopHeadphoneService(); // Stop the HeadphoneService when MainActivity is destroyed
    }

    private void startHeadphoneService() {

        Intent serviceIntent = new Intent(this, HeadphoneService.class);
        serviceIntent.setAction("START");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        }
        else {
            startService(serviceIntent);
        }
    }

    private void stopHeadphoneService() {
        Intent serviceIntent = new Intent(this, HeadphoneService.class);
        serviceIntent.setAction("STOP");
        stopService(serviceIntent);
    }

    private boolean isYouTubeRunning() {

        long endTime = System.currentTimeMillis();
        long beginTime = endTime - 60000; // Check the last 60 seconds

        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginTime, endTime);

        if (stats != null) {

            for (UsageStats usageStats : stats) {

                if ("com.google.android.youtube".equals(usageStats.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void showPermissionDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Usage Access Permission Needed")
                .setMessage("To detect if YouTube is running, this app needs usage access permission. Please grant the permission in the next screen.")
                .setPositiveButton("Grant Permission", (dialog, which) -> requestUsageStatsPermission())
                .setNegativeButton(android.R.string.cancel, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void requestUsageStatsPermission() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }

    private boolean isUsageStatsPermissionGranted() {

        try {

            AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED;
        }
        catch (Exception e) {
            return false;
        }
    }
}