package com.example.monechattest.tab2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.monechattest.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatMessages.get(position);

        if (message.isSentByUser()) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(chatMessage);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(chatMessage, holder.itemView);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public static class SentMessageHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;

        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.text_message_body);
        }

        void bind(ChatMessage message) {
            messageTextView.setText(message.getMessage());
        }
    }

    public static class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView userNameTextView;
        public ImageView profileImageView;
        public TextView timestampTextView;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.text_message_body);
            userNameTextView = itemView.findViewById(R.id.text_message_name);
            profileImageView = itemView.findViewById(R.id.image_profile_picture);
            timestampTextView = itemView.findViewById(R.id.text_message_time);
        }

        void bind(ChatMessage message, @NonNull View itemView) {
            messageTextView.setText(message.getMessage());
            userNameTextView.setText(message.getUserName());
            timestampTextView.setText(message.getTimestamp());
            if (message.getProfileImage() != null && !message.getProfileImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(message.getProfileImage())
                        .into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.icon_backspace); // 기본 프로필 이미지 설정
            }
        }
    }
}
