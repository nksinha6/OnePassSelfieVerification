package com.onepass.reception.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    private final BroadcastReceiver sessionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AppUtils.showLog("Received session expired");
            showSessionExpiredDialog();
        }
    };

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onStart() {
        super.onStart();
        AppUtils.showLog("Registering session expired receiver");
        IntentFilter filter =
                new IntentFilter("SESSION_EXPIRED");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                    sessionReceiver,
                    filter,
                    Context.RECEIVER_NOT_EXPORTED
            );
        } else {
            registerReceiver(sessionReceiver, filter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(sessionReceiver);
    }

    private void showSessionExpiredDialog() {
        AppUtils.showLog("showSessionExpiredDialog");
       runOnUiThread(()->AppUtils.sessionExpired(this));
    }
}

