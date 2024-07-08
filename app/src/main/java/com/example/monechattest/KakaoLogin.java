package com.example.monechattest;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;

import com.example.monechattest.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;

public class KakaoLogin extends AppCompatActivity {
    private static final String serverAddress = BuildConfig.SERVER_ADDRESS;
    private static final String TAG = "KakaoLogin";
    private static final String POST_URL = serverAddress + "auth/kakao/signin";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String PREFERENCES_NAME = "MyAppPreferences";
    private static final String JWT_TOKEN_KEY = "jwt_token";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao_login);

        // SharedPreferences에서 JWT 토큰 확인
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString(JWT_TOKEN_KEY, null);
        if (jwtToken != null) {
            // JWT 토큰이 존재하면 MainActivity로 이동
            moveToMainActivity();
            return; // 로그인 버튼 클릭 처리를 건너뜀
        }

        Button kakaoLoginButton = findViewById(R.id.kakao_login_button);
        Button guestLoginButton = findViewById(R.id.guest_login_button);

        kakaoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("KakaoLogin", "start login kakaotalk process");
                UserApiClient.getInstance().loginWithKakaoAccount(KakaoLogin.this, (oAuthToken, throwable) -> {
                    if (throwable != null) {
                        Log.e("KakaoLogin", "로그인 실패", throwable);
                    } else if (oAuthToken != null) {
                        Log.i("KakaoLogin", "로그인 성공" + oAuthToken.getAccessToken());

                        // POST 요청을 보내기 위한 메서드 호출
                        sendPostRequest(oAuthToken.getAccessToken());
                        moveToMainActivity();
                    }
                    return null;
                });
            }
        });

        guestLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("GuestLogin", "게스트 로그인 선택");
                moveToMainActivity();
            }
        });
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(KakaoLogin.this, MainActivity.class);
        startActivity(intent);
        finish(); // LoginActivity 종료
    }

    private void sendPostRequest(String kakaoToken) {
        OkHttpClient client = new OkHttpClient();

        // JSON body for the POST request
        String jsonBody = "{}";
        RequestBody body = RequestBody.create(jsonBody, JSON);

        // Build the request
        Request request = new Request.Builder()
                .url(POST_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + kakaoToken)
                .build();

        // Execute the request in a new thread to avoid NetworkOnMainThreadException
        new Thread(() -> {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    // Handle the successful response
                    String responseData = response.body().string();

                    // Parse JSON response to get accessToken
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        String accessToken = jsonResponse.getString("accessToken");
                        Log.i(TAG, "Access Token: " + accessToken);

                        // JWT 토큰을 SharedPreferences에 저장
                        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(JWT_TOKEN_KEY, accessToken);
                        editor.apply();
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage(), e);
                    }
                } else {
                    // Handle the failed response
                    String errorData = response.body().string();
                    Log.e(TAG, "Sign in failed: " + errorData);
                }
            } catch (IOException e) {
                // Handle the exception
                Log.e(TAG, "Error: " + e.getMessage(), e);
            }
        }).start();
    }
}