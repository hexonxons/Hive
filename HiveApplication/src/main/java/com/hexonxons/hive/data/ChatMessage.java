package com.hexonxons.hive.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.hexonxons.hive.database.DbOpenHelper;
import com.hexonxons.hive.database.DbOpenHelper.MESSAGE;

public final class ChatMessage {
    public boolean isUserMe = false;
    public String message = null;

    private ChatMessage() {

    }

    public static ChatMessage create(boolean isUserMe, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.isUserMe = isUserMe;
        chatMessage.message = message;
        return chatMessage;
    }

    public static ChatMessage create(Cursor cursor) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.isUserMe = cursor.getInt(cursor.getColumnIndexOrThrow(MESSAGE._ID)) != 0;
        chatMessage.message = cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE._MESSAGE));
        return chatMessage;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(DbOpenHelper.MESSAGE._IS_USER_ME, isUserMe ? 1 : 0);
        values.put(DbOpenHelper.MESSAGE._MESSAGE, message);
        return values;
    }
}
