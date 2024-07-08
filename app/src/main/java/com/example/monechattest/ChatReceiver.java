package com.example.monechattest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ChatReceiver extends BroadcastReceiver {
    private static final String TAG = "ChatReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("NEW_CHAT_MESSAGE".equals(intent.getAction())) {
            String message = intent.getStringExtra("message");
            Log.d(TAG, "New chat message: " + message);
            Toast.makeText(context, "New chat message: " + message, Toast.LENGTH_LONG).show();
            // 여기에 알림을 띄우거나 UI 업데이트를 추가할 수 있습니다.
        }
    }
}