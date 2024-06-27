package com.example.adskipper;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
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
    private HeadphoneButtonHelper headphoneButtonHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activateButton = findViewById(R.id.activate_button);
        headphoneButtonHelper = new HeadphoneButtonHelper(this);

        activateButton.setOnClickListener(v -> {
            if (isUsageStatsPermissionGranted()) {
                if (isYouTubeRunning()) {
                    Toast.makeText(MainActivity.this, "Feature Activated", Toast.LENGTH_SHORT).show();
                    // Simulate click on Skip Ad button (replace with actual coordinates)
                    headphoneButtonHelper.simulateClick(500, 1000); // Example coordinates
                } else {
                    Toast.makeText(MainActivity.this, "YouTube is not running, open YouTube first", Toast.LENGTH_SHORT).show();
                }
            } else {
                showPermissionDialog();
            }
        });

        if (!isUsageStatsPermissionGranted()) {
            showPermissionDialog();
        }
    }

    private boolean isYouTubeRunning() {
        long endTime = System.currentTimeMillis();
        long beginTime = endTime - 60000; // Check the last 60 seconds

        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginTime, endTime);

        if (stats != null) {
            for (UsageStats usageStats : stats) {
                if (usageStats.getPackageName().equals("com.google.android.youtube")) {
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
            Context context = getApplicationContext();
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), context.getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isUsageStatsPermissionGranted()) {
            Toast.makeText(this, "Please grant Usage Access permission", Toast.LENGTH_LONG).show();
        }
    }
}
