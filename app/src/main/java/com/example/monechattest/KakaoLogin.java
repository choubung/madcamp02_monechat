package com.example.monechattest;

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

public class KakaoLogin extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao_login);

        // 카카오 SDK 초기화
        KakaoSdk.init(this, "카카오 앱키");

        Button kakaoLoginButton = findViewById(R.id.kakao_login_button);
        kakaoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserApiClient.getInstance().loginWithKakaoTalk(KakaoLogin.this, (oAuthToken, throwable) -> {
                    if (throwable != null) {
                        Log.e("KakaoLogin", "로그인 실패", throwable);
                    } else if (oAuthToken != null) {
                        Log.i("KakaoLogin", "로그인 성공");
                        moveToMainActivity();
                    }
                    return null;
                });
            }
        });
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(KakaoLogin.this, MainActivity.class);
        startActivity(intent);
        finish(); // LoginActivity 종료
    }
}