package com.hexonxons.hive.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hexonxons.hive.R;

public class ChatViewHolder extends RecyclerView.ViewHolder {
    public TextView message = null;

    public ChatViewHolder(View itemView) {
        super(itemView);
        message = (TextView) itemView.findViewById(R.id.chat_message);
    }
}
