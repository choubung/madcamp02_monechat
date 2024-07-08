package com.example.monechattest.tab1;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.monechattest.R;
import com.example.monechattest.database.AppDatabase;
import com.example.monechattest.database.ExpenseViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.Locale;

public class Fragment1 extends Fragment {
    Spinner spinner;
    String[] monthList = {"2024년 1월", "2024년 2월", "2024년 3월", "2024년 4월", "2024년 5월", "2024년 6월", "2024년 7월", "2024년 8월", "2024년 9월", "2024년 10월", "2024년 11월", "2024년 12월"}; // 소비내역 리스트
    private ExpenseViewModel expenseViewModel;
    private TextView totalExpenseText;
    FragmentAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_1, container, false);

        ViewPager viewPager = rootView.findViewById(R.id.viewPager);
        TabLayout tabLayout = rootView.findViewById(R.id.tabLayout);
        totalExpenseText = rootView.findViewById(R.id.totalExpenseText);

        // ViewModel 설정
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        spinner = rootView.findViewById(R.id.spinnerMonth);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, monthList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setSelection(6);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    // 스피너에서 선택된 값에 따라 yearMonth 문자열 생성
                    String yearMonth = String.format(Locale.getDefault(), "2024-%02d", i + 1);

                    // 옵저버 등록
                    expenseViewModel.getMonthlyExpenseSum(yearMonth).observe(getViewLifecycleOwner(), new Observer<Double>() {
                        @Override
                        public void onChanged(Double totalAmount) {
                            if (totalAmount != null) {
                                totalExpenseText.setText(String.format(Locale.getDefault(), "%.0f", totalAmount));
                            } else {
                                totalExpenseText.setText("0");
                            }
                        }
                    });

                    // gpt 0708-09
                    // 프래그먼트들에게 월 정보 전달
                    for (Fragment fragment : getChildFragmentManager().getFragments()) {
                        if (fragment instanceof MonthlyFilterable) {
                            ((MonthlyFilterable) fragment).onMonthSelected(yearMonth);
                        }
                    }
                } catch (Exception e) {
                    Log.e("Fragment1", "Error observing monthly expense sum", e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                totalExpenseText.setText("0");
            }
        });

        adapter = new FragmentAdapter(getChildFragmentManager());
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
//    public void loadTotalForMonth(Context context, int year, int month) {
//        AppDatabase db = AppDatabase.getInstance(context);
//        String yearMonth = String.format(Locale.getDefault(), "%04d-%02d", year, month);
//        double total = db.getExpenseDao().getTotalForMonth(yearMonth);
//        Log.d("총합", ""+total);
//    }

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