package com.example.monechattest.tab2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monechattest.R;

import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class Fragment2 extends Fragment {

    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private List<ChatMessage> mMessageList;
    private EditText mInputEditText;
    private Button mSendButton;

    private OkHttpClient client;
    private WebSocket webSocket;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_2, container, false);

        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mInputEditText = rootView.findViewById(R.id.input);
        mSendButton = rootView.findViewById(R.id.sendButton);

        mMessageList = new ArrayList<>();
        mAdapter = new ChatAdapter(mMessageList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        setupWebSocket();

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = mInputEditText.getText().toString();
                if (!messageText.isEmpty()) {
                    sendMessage(messageText);
                    mInputEditText.setText("");
                }
            }
        });

        return rootView;
    }

    private void setupWebSocket() {
        client = new OkHttpClient();
        Request request = new Request.Builder().url("wss://your.websocket.url").build();
        webSocket = client.newWebSocket(request, new ChatWebSocketListener());
    }

    private void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
            ChatMessage chatMessage = new ChatMessage(message, true);
            mMessageList.add(chatMessage);
            mAdapter.notifyItemInserted(mMessageList.size() - 1);
            mRecyclerView.scrollToPosition(mMessageList.size() - 1);
        }
    }

    private class ChatWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            webSocket.send("Hello from fragment!");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ChatMessage message = new ChatMessage(text, false);
                    mMessageList.add(message);
                    mAdapter.notifyItemInserted(mMessageList.size() - 1);
                    mRecyclerView.scrollToPosition(mMessageList.size() - 1);
                }
            });
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            // Handle binary message
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(1000, null);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            // Handle failure
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webSocket != null) {
            webSocket.close(1000, "Fragment destroyed");
        }
        if (client != null) {
            client.dispatcher().executorService().shutdown();
        }
    }
}