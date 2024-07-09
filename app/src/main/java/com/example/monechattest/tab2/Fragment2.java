package com.example.monechattest.tab2;

import android.content.BroadcastReceiver;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.example.monechattest.ChatMessageListener;
import com.example.monechattest.ChatReceiver;
import com.example.monechattest.SocketManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Fragment2 extends Fragment implements ChatMessageListener {

    private static final String TAG = "Fragment2";
    private static final String PREFERENCES_NAME = "MyAppPreferences";
    private static final String JWT_TOKEN_KEY = "jwt_token";
    private static final String CHAT_ROOM_IDENTIFIER_KEY = "chat_room_identifier";
    private static final String CHAT_MESSAGES_KEY = "chat_messages";
    private static final String USER_NAME_KEY = "user_name";

    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private List<ChatMessage> mMessageList;
    private EditText mInputEditText;
    private Button mSendButton;
    private Button mNewChatButton; // 새로운 채팅방 생성 버튼

    private ChatReceiver chatReceiver; // 메시지 수신기

    private Socket socket;
    private String chatRoomIdentifier;
    private boolean isRoomJoined = false; // 채팅방 입장 여부를 추적
    private boolean isSocketInitialized = false; // 소켓 초기화 여부를 추적
    private String userName; // 사용자 이름

    private Set<String> sentMessages = new HashSet<>(); // 보낸 메시지 추적

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

        // BroadcastReceiver 등록
        chatReceiver = new ChatReceiver();
        IntentFilter filter = new IntentFilter("NEW_CHAT_MESSAGE");
        requireContext().registerReceiver(chatReceiver, filter);

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

        // SharedPreferences에서 사용자 이름 로드
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString(USER_NAME_KEY, "Unknown User");

        initializeSocket();

        // 이전에 저장된 채팅방 상태와 메시지 복원
        chatRoomIdentifier = sharedPreferences.getString(CHAT_ROOM_IDENTIFIER_KEY, null);
        if (chatRoomIdentifier != null) {
            openChatRoom(chatRoomIdentifier);
        }

        loadChatMessages();

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
                chatRoomIdentifier = chatRoomName;
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
                chatRoomIdentifier = inviteCode;
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

        // 채팅방에 입장 또는 개설 (서버에 따라 다름)
        if (!isRoomJoined) {
            sendMessage("joinRoom", chatRoomIdentifier);
            isRoomJoined = true;

            // 채팅방 상태 저장
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(CHAT_ROOM_IDENTIFIER_KEY, chatRoomIdentifier);
            editor.apply();
        }
    }

    private void initializeSocket() {
        if (isSocketInitialized) {
            // 이미 소켓이 초기화되어 있는 경우, 재연결을 방지
            return;
        }

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString(JWT_TOKEN_KEY, null);

        if (jwtToken == null) {
            Log.e(TAG, "JWT Token is missing!");
            return;
        }

        SocketManager socketManager = SocketManager.getInstance();
        if (!socketManager.isSocketConnected()) {
            socketManager.setAuthToken(jwtToken);
            socket = socketManager.getSocket();

            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            socket.on("chatMessage", onChatMessage); // 이벤트 리스너 설정
            socket.connect();
            Log.d(TAG, "Socket connected and listener set up"); // 소켓 연결 로그 추가

            isSocketInitialized = true; // 소켓이 초기화되었음을 표시
        } else {
            socket = socketManager.getSocket();
            socket.on("chatMessage", onChatMessage); // 이벤트 리스너 설정
        }
    }

    private void sendMessage(String message) {
        sendMessage("chatMessage", message);
    }

    private void sendMessage(String event, String message) {
        if (socket != null) {
            JSONObject data = new JSONObject();
            try {
                data.put("event", event);
                data.put("message", message);
                data.put("userName", userName); // 사용자 이름 추가
                Log.d(TAG, "Sending: " + data.toString());
                socket.emit("message", data.toString()); // JSON 객체를 문자열로 변환하여 전송

                if ("chatMessage".equals(event)) {
                    if (!event.equals("joinRoom")) {
                        sentMessages.add(message); // 보낸 메시지 저장
                    }
                    ChatMessage chatMessage = new ChatMessage(message, true, userName); // 사용자 이름 추가
                    mMessageList.add(chatMessage);
                    mAdapter.notifyItemInserted(mMessageList.size() - 1);
                    mRecyclerView.scrollToPosition(mMessageList.size() - 1);
                    saveChatMessages(); // 메시지 저장
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveChatMessages() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mMessageList);
        editor.putString(CHAT_MESSAGES_KEY, json);
        editor.apply();
    }

    private void loadChatMessages() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(CHAT_MESSAGES_KEY, null);
        if (json != null) {
            Type type = new TypeToken<List<ChatMessage>>() {}.getType();
            List<ChatMessage> messages = gson.fromJson(json, type);
            mMessageList.addAll(messages);
            mAdapter.notifyDataSetChanged();
        }
    }

    private final Emitter.Listener onConnect = args -> {
        Log.d(TAG, "Connected");
        if (chatRoomIdentifier != null && !isRoomJoined) {
            sendMessage("joinRoom", chatRoomIdentifier);
            isRoomJoined = true;
        }
    };

    private final Emitter.Listener onDisconnect = args -> Log.d(TAG, "Disconnected");

    private final Emitter.Listener onChatMessage = args -> {
        Log.d(TAG, "onChatMessage event triggered");
        if (args[0] != null) {
            JSONObject messageData = (JSONObject) args[0];
            Log.d(TAG, "New message: " + messageData);

            // 이 부분을 수정하여 프래그먼트가 연결된 경우에만 UI 작업을 수행하도록 합니다.
            if (!isAdded() || getActivity() == null) {
                Log.d(TAG, "Fragment not attached to an activity");
                return;
            }

            getActivity().runOnUiThread(() -> {
                try {
                    String message = messageData.getString("message");
                    String userName = messageData.getString("username");
                    String profileImage = messageData.getString("profile_image");
                    String timestamp = messageData.getString("timestamp");

                    // 보낸 메시지와 동일한 메시지가 서버에서 돌아온 경우 무시
                    if (sentMessages.contains(message)) {
                        sentMessages.remove(message);
                        return;
                    }

                    ChatMessage chatMessage = new ChatMessage(message, false, userName, profileImage, timestamp);
                    mMessageList.add(chatMessage);
                    mAdapter.notifyItemInserted(mMessageList.size() - 1);
                    mRecyclerView.scrollToPosition(mMessageList.size() - 1);
                    saveChatMessages(); // 메시지 저장
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    };

    @Override
    public void onNewChatMessage(String message) {
        // 새로운 채팅 메시지를 처리
        ChatMessage chatMessage = new ChatMessage(message, false, userName);
        mMessageList.add(chatMessage);
        mAdapter.notifyItemInserted(mMessageList.size() - 1);
        mRecyclerView.scrollToPosition(mMessageList.size() - 1);
        saveChatMessages(); // 메시지 저장
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 소켓 연결을 유지하기 위해 이 부분에서 소켓 연결을 종료하지 않음

        requireContext().unregisterReceiver(chatReceiver);
    }
}
