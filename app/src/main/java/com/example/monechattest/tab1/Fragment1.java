package com.example.monechattest.tab1;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.monechattest.R;
import com.google.android.material.tabs.TabLayout;

public class Fragment1 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_1, container, false);

        ViewPager viewPager = rootView.findViewById(R.id.viewPager);
        TabLayout tabLayout = rootView.findViewById(R.id.tabLayout);

        FragmentAdapter adapter = new FragmentAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);

        // TabLayout을 ViewPager에 연결
        tabLayout.setupWithViewPager(viewPager);

        // 각 탭의 제목 설정 및 커스텀 탭 뷰 적용
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                switch (i) {
                    case 0:
                        tab.setCustomView(createCustomTabView("지출", Gravity.RIGHT));
                        break;
                    case 1:
                        tab.setCustomView(createCustomTabView("수입", Gravity.LEFT));
                        break;
                }
            }
        }

        return rootView;
    }

    // 커스텀 탭 뷰 생성 함수
    private View createCustomTabView(String text, int gravity) {
        TextView tabTextView = new TextView(getContext());
        tabTextView.setText(text);
        tabTextView.setGravity(gravity);
        tabTextView.setTextColor(Color.parseColor("#666666")); // 기본 텍스트 색상
        tabTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        return tabTextView;
    }
}