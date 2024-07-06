package com.example.monechattest.tab1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.monechattest.R;
import com.example.monechattest.database.AppDatabase;
import com.example.monechattest.database.ExpenseEntity;
import com.example.monechattest.database.IncomeEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class IncomeFragment extends Fragment {
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ExpenseAdapter adapter;
    ArrayList<IncomeItem> incomeItems = new ArrayList<>();
    FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_income, container, false);

        return rootView;
    }
}