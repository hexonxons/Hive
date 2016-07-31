package com.hexonxons.hive.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.hexonxons.hive.Constants;
import com.hexonxons.hive.R;

public class SetupActivity extends AppCompatActivity {
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.ACTION.BROADCAST_SETUP_FINISHED: {
                    // Setup done. Start main activity.
                    startMainActivity();
                    break;
                }

                default: {
                    throw new IllegalArgumentException("Illegal intent action: " + intent.getAction());
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getSharedPreferences(Constants.SHARED_PREFERENCES.NAME, MODE_PRIVATE)
                .getBoolean(Constants.SHARED_PREFERENCES.KEY_INSTALLED, false)) {
            startMainActivity();
        } else {
            // Start setup service.
            startService(new Intent(this, SetupService.class));
            // Register receiver.
            LocalBroadcastManager.getInstance(this)
                    .registerReceiver(mReceiver, new IntentFilter(Constants.ACTION.BROADCAST_SETUP_FINISHED));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mReceiver);
    }

    private void startMainActivity() {
        Intent activityIntent = new Intent(this, MainActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(activityIntent);
    }
}
