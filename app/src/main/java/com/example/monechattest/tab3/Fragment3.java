package com.example.monechattest.tab3;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.monechattest.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment3 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_3, container, false);

        // ExpensePieChartFragment를 탭 3 프래그먼트에 추가
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.chart_container, new ExpensePieChartFragment());
        transaction.commit();

        return rootView;
    }
}