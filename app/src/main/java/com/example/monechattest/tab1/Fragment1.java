package com.example.monechattest.tab1;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.monechattest.R;
import com.example.monechattest.database.AppDatabase;
import com.google.android.material.tabs.TabLayout;

import java.util.Locale;

public class Fragment1 extends Fragment {
    Spinner spinner;
    String[] monthList = {"2024년 1월", "2024년 2월", "2024년 3월", "2024년 4월", "2024년 5월", "2024년 6월", "2024년 7월", "2024년 8월", "2024년 9월", "2024년 10월", "2024년 11월", "2024년 12월"}; // 소비내역 리스트
    int year, month; // TODO: 월 선정을 스피너로해서 이 값도 지정 gpt코드를 위해 내가 만든 변수

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_1, container, false);

        ViewPager viewPager = rootView.findViewById(R.id.viewPager);
        TabLayout tabLayout = rootView.findViewById(R.id.tabLayout);
        TextView totalExpenseText = rootView.findViewById(R.id.totalExpenseText);

        spinner = rootView.findViewById(R.id.spinnerMonth);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, monthList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

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

    // 월별 총합을 가져오는 메소드 gpt (240707)
    public void loadTotalForMonth(Context context, int year, int month) {
        AppDatabase db = AppDatabase.getInstance(context);
        String yearMonth = String.format(Locale.getDefault(), "%04d-%02d", year, month);
        double total = db.getExpenseDao().getTotalForMonth(yearMonth);
        Log.d("총합", ""+total);
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