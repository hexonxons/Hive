package com.hexonxons.hive.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.hexonxons.hive.Constants;

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get shared preferences.
        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCES.NAME, MODE_PRIVATE);

        // Create appropriate intent.
        Intent intent;
        if (preferences.getBoolean(Constants.SHARED_PREFERENCES.KEY_INSTALLED, false)) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, SetupActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Start appropriate activity.
        finish();
        startActivity(intent);
    }
}
