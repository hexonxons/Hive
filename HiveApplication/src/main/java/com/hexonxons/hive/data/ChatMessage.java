package com.hexonxons.hive.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.hexonxons.hive.database.DbOpenHelper;
import com.hexonxons.hive.database.DbOpenHelper.MESSAGE;

public final class ChatMessage implements Parcelable {
    public int id = Integer.MIN_VALUE;
    public boolean isUserMe = false;
    public String message = null;

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

    private ChatMessage() {

    }

    private ChatMessage(Parcel in) {
        id = in.readInt();
        isUserMe = in.readInt() == 1;
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(isUserMe ? 1 : 0);
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static ChatMessage create(boolean isUserMe, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.isUserMe = isUserMe;
        chatMessage.message = message;
        return chatMessage;
    }

    public static ChatMessage create(Cursor cursor) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.id = cursor.getInt(cursor.getColumnIndexOrThrow(MESSAGE._ID));
        chatMessage.isUserMe = cursor.getInt(cursor.getColumnIndexOrThrow(MESSAGE._IS_USER_ME)) == 1;
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
