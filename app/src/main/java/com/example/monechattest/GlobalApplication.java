package com.example.monechattest;

import android.app.Application;

import com.example.monechattest.tab2.Fragment2;
import com.kakao.sdk.common.KakaoSdk; // Kakao SDK 라이브러리 import 경로는 실제 사용 환경에 따라 다를 수 있습니다

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import android.app.Activity;

import android.Manifest;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.monechattest.BuildConfig;

public class GlobalApplication extends Application {
    private static final String TAG = "GlobalApplication";
    private static final String CHANNEL_ID = "chat_messages";
    private Socket mSocket;
    private ChatReceiver chatReceiver;
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1001;

    @Override
    public void onCreate() {
        super.onCreate();
        // 다른 초기화 코드들
        SocketManager socketManager = SocketManager.getInstance();
        mSocket = socketManager.getSocket();
        mSocket.on("chatMessage", onChatMessage);
        mSocket.connect();

        createNotificationChannel();

        String kakaoNativeAppKey = BuildConfig.KAKAO_NATIVE_APP_KEY;

        // Kakao SDK 초기화
        KakaoSdk.init(this, kakaoNativeAppKey);

        // ChatReceiver 등록
        chatReceiver = new ChatReceiver();
        IntentFilter filter = new IntentFilter("NEW_CHAT_MESSAGE");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(chatReceiver, filter, RECEIVER_EXPORTED);
        } else {
            registerReceiver(chatReceiver, filter);
        }
    }

    private Emitter.Listener onChatMessage = args ->  {
        Log.d(TAG, "New chat message received");
        Intent intent = new Intent(GlobalApplication.this, ChatReceiver.class);
        intent.setAction("NEW_CHAT_MESSAGE");
        intent.putExtra("message", args[0].toString());
        sendBroadcast(intent);
        Log.d(TAG, "BroadCast 보낸듯");

        if (args[0] != null) {
            // 메시지를 Fragment2로 전달
            if (getApplicationContext() instanceof FragmentActivity) {
                FragmentManager fragmentManager = ((FragmentActivity) getApplicationContext()).getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.main_layout);
                if (fragment instanceof Fragment2) {
                    ((Fragment2) fragment).onNewChatMessage(args[0]);
                }
            }
        }
    };

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (chatReceiver != null) {
            try {
                unregisterReceiver(chatReceiver);
            } catch (SecurityException e) {
                Log.e(TAG, "SecurityException while unregistering receiver: " + e.getMessage());
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Chat Messages";
            String description = "Channel for chat message notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // 권한 요청을 처리하는 메서드
    public void checkAndRequestNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_POST_NOTIFICATIONS);
            }
        }
    }
}