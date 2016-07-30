package com.hexonxons.hive.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hexonxons.hive.R;
import com.hexonxons.hive.data.ChatMessage;
import com.hexonxons.hive.database.DbManager;
import com.hexonxons.hive.widget.SpacesItemDecorator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Chat recycler.
    private RecyclerView mRecyclerView = null;
    // Chat adapter.
    private ChatAdapter mAdapter = null;
    // Chat message.
    private TextView mMessageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        // Set messages from database.
        mAdapter.addMessages(DbManager.getInstance(this).getMessages());
        // Set adapter.
        mRecyclerView.setAdapter(mAdapter);

        // Find chat message view.
        mMessageView = (TextView) findViewById(R.id.message);
        // Set send button click listener.
        findViewById(R.id.send).setOnClickListener(view -> {
            // Create chat message.
            ChatMessage message = ChatMessage.create(true, mMessageView.getText().toString());
            // Add message to database.
            DbManager.getInstance(MainActivity.this).insertMessage(message);
            // Add message to chat list.
            mAdapter.addMessages(message);
            // Clear message text.
            mMessageView.setText(null);
            // Scroll to last position.
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
        });
    }

    private static class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {
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

    private static class ChatViewHolder extends RecyclerView.ViewHolder {
        public TextView message = null;

        public ChatViewHolder(View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.chat_message);
        }
    }
}
