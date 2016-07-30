package com.hexonxons.hive.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.hexonxons.hive.data.ChatMessage;

public final class DbManager {
    // Sync mutex.
    private static final Object MUTEX = new Object();
    // Singleton instance.
    private static DbManager sInstance = null;

    // Database object.
    private SQLiteDatabase mDatabase = null;

    // Empty chat messages.
    private static final ChatMessage[] EMPTY_MESSAGES = {};

    public static DbManager getInstance(@NonNull Context context) {
        synchronized (MUTEX) {
            if (sInstance == null) {
                sInstance = new DbManager(context.getApplicationContext());
            }

            return sInstance;
        }
    }

    private DbManager(Context context) {
        // Get writable database.
        mDatabase = new DbOpenHelper(context).getWritableDatabase();
        // https://code.google.com/p/android/issues/detail?id=11607
        mDatabase.execSQL("PRAGMA foreign_keys=ON;");
    }

    /***********************************************
     * Message methods.                            *
     ***********************************************/
    public void insertMessage(@NonNull ChatMessage message) {
        mDatabase.insert(DbOpenHelper.MESSAGE._TABLE_NAME, null, message.toContentValues());
    }

    @NonNull
    public ChatMessage[] getMessages() {
        Cursor cursor = mDatabase.query(DbOpenHelper.MESSAGE._TABLE_NAME, null, null, null, null, null, null);

        if (cursor.getColumnIndex(DbOpenHelper.MESSAGE._ID) == -1 || cursor.isAfterLast()) {
            cursor.close();
            return EMPTY_MESSAGES;
        }

        ChatMessage[] messages = new ChatMessage[cursor.getCount()];
        for (int i = 0; cursor.moveToNext(); ++i) {
            messages[i] = ChatMessage.create(cursor);
        }
        cursor.close();

        return messages;
    }
}
