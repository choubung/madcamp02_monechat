package com.example.monechattest.tab2;

import android.app.AlertDialog;
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
    private Button mNewChatButton; // 새로운 채팅방 생성 버튼

    private OkHttpClient client;
    private WebSocket webSocket;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_2, container, false);

        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mInputEditText = rootView.findViewById(R.id.input);
        mSendButton = rootView.findViewById(R.id.sendButton);
        mNewChatButton = rootView.findViewById(R.id.newChatButton); // 새로운 채팅방 생성 버튼

        mMessageList = new ArrayList<>();
        mAdapter = new ChatAdapter(mMessageList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        setupWebSocket();

        mNewChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChatOptionsDialog();
            }
        });

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

    // 채팅방 옵션 다이얼로그 표시
    private void showChatOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("채팅방 옵션 선택");

        String[] options = {"새로운 채팅방 개설", "기존 채팅방 입장"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                showNewChatRoomDialog();
            } else {
                showJoinChatRoomDialog();
            }
        });

        builder.show();
    }

    // 새로운 채팅방 개설 다이얼로그
    private void showNewChatRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("채팅방 이름 입력");

        final EditText input = new EditText(requireContext());
        input.setHint("채팅방 이름");
        builder.setView(input);

        builder.setPositiveButton("개설", (dialog, which) -> {
            String chatRoomName = input.getText().toString();
            if (!chatRoomName.isEmpty()) {
                openChatRoom(chatRoomName);
            }
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // 기존 채팅방 입장 다이얼로그
    private void showJoinChatRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("초대 코드 입력");

        final EditText input = new EditText(requireContext());
        input.setHint("초대 코드");
        builder.setView(input);

        builder.setPositiveButton("입장", (dialog, which) -> {
            String inviteCode = input.getText().toString();
            if (!inviteCode.isEmpty()) {
                openChatRoom(inviteCode); // 초대 코드를 통해 채팅방 입장
            }
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // 채팅방 열기 (새로운 채팅방 개설 또는 초대 코드로 입장)
    private void openChatRoom(String chatRoomIdentifier) {
        // + 버튼 숨기기
        mNewChatButton.setVisibility(View.GONE);

        // 채팅 화면 표시
        mRecyclerView.setVisibility(View.VISIBLE);
        mInputEditText.setVisibility(View.VISIBLE);
        mSendButton.setVisibility(View.VISIBLE);

        // WebSocket 설정
        setupWebSocket();

        // 채팅방에 입장 또는 개설 (서버에 따라 다름)
        sendMessage("Entering or creating chat room: " + chatRoomIdentifier);
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