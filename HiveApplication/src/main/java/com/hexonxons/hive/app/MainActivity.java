package com.hexonxons.hive.app;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.hexonxons.hive.Constants;
import com.hexonxons.hive.R;
import com.hexonxons.hive.data.ChatMessage;
import com.hexonxons.hive.database.DbManager;
import com.hexonxons.hive.widget.ChatAdapter;
import com.hexonxons.hive.widget.SpacesItemDecorator;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_REQUEST_CODE = 0x0000DEAD;
    private static final String TAG_LOCATION_SENT = "TAG_LOCATION_SENT";

    // Chat recycler.
    private RecyclerView mRecyclerView = null;
    // Chat adapter.
    private ChatAdapter mAdapter = null;
    // Chat message.
    private TextView mMessageView = null;
    // Db manager.
    private DbManager mDbManager = null;
    // Location sent flag.
    private boolean mIsLocationSent = false;
    // Location listener.
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mIsLocationSent = true;
            mAdapter.addMessages(ChatMessage.create(true,
                    location.getLatitude() + " " + location.getLongitude()));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    // Message receiver.
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.ACTION.BROADCAST_MESSAGE: {
                    // Add message to list.
                    mAdapter.addMessages(intent.getParcelableExtra(Constants.BUNDLE.KEY_MESSAGE));
                    // Scroll to last position.
                    mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                    break;
                }

                default: {
                    throw new IllegalArgumentException("Illegal intent action: " + intent.getAction());
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mIsLocationSent = savedInstanceState.getBoolean(TAG_LOCATION_SENT);
        }

        setContentView(R.layout.activity_main);

        // Get database manager.
        mDbManager = DbManager.getInstance(this);

        // Find recycler view.
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        // Set layout manager.
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        manager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(manager);
        // Add item decorator.
        mRecyclerView.addItemDecoration(new SpacesItemDecorator(this));
        // Create adapter.
        mAdapter = new ChatAdapter(LayoutInflater.from(this));
        // Set adapter.
        mRecyclerView.setAdapter(mAdapter);

        // Find chat message view.
        mMessageView = (TextView) findViewById(R.id.message);
        // Set send button click listener.
        findViewById(R.id.send).setOnClickListener(view -> {
            // Create chat message.
            ChatMessage message = ChatMessage.create(true, mMessageView.getText().toString());

            if (TextUtils.isEmpty(message.message)) {
                return;
            }

            // Add message to database.
            mDbManager.insertMessage(message);
            // Add message to chat list.
            mAdapter.addMessages(message);
            // Clear message text.
            mMessageView.setText(null);
            // Scroll to last position.
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
        });

        if (!mIsLocationSent) {
            int locationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (locationPermission == PackageManager.PERMISSION_GRANTED) {
                setupLocationUpdates();
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int itemCount = mAdapter.getItemCount();
        if (itemCount == 0) {
            // Set messages from database.
            mAdapter.addMessages(mDbManager.getMessages());
        } else {
            // Add messages.
            mAdapter.addMessages(mDbManager.getMessages(itemCount));
        }
        // Scroll to last position.
        mRecyclerView.scrollToPosition(mAdapter.getItemCount());
        // Register receiver.
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mReceiver, new IntentFilter(Constants.ACTION.BROADCAST_MESSAGE));

        // Cancel notification.
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.cancel(BotService.NOTIFICATION_ID);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(TAG_LOCATION_SENT, mIsLocationSent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mReceiver);
    }

    private void setupLocationUpdates() {
        // Get location manager instance.
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
            return;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location == null) {
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, mLocationListener, null);
            if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Please, turn on ur gps.")
                        .setPositiveButton("OK", (d, id) -> {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            d.dismiss();
                        })
                        .setNegativeButton("Cancel", (d, id) -> {
                            d.cancel();
                        });
                builder.create().show();
            }
        } else {
            mIsLocationSent = true;
            mDbManager.insertMessage(ChatMessage.create(true, location.getLatitude() + " " + location.getLongitude()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupLocationUpdates();
            }
        }
    }
}
