package com.example.monechattest;

import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class SocketManager {
    private static SocketManager instance;
    private Socket mSocket;
    private String authToken;
    private static final String serverAddress = BuildConfig.SERVER_ADDRESS;

    private SocketManager() {
        // 초기화할 때는 기본 옵션으로 소켓을 생성합니다.
        initializeSocket(null);
    }

    public static SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    public Socket getSocket() {
        return mSocket;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
        // 새로운 인증 토큰을 사용하여 소켓을 재설정합니다.
        Map<String, String> auth = new HashMap<>();
        auth.put("token", token);
        initializeSocket(auth);
    }

    private void initializeSocket(Map<String, String> auth) {

        try {
            IO.Options options = IO.Options.builder().build();
            if (auth != null) {
                options.auth = auth;
            }
            mSocket = IO.socket(serverAddress, options);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public boolean isSocketConnected() {
        return mSocket != null && mSocket.connected();
    }
}
