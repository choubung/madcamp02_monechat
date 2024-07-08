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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.monechattest.R;
import com.example.monechattest.database.AppDatabase;
import com.example.monechattest.database.ExpenseEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExpenseFragment extends Fragment implements MonthlyFilterable{
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ExpenseAdapter adapter;
    ArrayList<ExpenseItem> expenseItems = new ArrayList<>();
    FloatingActionButton fab;
    private ActivityResultLauncher<Intent> addExpenseLauncher;
    String currentSelectedMonth; // 현재 선택된 월을 저장할 변수

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_expense, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ExpenseAdapter(getContext(), expenseItems);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ExpenseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ExpenseAdapter.ViewHolder holder, View view, int position) {
                // gpt 0708-13
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

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddExpenseDetailActivity.class);
                // startActivity(intent); // 기존 코드
                addExpenseLauncher.launch(intent); // gpt코드
            }
        });

        // ActivityResultLauncher 초기화 / 참고
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
        // 전달받은 월별로 필터링된 데이터를 가져오는 메소드 실행
        currentSelectedMonth = yearMonth; // 현재 선택된 월 업데이트
        new GetExpense(getActivity(), yearMonth).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (addExpenseLauncher != null) { // addExpenseLauncher가 초기화된 경우에만 호출
            refreshExpenseList(); // gpt로 if문 추가 0707
        }
    }

    private void refreshExpenseList() {
        if (currentSelectedMonth != null) {
            new GetExpense(getActivity(), currentSelectedMonth).start();
        } else {
            new GetExpense(getActivity(), null).start();
        }
    }


    // 삭제 확인 다이얼로그 표시
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
            long newIdx = AppDatabase.getInstance(context).getExpenseDao().insert(expense); // 새로 만든 entity contact를 넣기
            item.setIdx((int) newIdx);

            // UI 갱신 / 기존코드
//            getActivity().runOnUiThread(() -> {
//                adapter.addItem(item);
//            });

            //gpt코드 0707
            //getActivity().runOnUiThread(() -> {
             //   ((Fragment1)getParentFragment()).updateMonthlyExpense(currentSelectedMonth);
            //});

            // UI 갱신
            getActivity().runOnUiThread(() -> {
                refreshExpenseList(); // 새로운 데이터가 추가되었으므로, 리스트를 새로고침
                if (getParentFragment() instanceof Fragment1) {
                    ((Fragment1) getParentFragment()).updateMonthlyExpense(currentSelectedMonth);
                }
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
            // 0708-09 주석처리
            // List<ExpenseEntity> entities = AppDatabase.getInstance(context).getExpenseDao().getAllExpense();

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
            }

            // 정렬 후 UI 갱신
            Collections.sort(items, new Comparator<ExpenseItem>() {
                @Override
                public int compare(ExpenseItem item1, ExpenseItem item2) {
                    return Long.compare(item2.getDateMillis(), item1.getDateMillis()); // 최신 순으로 정렬
                }
            });

            getActivity().runOnUiThread(() -> {
                expenseItems.clear();
                expenseItems.addAll(items);
                adapter.notifyDataSetChanged();
            });
            // 주석처리 0708-09
//            expenseItems.clear();
//            for (ExpenseEntity entity : entities) {
//                ExpenseItem item = new ExpenseItem(entity.getIdx(), entity.getDate(), entity.getCategory(), entity.getDescription(), entity.getAmount(), entity.getNote(), entity.isSmartExpense());
//                expenseItems.add(item);
//            }
//            getActivity().runOnUiThread(() -> {
//                adapter.setItems(expenseItems); //gpt 코드
//                adapter.notifyDataSetChanged();
//            });
        }
    }

    class DeleteExpense extends Thread {
        private Context context;
        private int idx; // 삭제할 연락처의 인덱스

        public DeleteExpense(Context context, int idx) {
            this.context = context;
            this.idx = idx;
        }

        @Override
        public void run() {
            AppDatabase.getInstance(context).getExpenseDao().delete(idx);

            // UI 갱신
            /*getActivity().runOnUiThread(() -> {
                //0708-09 주석처리
//                try {
//                    for (int i = 0; i < expenseItems.size(); i++) {
//                        if (expenseItems.get(i).getIdx() == idx) {
//                            expenseItems.remove(i);
//                            // 기존 코드 adapter.removeItem(i); // 어댑터에 삭제 알림/어댑터가 관리
//                            adapter.notifyItemRemoved(i); // notifyDataSetChanged 대신 notifyItemRemoved 사용
//                            break;
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.e("DeleteExpense", "아이템 삭제 중 문제 발생");
//                }
                ((Fragment1)getParentFragment()).updateMonthlyExpense(currentSelectedMonth);
            });
             */

            // UI 갱신
            getActivity().runOnUiThread(() -> {
                refreshExpenseList(); // 데이터가 삭제되었으므로, 리스트를 새로고침
                if (getParentFragment() instanceof Fragment1) {
                    ((Fragment1) getParentFragment()).updateMonthlyExpense(currentSelectedMonth);
                }
            });
        }
    }
}