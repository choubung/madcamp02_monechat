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
// import com.kakao.sdk.common.util.Utility;

public class KakaoLogin extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao_login);

//        String keyHash = Utility.INSTANCE.getKeyHash(this);
//        Log.i("GlobalApplication", keyHash);

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
}