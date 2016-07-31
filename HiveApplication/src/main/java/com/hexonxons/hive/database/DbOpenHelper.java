package com.hexonxons.hive.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "hive.db";
    private static final int DATABASE_VERSION = 1;

    // Messages table.
    public static final class MESSAGE {
        public static final String _TABLE_NAME = "message_table";

        public static final String _ID = "_id";
        public static final String _IS_USER_ME = "_user_id";
        public static final String _MESSAGE = "_message";

        private static final String _TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + _TABLE_NAME + '('
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + _IS_USER_ME + " INTEGER NOT NULL, "
                + _MESSAGE + " TEXT NOT NULL);";
    }

    // Bot messages table.
    public static final class BOT_MESSAGE {
        public static final String _TABLE_NAME = "bot_message_table";

        public static final String _ID = MESSAGE._ID;
        public static final String _IS_USER_ME = MESSAGE._IS_USER_ME;
        public static final String _MESSAGE = MESSAGE._MESSAGE;

        private static final String _TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + _TABLE_NAME + '('
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + _IS_USER_ME + " INTEGER NOT NULL, "
                + _MESSAGE + " TEXT NOT NULL);";
    }

    public DbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create main tables.
        db.execSQL(MESSAGE._TABLE_CREATE);
        db.execSQL(BOT_MESSAGE._TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
