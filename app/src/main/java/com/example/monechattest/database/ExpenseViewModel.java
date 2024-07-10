package com.example.monechattest.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseViewModel extends AndroidViewModel {
    private final ExpenseDao expenseDao;
    private final ExecutorService executorService;
    private final MutableLiveData<ExpenseEntity> newExpense = new MutableLiveData<>();

    // gpt 0710-15
    public void addExpense(ExpenseEntity expense) {
        executorService.execute(() -> {
            expenseDao.insert(expense);
            newExpense.postValue(expense);
        });
        newExpense.setValue(expense);
    }

    public LiveData<ExpenseEntity> getNewExpense() {
        return newExpense;
    }

    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        expenseDao = db.getExpenseDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<CategoryExpense>> getCategoryExpenses() {
        MutableLiveData<List<CategoryExpense>> categoryExpensesLiveData = new MutableLiveData<>();
        executorService.execute(() -> {
            List<CategoryExpense> categoryExpenses = expenseDao.getCategoryExpenses();
            categoryExpensesLiveData.postValue(categoryExpenses);
        });
        return categoryExpensesLiveData;
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

    public LiveData<List<CategoryExpense>> getCategoryExpenses(String yearMonth) {
        MutableLiveData<List<CategoryExpense>> categoryExpensesLiveData = new MutableLiveData<>();
        executorService.execute(() -> {
            List<CategoryExpense> categoryExpenses = expenseDao.getCategoryExpensesByMonth(yearMonth);
            categoryExpensesLiveData.postValue(categoryExpenses);
        });
        return categoryExpensesLiveData;
    }
}