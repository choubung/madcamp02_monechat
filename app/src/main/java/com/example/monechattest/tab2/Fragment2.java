package com.example.monechattest.tab2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.monechattest.GlobalApplication;
import com.example.monechattest.R;
import com.example.monechattest.ChatMessageListener;
import com.example.monechattest.ChatReceiver;
import com.example.monechattest.SocketManager;
import com.example.monechattest.database.ExpenseViewModel;
import com.example.monechattest.database.SharedViewModel;
import com.example.monechattest.tab1.ExpenseItem;
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
    private List<ChatMessage> pendingMessages = new ArrayList<>(); // 임시 메시지 리스트 초기화
    private EditText mInputEditText;
    private Button mSendButton;
    private Button mNewChatButton; // 새로운 채팅방 생성 버튼

    private Socket socket;
    private String chatRoomIdentifier;
    private boolean isRoomJoined = false; // 채팅방 입장 여부를 추적
    private boolean isSocketInitialized = false; // 소켓 초기화 여부를 추적
    private String userName; // 사용자 이름

    private Set<String> sentMessages = new HashSet<>(); // 보낸 메시지 추적

    Intent serviceIntent;

    // private ExpenseViewModel expenseViewModel;
    private SharedViewModel sharedViewModel;

    private boolean expenseSent = false;  // 지출 내역 전송 여부 추적

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // SharedPreferences에서 사용자 이름 로드
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString(USER_NAME_KEY, "Unknown User");

        initializeSocket();

        // BroadcastReceiver 등록
        ChatReceiver chatReceiver = new ChatReceiver();
        IntentFilter filter = new IntentFilter("NEW_CHAT_MESSAGE");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().registerReceiver(chatReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            requireActivity().registerReceiver(chatReceiver, filter);
        }

        // 여기에 추가합니다.
        expenseSent = false;  // 지출 내역 전송 여부 초기화
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_2, container, false);

        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mInputEditText = rootView.findViewById(R.id.input);
        mSendButton = rootView.findViewById(R.id.sendButton);
        mNewChatButton = rootView.findViewById(R.id.newChatButton); // 새로운 채팅방 생성 버튼

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        sharedViewModel.getNewExpense().observe(getViewLifecycleOwner(), newExpense -> {
            if (newExpense != null && !expenseSent) {
                expenseSent = true;  // 전송 여부 설정
                // EditText에 데이터 설정
                mInputEditText.setText(newExpense.getDescription() + " " + newExpense.getAmount());
                // 상태 초기화
                sharedViewModel.setNewExpense(null);
            }
        });

        if (getArguments() != null) {
            ExpenseItem expenseItem = (ExpenseItem) getArguments().getSerializable("expenseItem");
            if (expenseItem != null) {
                String combinedText = expenseItem.getDescription() + " " + expenseItem.getAmount();
                mInputEditText.setText(combinedText);
            } else {
                mInputEditText.setText("");  // EditText 초기화
            }
        } else {
            mInputEditText.setText("");  // EditText 초기화
        }

        // 전달된 데이터 아이템 처리
        if (getArguments() != null) {
            ExpenseItem expenseItem = (ExpenseItem) getArguments().getSerializable("expenseItem");
            if (expenseItem != null) {
                String combinedText = expenseItem.getDescription() + " " + expenseItem.getAmount();
                mInputEditText.setText(combinedText);
            }
        }

        mMessageList = new ArrayList<>();
        mAdapter = new ChatAdapter(mMessageList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mNewChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewChatRoomDialog();
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

        // 이전에 저장된 채팅방 상태와 메시지 복원
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        chatRoomIdentifier = sharedPreferences.getString(CHAT_ROOM_IDENTIFIER_KEY, null);
        if (chatRoomIdentifier != null) {
            openChatRoom(chatRoomIdentifier);
        }

        loadChatMessages();

        // Initialize the ExpenseViewModel
        //expenseViewModel = new ViewModelProvider(requireActivity()).get(ExpenseViewModel.class);

        // Observe new expenses
        //expenseViewModel.getNewExpense().observe(getViewLifecycleOwner(), newExpense -> {
        //    if (newExpense != null) {
        //        sendMessage("" + newExpense.getDescription() + " " + newExpense.getAmount());
        //    }
        //});

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        for (ChatMessage message : pendingMessages) {
            mMessageList.add(message);
            mAdapter.notifyItemInserted(mMessageList.size() - 1);
            mRecyclerView.scrollToPosition(mMessageList.size() - 1);
            saveChatMessages(); // 메시지 저장
        }
        pendingMessages.clear(); // 임시 메시지 리스트 초기화
    }

//    // 채팅방 옵션 다이얼로그 표시
//    private void showChatOptionsDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//        builder.setTitle("채팅방 옵션 선택");
//
//        String[] options = {"새로운 채팅방 개설", "기존 채팅방 입장"};
//        builder.setItems(options, (dialog, which) -> {
//            if (which == 0) {
//                showNewChatRoomDialog();
//            } else {
//                showJoinChatRoomDialog();
//            }
//        });
//
//        builder.show();
//    }

    // 새로운 채팅방 개설 다이얼로그
    private void showNewChatRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("채팅방 코드 입력");

        final EditText input = new EditText(requireContext());
        input.setHint("채팅방 코드");
        builder.setView(input);

        builder.setPositiveButton("입장", (dialog, which) -> {
            String chatRoomName = input.getText().toString();
            if (!chatRoomName.isEmpty()) {
                chatRoomIdentifier = chatRoomName;
                openChatRoom(chatRoomName);
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
            if (!isRoomJoined) {
                socket.on("chatMessage", onChatMessage); // 이벤트 리스너 설정
            }
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
                Log.d(TAG, "Fragment not attached to an activity, adding to pendingMessages");
                try {
                    String message = messageData.getString("message");
                    String userName = messageData.getString("username");
                    String profileImage = messageData.getString("profile_image");
                    String timestamp = messageData.getString("timestamp");

                    ChatMessage chatMessage = new ChatMessage(message, false, userName, profileImage, timestamp);
                    pendingMessages.add(chatMessage);
                    Log.d(TAG, "Added to pendingMessages: " + message);
                    if (pendingMessages.size() >0) {
                        Log.d(TAG, "good: " + pendingMessages.get(pendingMessages.size()-1).getMessage());
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                    // serviceIntent.sendNotification(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    };

    @Override
    public void onNewChatMessage(Object args) {
        Log.d(TAG, "onChatMessage event triggered");
        if (args != null) {
            JSONObject messageData = (JSONObject) args;
            Log.d(TAG, "New message: " + messageData);

            // 이 부분을 수정하여 프래그먼트가 연결된 경우에만 UI 작업을 수행하도록 합니다.
            if (!isAdded() || getActivity() == null) {
                Log.d(TAG, "Fragment not attached to an activity, adding to pendingMessages");
                try {
                    String message = messageData.getString("message");
                    String userName = messageData.getString("username");
                    String profileImage = messageData.getString("profile_image");
                    String timestamp = messageData.getString("timestamp");

                    ChatMessage chatMessage = new ChatMessage(message, false, userName, profileImage, timestamp);
                    pendingMessages.add(chatMessage);
                    Log.d(TAG, "Added to pendingMessages: " + message);
                    if (pendingMessages.size() >0) {
                        Log.d(TAG, "good: " + pendingMessages.get(pendingMessages.size()-1).getMessage());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 소켓 연결을 유지하기 위해 이 부분에서 소켓 연결을 종료하지 않음
    }
}
