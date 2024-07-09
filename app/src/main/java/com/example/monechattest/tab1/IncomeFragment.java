package com.example.monechattest.tab1;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.monechattest.R;
import com.example.monechattest.database.AppDatabase;
import com.example.monechattest.database.IncomeEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IncomeFragment extends Fragment implements MonthlyFilterable {
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    IncomeAdapter adapter;
    ArrayList<IncomeItem> incomeItems = new ArrayList<>();
    FloatingActionButton fab;
    private ActivityResultLauncher<Intent> addIncomeLauncher;
    private String currentSelectedMonth; // 현재 선택된 월을 저장할 변수

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_income, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new IncomeAdapter(getContext(), incomeItems);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new IncomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(IncomeAdapter.ViewHolder holder, View view, int position) {
                IncomeItem item = adapter.getItem(position);
                Intent intent = new Intent(getActivity(), IncomeDetailActivity.class);
                intent.putExtra("date", item.getDateMillis());
                intent.putExtra("category", item.getCategory());
                intent.putExtra("description", item.getDescription());
                intent.putExtra("amount", item.getAmount());
                intent.putExtra("note", item.getNote());
                startActivity(intent);
            }
        });

        adapter.setOnItemLongClickListener(new IncomeAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(IncomeAdapter.ViewHolder holder, View view, int position) {
                IncomeItem item = adapter.getItem(position);
                showDeleteConfirmationDialog(item);
            }
        });

        fab = rootView.findViewById(R.id.fab_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddIncomeDetailActivity.class);
                addIncomeLauncher.launch(intent);
            }
        });

        addIncomeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        IncomeItem newItem = (IncomeItem) result.getData().getSerializableExtra("incomeItem");
                        if (newItem != null) {
                            new AddIncome(getActivity(), newItem).start();
                        }
                    }
                }
        );

        return rootView;
    }

    @Override
    public void onMonthSelected(String yearMonth) {
        currentSelectedMonth = yearMonth;
        new GetIncome(getActivity(), yearMonth).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (addIncomeLauncher != null) {
            refreshIncomeList();
        }
    }

    private void refreshIncomeList() {
        new GetIncome(getActivity(), currentSelectedMonth).start();
    }

    private void showDeleteConfirmationDialog(IncomeItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("삭제 확인")
                .setMessage(item.getDescription() + " 내역을 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialogInterface, i) -> new DeleteIncome(getActivity(), item.getIdx()).start())
                .setNegativeButton("취소", null)
                .show();
    }

    class AddIncome extends Thread {
        private Context context;
        private IncomeItem item;

        public AddIncome(Context context, IncomeItem item) {
            this.context = context;
            this.item = item;
        }

        @Override
        public void run() {
            IncomeEntity income = new IncomeEntity(item.getDescription(), item.getDate(), item.getCategory(), item.getAmount(), item.getNote());
            long newIdx = AppDatabase.getInstance(context).getIncomeDao().insert(income);
            item.setIdx((int) newIdx);

            getActivity().runOnUiThread(() -> {
                refreshIncomeList();
                if (getParentFragment() instanceof Fragment1) {
                    ((Fragment1) getParentFragment()).updateMonthlyIncome(currentSelectedMonth);
                }
            });
        }
    }

    class GetIncome extends Thread {
        private Context context;
        private String yearMonth;

        public GetIncome(Context context, String yearMonth) {
            this.context = context;
            this.yearMonth = yearMonth;
        }

        @Override
        public void run() {
            List<IncomeEntity> incomes;
            if (yearMonth == null) {
                incomes = AppDatabase.getInstance(context).getIncomeDao().getAllIncome();
            } else {
                incomes = AppDatabase.getInstance(context).getIncomeDao().getIncomesByMonth(yearMonth);
            }

            ArrayList<IncomeItem> items = new ArrayList<>();
            for (IncomeEntity income : incomes) {
                IncomeItem item = new IncomeItem(income.getDescription(), income.getDate(), income.getCategory(), income.getAmount(), income.getNote());
                item.setIdx(income.getIdx());
                items.add(item);
            }

            Collections.sort(items, new Comparator<IncomeItem>() {
                @Override
                public int compare(IncomeItem item1, IncomeItem item2) {
                    return Long.compare(item2.getDateMillis(), item1.getDateMillis());
                }
            });

            getActivity().runOnUiThread(() -> {
                incomeItems.clear();
                incomeItems.addAll(items);
                adapter.notifyDataSetChanged();
            });
        }
    }

    class DeleteIncome extends Thread {
        private Context context;
        private int idx;

        public DeleteIncome(Context context, int idx) {
            this.context = context;
            this.idx = idx;
        }

        @Override
        public void run() {
            AppDatabase.getInstance(context).getIncomeDao().delete(idx);

            getActivity().runOnUiThread(() -> {
                refreshIncomeList();
                if (getParentFragment() instanceof Fragment1) {
                    ((Fragment1) getParentFragment()).updateMonthlyIncome(currentSelectedMonth);
                }
            });
        }
    }
}
