package com.example.monechattest.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Calendar;

public class ExpenseViewModel extends AndroidViewModel {
    private final ExpenseDao expenseDao;

    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        expenseDao = db.getExpenseDao();
    }

    public LiveData<Double> getMonthlyExpenseSum(String yearMonth) {
        // yearMonth를 파싱하여 startOfMonth와 endOfMonth를 계산
        String[] parts = yearMonth.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]) - 1; // Calendar의 month는 0-based

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startOfMonth = calendar.getTimeInMillis();

        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        long endOfMonth = calendar.getTimeInMillis();

        return expenseDao.getTotalForMonthLiveData(startOfMonth, endOfMonth);
    }
}