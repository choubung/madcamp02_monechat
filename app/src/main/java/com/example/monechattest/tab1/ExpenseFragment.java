package com.example.monechattest.tab1;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.monechattest.R;
import com.example.monechattest.database.AppDatabase;
import com.example.monechattest.database.ExpenseEntity;
import com.example.monechattest.database.SharedViewModel;
import com.example.monechattest.tab2.Fragment2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExpenseFragment extends Fragment implements MonthlyFilterable {
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ExpenseAdapter adapter;
    ArrayList<ExpenseItem> expenseItems = new ArrayList<>();
    FloatingActionButton fab;
    private ActivityResultLauncher<Intent> addExpenseLauncher;
    String currentSelectedMonth; // 현재 선택된 월을 저장할 변수

    private SharedViewModel sharedViewModel; // SharedViewModel 추가

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_expense, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ExpenseAdapter(getContext(), expenseItems);
        recyclerView.setAdapter(adapter);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getNewExpense().observe(getViewLifecycleOwner(), newExpense -> {
            if (newExpense != null) {
                adapter.addItem(newExpense);
                Log.d("ExpenseFragment", "New expense added: " + newExpense.getDescription());
            }
        });

        adapter.setOnItemClickListener(new ExpenseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ExpenseAdapter.ViewHolder holder, View view, int position) {
                ExpenseItem item = adapter.getItem(position);
                Intent intent = new Intent(getActivity(), ExpenseDetailActivity.class);
                intent.putExtra("date", item.getDateMillis());
                intent.putExtra("category", item.getCategory());
                intent.putExtra("description", item.getDescription());
                intent.putExtra("amount", item.getAmount());
                intent.putExtra("note", item.getNote());
                startActivity(intent);
            }
        });

        adapter.setOnItemLongClickListener(new ExpenseAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(ExpenseAdapter.ViewHolder holder, View view, int position) {
                ExpenseItem item = adapter.getItem(position);
                showDeleteConfirmationDialog(item);
            }
        });

        fab = rootView.findViewById(R.id.fab_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddExpenseDetailActivity.class);
                addExpenseLauncher.launch(intent);
            }
        });

        // ActivityResultLauncher 초기화
        addExpenseLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ExpenseItem newItem = (ExpenseItem) result.getData().getSerializableExtra("expenseItem");
                        if (newItem != null) {
                            new AddExpense(getActivity(), newItem).start();
                        }
                    }
                }
        );

        return rootView;
    }

    @Override
    public void onMonthSelected(String yearMonth) {
        currentSelectedMonth = yearMonth;
        new GetExpense(getActivity(), yearMonth).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (addExpenseLauncher != null) {
            refreshExpenseList();
        }
    }

    private void refreshExpenseList() {
        if (currentSelectedMonth != null) {
            new GetExpense(getActivity(), currentSelectedMonth).start();
        } else {
            new GetExpense(getActivity(), null).start();
        }
    }

    private void showDeleteConfirmationDialog(ExpenseItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("삭제 확인")
                .setMessage(item.getDescription() + " 내역을 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialogInterface, i) -> new DeleteExpense(getActivity(), item.getIdx()).start())
                .setNegativeButton("취소", null)
                .show();
    }

    class AddExpense extends Thread {
        private Context context;
        ExpenseItem item;

        public AddExpense(Context context, ExpenseItem item) {
            this.context = context;
            this.item = item;
        }

        @Override
        public void run() {
            ExpenseEntity expense = new ExpenseEntity(item.getDescription(), item.getDate(), item.getCategory(), item.getAmount(), item.getNote());
            long newIdx = AppDatabase.getInstance(context).getExpenseDao().insert(expense);
            item.setIdx((int) newIdx);
            Log.d("AddExpense", "Expense added to database with id: " + newIdx);

            getActivity().runOnUiThread(() -> {
                sharedViewModel.setNewExpense(item);
                refreshExpenseList();
            });
        }
    }

    class GetExpense extends Thread {
        private Context context;
        private String yearMonth;

        public GetExpense(Context context, String yearMonth) {
            this.context = context;
            this.yearMonth = yearMonth;
        }

        @Override
        public void run() {
            List<ExpenseEntity> expenses;
            if (yearMonth == null) {
                expenses = AppDatabase.getInstance(context).getExpenseDao().getAllExpense();
            } else {
                expenses = AppDatabase.getInstance(context).getExpenseDao().getExpensesByMonth(yearMonth);
            }

            ArrayList<ExpenseItem> items = new ArrayList<>();
            for (ExpenseEntity expense : expenses) {
                ExpenseItem item = new ExpenseItem(expense.getDescription(), expense.getDate(), expense.getCategory(), expense.getAmount(), expense.getNote());
                item.setIdx(expense.getIdx());
                items.add(item);
                Log.d("GetExpense", "Expense retrieved from database: " + expense.getDescription());
            }

            Collections.sort(items, new Comparator<ExpenseItem>() {
                @Override
                public int compare(ExpenseItem item1, ExpenseItem item2) {
                    return Long.compare(item2.getDateMillis(), item1.getDateMillis());
                }
            });

            getActivity().runOnUiThread(() -> {
                expenseItems.clear();
                expenseItems.addAll(items);
                adapter.notifyDataSetChanged();
                Log.d("GetExpense", "RecyclerView updated with new data");
            });
        }
    }

    class DeleteExpense extends Thread {
        private Context context;
        private int idx;

        public DeleteExpense(Context context, int idx) {
            this.context = context;
            this.idx = idx;
        }

        @Override
        public void run() {
            AppDatabase.getInstance(context).getExpenseDao().delete(idx);
            Log.d("DeleteExpense", "Expense deleted from database with id: " + idx);

            getActivity().runOnUiThread(() -> {
                refreshExpenseList();
            });
        }
    }
}