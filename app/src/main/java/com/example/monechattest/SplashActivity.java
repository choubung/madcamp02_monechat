package com.example.monechattest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.example.monechattest.BuildConfig;

public class SplashActivity extends AppCompatActivity {
    private static final String PREFERENCES_NAME = "MyAppPreferences";
    private static final String JWT_TOKEN_KEY = "jwt_token";
    private static final String SERVER_URL = BuildConfig.SERVER_ADDRESS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // 스플래시 레이아웃 설정

        // SharedPreferences에서 JWT 토큰 확인
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString(JWT_TOKEN_KEY, null);
        if (jwtToken != null) {
            // JWT 토큰이 존재하면 서버에 연결 시도
            Log.i("JWT token check", "jwtToken is Exist: " + jwtToken);
            try {
                connectToServer(jwtToken);
            } catch (Exception e) {
                Log.e("SocketIO", "Error connecting to server", e);
                reloadActivity();
            }
        } else {
            // JWT 토큰이 없으면 LoginActivity로 이동
            Log.i("JWT token check", "Check Fail");
            moveToLoginActivity();
        }
    }

    private void connectToServer(String jwtToken) {
        SocketManager socketManager = SocketManager.getInstance();

        if (socketManager.isSocketConnected()) {
            Log.i("SocketIO", "Socket is already connected");
            moveToMainActivity();
        } else {
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject();

            try {
                RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
                Request request = new Request.Builder()
                        .url(SERVER_URL + "/auth")
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Bearer " + jwtToken)
                        .build();

                Log.i("HTTP", "Sending token: " + jwtToken);

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("HTTP", "Error on POST request", e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reloadActivity();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i("HTTP", "Response code: " + response.code());
                        Log.i("HTTP", "Response message: " + response.message());

                        if (response.isSuccessful()) {
                            Log.i("HTTP", "Successfully authenticated with server");

                            try {
                                // 싱글톤 패턴을 사용하여 Socket 객체에 인증 토큰 설정
                                socketManager.setAuthToken(jwtToken);
                                Socket mSocket = socketManager.getSocket();
                                mSocket.connect();

                                mSocket.on(Socket.EVENT_CONNECT, args -> {
                                    Log.i("SocketIO", "Connected to server");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            moveToMainActivity();
                                        }
                                    });
                                });

                                mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> {
                                    Log.e("SocketIO", "Connection error: " + args[0]);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            reloadActivity();
                                        }
                                    });
                                });
                            } catch (Exception e) {
                                Log.e("SocketIO", "Error initializing socket", e);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        reloadActivity();
                                    }
                                });
                            }
                        } else {
                            Log.e("HTTP", "Authentication failed with server");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    reloadActivity();
                                }
                            });
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("HTTP", "Error creating POST request", e);
                reloadActivity();
            }
        }
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // SplashActivity 종료
    }

    private void moveToLoginActivity() {
        Intent intent = new Intent(SplashActivity.this, KakaoLogin.class);
        startActivity(intent);
        finish(); // SplashActivity 종료
    }

    private void reloadActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
