package com.hexonxons.hive.app;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

public class HiveApplication extends Application {
    private boolean mBotServiceIsStarted = false;
    private int mForegroundCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                if (!mBotServiceIsStarted && activity instanceof MainActivity) {
                    // Aaand start bot service.
                    startService(new Intent(HiveApplication.this, BotService.class));
                    mBotServiceIsStarted = true;
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                mForegroundCount++;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                mForegroundCount--;
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    public boolean isInForeground() {
        return mForegroundCount > 0;
    }
}
