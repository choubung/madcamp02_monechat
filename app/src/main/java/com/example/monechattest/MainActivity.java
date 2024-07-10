package com.example.monechattest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.monechattest.database.SharedViewModel;
import com.example.monechattest.tab1.ExpenseItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kakao.sdk.common.util.Utility;

import com.example.monechattest.tab1.Fragment1;
import com.example.monechattest.tab2.Fragment2;
import com.example.monechattest.tab3.Fragment3;
import com.example.monechattest.tab4.Fragment4;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String CHANNEL_ID = "chat_messages";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    // 바텀 네비게이션
    BottomNavigationView bottomNavigationView; // 하단 탭 뷰
    // 프래그먼트 변수
    Fragment1 fragment1; // 연락처탭
    Fragment2 fragment2; // 사진탭
    Fragment3 fragment3; //
    Fragment4 fragment4;

    private SharedViewModel sharedViewModel;

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
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                if (item.getItemId() == R.id.list_menu) { // switch로 했더니 오류 발생하여 if문으로 변경
                    selectedFragment = fragment1;
                } else if (item.getItemId() == R.id.chat) {
                    selectedFragment = fragment2;
                } else if (item.getItemId() == R.id.expense_view) {
                    selectedFragment = fragment3;
                } else if (item.getItemId() == R.id.my_page) {
                    selectedFragment = fragment4;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, selectedFragment).commitAllowingStateLoss();
                return true;
            }
        });

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // 인텐트를 통해 전달된 데이터 처리
        if (getIntent().getBooleanExtra("navigateToFragment2", false)) {
            ExpenseItem newItem = (ExpenseItem) getIntent().getSerializableExtra("expenseItem");
            if (newItem != null) {
                sharedViewModel.setNewExpense(newItem);
                Log.d("MainActivity", "New expense item received: " + newItem.getDescription());

                // Fragment2로 이동
                bottomNavigationView.setSelectedItemId(R.id.chat);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_layout, fragment2)
                        .addToBackStack(null)
                        .commit();
            }
        }

        // Notification Channel 생성
        createNotificationChannel();

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
}