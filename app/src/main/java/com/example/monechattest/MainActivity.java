package com.example.monechattest;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.monechattest.tab1.Fragment1;
import com.example.monechattest.tab2.Fragment2;
import com.example.monechattest.tab3.Fragment3;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// 커밋 테스트
public class MainActivity extends AppCompatActivity {
    // 바텀 네비게이션
    BottomNavigationView bottomNavigationView; // 하단 탭 뷰
    // 프래그먼트 변수
    Fragment1 fragment1; // 연락처탭
    Fragment2 fragment2; // 사진탭
    Fragment3 fragment3; //
    // TODO:Fragment4 fragment4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 프래그먼트 생성
        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        // TODO: fragment4 구현

        // 바텀 네비게이션
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // 초기 플래그먼트 설정
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment1).commitAllowingStateLoss();

        // 바텀 네비게이션
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

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
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment3).commitAllowingStateLoss();
                    return true;
                }
                return true;
            }
        });
    }
}