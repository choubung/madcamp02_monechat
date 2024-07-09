package com.example.monechattest;

import android.app.Application;
import com.kakao.sdk.common.KakaoSdk; // Kakao SDK 라이브러리 import 경로는 실제 사용 환경에 따라 다를 수 있습니다
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import com.example.monechattest.BuildConfig;

public class GlobalApplication extends Application {
    private static final String TAG = "GlobalApplication";
    private Socket mSocket;
    private ChatReceiver chatReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        // 다른 초기화 코드들
        SocketManager socketManager = SocketManager.getInstance();
        mSocket = socketManager.getSocket();
        mSocket.on("chatMessage", onChatMessage);
        mSocket.connect();

        String kakaoNativeAppKey = BuildConfig.KAKAO_NATIVE_APP_KEY;

        // Kakao SDK 초기화
        KakaoSdk.init(this, kakaoNativeAppKey);

        // ChatReceiver 등록
        chatReceiver = new ChatReceiver();
        IntentFilter filter = new IntentFilter("NEW_CHAT_MESSAGE");
        registerReceiver(chatReceiver, filter);
    }

    private Emitter.Listener onChatMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "New chat message received");

            Intent intent = new Intent(GlobalApplication.this, ChatReceiver.class);
            intent.setAction("NEW_CHAT_MESSAGE");
            intent.putExtra("message", args[0].toString());
            sendBroadcast(intent);
        }
    };

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterReceiver(chatReceiver);
    }
}