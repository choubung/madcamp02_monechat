package com.example.monechattest;

import android.app.Application;
import com.kakao.sdk.common.KakaoSdk; // Kakao SDK 라이브러리 import 경로는 실제 사용 환경에 따라 다를 수 있습니다

import com.example.monechattest.BuildConfig;

public class GlobalApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 다른 초기화 코드들

        String kakaoNativeAppKey = BuildConfig.KAKAO_NATIVE_APP_KEY;

        // Kakao SDK 초기화
        KakaoSdk.init(this, kakaoNativeAppKey);
    }
}