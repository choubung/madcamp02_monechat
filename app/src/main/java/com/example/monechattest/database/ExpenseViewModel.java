package com.example.monechattest.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ExpenseViewModel extends AndroidViewModel {
    private final ExpenseDao expenseDao;

    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        expenseDao = db.getExpenseDao();
    }

    public LiveData<Double> getMonthlyExpenseSum(String yearMonth) {
        return expenseDao.getTotalForMonthLiveData(yearMonth);
    }
}