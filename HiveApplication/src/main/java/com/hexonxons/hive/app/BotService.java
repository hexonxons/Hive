package com.hexonxons.hive.app;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;

import com.hexonxons.hive.Constants;
import com.hexonxons.hive.R;
import com.hexonxons.hive.data.ChatMessage;
import com.hexonxons.hive.database.DbManager;

public class BotService extends IntentService {
    private static final String TAG = "BotService";
    // Notification id.
    public static final int NOTIFICATION_ID = 0xDEADBEEF;

    // Shared preferences.
    private SharedPreferences mPreferences = null;
    // Db manager.
    private DbManager mDbManager = null;
    // Local broadcast manager.
    private LocalBroadcastManager mBroadcastManager = null;

    // Minutes * seconds * milliseconds. 3 seconds.
    private static final int ALARM_TIME = 5 * 1000;

    public BotService() {
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
        // Get message index.
        int messageIndex = mPreferences.getInt(Constants.SHARED_PREFERENCES.KEY_BOT_MESSAGE_INDEX, 1);
        // Get message by index.
        ChatMessage message = mDbManager.getBotMessage(messageIndex);
        if (message == null) {
            messageIndex = 1;
            message = mDbManager.getBotMessage(messageIndex);
        }
        if (message == null) {
            throw new RuntimeException("Message in null.");
        }
        // Update message index.
        messageIndex++;
        // Update message index.
        mPreferences.edit()
                .putInt(Constants.SHARED_PREFERENCES.KEY_BOT_MESSAGE_INDEX, messageIndex)
                .commit();

        // Add message to database.
        mDbManager.insertMessage(message);

        // Create message bundle.
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE.KEY_MESSAGE, message);

        if (((HiveApplication) getApplication()).isInForeground()) {
            // Send message intent.
            Intent messageIntent = new Intent(Constants.ACTION.BROADCAST_MESSAGE);
            messageIntent.putExtras(bundle);
            mBroadcastManager.sendBroadcast(messageIntent);
        } else {
            // Create activity intent.
            Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);
            activityIntent.putExtras(bundle);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setSmallIcon(R.drawable.ic_chat_bubble_opponent)
                    .setContentTitle("New message")
                    .setContentText(message.message)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
            manager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Set periodically launch service.
        Intent serviceIntent = new Intent(this, BotService.class);

        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, serviceIntent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ALARM_TIME, servicePendingIntent);
    }
}
