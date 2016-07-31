package com.hexonxons.hive.app;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.hexonxons.hive.Constants;
import com.hexonxons.hive.R;
import com.hexonxons.hive.data.ChatMessage;
import com.hexonxons.hive.database.DbManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SetupService extends IntentService {
    private static final String TAG = "SetupService";

    // Shared preferences.
    private SharedPreferences mPreferences = null;
    // Db manager.
    private DbManager mDbManager = null;
    // Local broadcast manager.
    private LocalBroadcastManager mBroadcastManager = null;

    public SetupService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Get db manager instance.
        mDbManager = DbManager.getInstance(this);
        // Get local broadcast manager instance.
        mBroadcastManager = LocalBroadcastManager.getInstance(this);
        // Read line number from shared preferences.
        mPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES.NAME, MODE_PRIVATE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InputStream stream = getResources().openRawResource(R.raw.bot_messages);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        try {
            String line = reader.readLine();
            while (line != null) {
                // Save message to database.
                mDbManager.insertBotMessage(ChatMessage.create(false, line));
                // Read line from bot messages file.
                line = reader.readLine();
            }

            // Set installed flag.
            // Do not use `.apply` there. It is already in background.
            mPreferences.edit()
                    .putBoolean(Constants.SHARED_PREFERENCES.KEY_INSTALLED, true)
                    .commit();

            // Send finish action.
            mBroadcastManager.sendBroadcast(new Intent(Constants.ACTION.BROADCAST_SETUP_FINISHED));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close streams.
            try {
                reader.close();
            } catch (IOException e) {
                // Nothing to do.
            }
        }
    }
}
