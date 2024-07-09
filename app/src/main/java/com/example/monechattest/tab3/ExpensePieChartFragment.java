package com.example.monechattest.tab3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.example.monechattest.R;
import com.example.monechattest.database.CategoryExpense;
import com.example.monechattest.database.ExpenseViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExpensePieChartFragment extends Fragment {

    private ExpenseViewModel expenseViewModel;
    private AnyChartView anyChartView;
    private Spinner spinner;
    private TextView noDataText;
    private String[] monthList = {"2024년 1월", "2024년 2월", "2024년 3월", "2024년 4월", "2024년 5월", "2024년 6월", "2024년 7월", "2024년 8월", "2024년 9월", "2024년 10월", "2024년 11월", "2024년 12월"};

    public ExpensePieChartFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_pie_chart, container, false);

        anyChartView = view.findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));
        noDataText = view.findViewById(R.id.no_data_text);

        spinner = view.findViewById(R.id.spinnerMonth);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, monthList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(6); // 기본 월을 7월로 설정

        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedMonth = String.format(Locale.getDefault(), "2024-%02d", i + 1);
                expenseViewModel.getCategoryExpenses(selectedMonth).observe(getViewLifecycleOwner(), categoryExpenses -> {
                    if (categoryExpenses != null && !categoryExpenses.isEmpty()) {
                        noDataText.setVisibility(View.GONE);
                        anyChartView.setVisibility(View.VISIBLE);
                        setupPieChart(categoryExpenses);
                    } else {
                        anyChartView.clear();
                        noDataText.setVisibility(View.VISIBLE);
                        anyChartView.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        return view;
    }

    private void setupPieChart(List<CategoryExpense> categoryExpenses) {
        List<DataEntry> dataEntries = new ArrayList<>();
        for (CategoryExpense categoryExpense : categoryExpenses) {
            dataEntries.add(new ValueDataEntry(categoryExpense.getCategory(), categoryExpense.getTotal()));
        }

        Pie pie = AnyChart.pie();
        pie.data(dataEntries);

        pie.title("소비 카테고리별 금액");

        pie.labels().position("outside");

        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("카테고리")
                .padding(0d, 0d, 10d, 0d);

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
            }
        });

        anyChartView.setChart(pie);
    }
}