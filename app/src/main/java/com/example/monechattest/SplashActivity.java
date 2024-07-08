package com.example.monechattest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final String PREFERENCES_NAME = "MyAppPreferences";
    private static final String JWT_TOKEN_KEY = "jwt_token";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // 스플래시 레이아웃 설정

        // SharedPreferences에서 JWT 토큰 확인
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString(JWT_TOKEN_KEY, null);
        if (jwtToken != null) {
            // JWT 토큰이 존재하면 MainActivity로 이동
            Log.i("JWT token check", "Check Success: " + jwtToken);
            moveToMainActivity();
        } else {
            // JWT 토큰이 없으면 LoginActivity로 이동
            Log.i("JWT token check", "Check Fail");
            moveToLoginActivity();
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
}