package com.hexonxons.hive.widget;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hexonxons.hive.R;
import com.hexonxons.hive.data.ChatMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    private static final int VIEW_TYPE_ME = 0;
    private static final int VIEW_TYPE_OPPONENT = 1;

    // Layout inflater.
    private final LayoutInflater mInflater;
    // Messages array.
    private final List<ChatMessage> mMessages = new ArrayList<>();

    public ChatAdapter(LayoutInflater inflater) {
        mInflater = inflater;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ME: {
                return new ChatViewHolder(mInflater.inflate(R.layout.chat_item_me, parent, false));
            }
            case VIEW_TYPE_OPPONENT: {
                return new ChatViewHolder(mInflater.inflate(R.layout.chat_item_opponent, parent, false));
            }
            default: {
                throw new RuntimeException("Unknown view type: " + viewType);
            }
        }
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        holder.message.setText(mMessages.get(position).message);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMessages.get(position).isUserMe ? VIEW_TYPE_ME : VIEW_TYPE_OPPONENT;
    }

    public void addMessages(ChatMessage... messages) {
        int itemCount = mMessages.size();
        mMessages.addAll(Arrays.asList(messages));
        notifyItemRangeInserted(itemCount, messages.length);
    }
}
