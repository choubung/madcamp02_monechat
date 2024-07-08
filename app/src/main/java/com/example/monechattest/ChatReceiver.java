package com.example.monechattest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import androidx.fragment.app.Fragment;

import android.widget.Toast;

public class ChatReceiver extends BroadcastReceiver {
    private static final String TAG = "ChatReceiver";
    private static final String CHANNEL_ID = "chat_messages";

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("NEW_CHAT_MESSAGE".equals(intent.getAction())) {
            String message = intent.getStringExtra("message");
            Log.d(TAG, "New chat message: " + message);

            // 알림 권한 확인
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // 알림 생성
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_chat) // 적절한 아이콘으로 변경
                        .setContentTitle("New Chat Message")
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(0, builder.build());
            } else {
                Log.w(TAG, "Notification permission not granted");
            }

            // 메시지를 각 탭으로 전달
            if (context instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) context;
                for (Fragment fragment : mainActivity.getSupportFragmentManager().getFragments()) {
                    if (fragment instanceof ChatMessageListener) {
                        ((ChatMessageListener) fragment).onNewChatMessage(message);
                    }
                }
            }

            Toast.makeText(context, "New chat message: " + message, Toast.LENGTH_LONG).show();
            // 여기에 알림을 띄우거나 UI 업데이트를 추가할 수 있습니다.
        }
    }
}