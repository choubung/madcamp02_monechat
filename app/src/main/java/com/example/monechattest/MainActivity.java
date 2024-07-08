package com.example.monechattest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kakao.sdk.common.util.Utility;

import com.example.monechattest.tab1.Fragment1;
import com.example.monechattest.tab2.Fragment2;
import com.example.monechattest.tab3.Fragment3;
import com.example.monechattest.tab4.Fragment4;

// 커밋 테스트
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String CHANNEL_ID = "chat_messages";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    private ChatReceiver chatReceiver;
    // 바텀 네비게이션
    BottomNavigationView bottomNavigationView; // 하단 탭 뷰
    // 프래그먼트 변수
    Fragment1 fragment1; // 연락처탭
    Fragment2 fragment2; // 사진탭
    Fragment3 fragment3; //
    Fragment4 fragment4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 키 해시 확인
        String keyHash = Utility.INSTANCE.getKeyHash(this);
        Log.d("KakaoKeyHash", keyHash);

        // 프래그먼트 생성
        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment4 = new Fragment4();

        // 바텀 네비게이션
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // 초기 플래그먼트 설정
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment1).commitAllowingStateLoss();

        // 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.list_menu) { // switch로 했더니 오류 발생하여 if문으로 변경
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment1).commitAllowingStateLoss();
                    return true;
                } else if (item.getItemId() == R.id.chat) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment2).commitAllowingStateLoss();
                    return true;
                } else if (item.getItemId() == R.id.expense_view) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment3).commitAllowingStateLoss();
                    return true;
                } else if (item.getItemId() == R.id.my_page) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment4).commitAllowingStateLoss();
                    return true;
                }
                return true;
            }
        });

        // Notification Channel 생성
        createNotificationChannel();

        // BroadcastReceiver 등록
        chatReceiver = new ChatReceiver();
        IntentFilter filter = new IntentFilter("NEW_CHAT_MESSAGE");
        registerReceiver(chatReceiver, filter);

        // Android 13 이상에서 알림 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Notification permission granted");
            } else {
                Log.d(TAG, "Notification permission denied");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(chatReceiver);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Chat Messages";
            String description = "Channel for chat message notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public class ChatReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("NEW_CHAT_MESSAGE".equals(intent.getAction())) {
                String message = intent.getStringExtra("message");
                Log.d(TAG, "New chat message: " + message);

                // 알림 생성
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_chat) // 적절한 아이콘으로 변경
                        .setContentTitle("New Chat Message")
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, builder.build());

                // 메시지를 각 탭으로 전달
                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment instanceof ChatMessageListener) {
                        ((ChatMessageListener) fragment).onNewChatMessage(message);
                    }
                }
            }
        }
    }
}