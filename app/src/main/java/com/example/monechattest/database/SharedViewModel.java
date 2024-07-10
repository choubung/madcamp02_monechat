package com.example.monechattest.database;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.monechattest.tab1.ExpenseItem;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<ExpenseItem> newExpense = new MutableLiveData<>();

    public void setNewExpense(ExpenseItem expense) {
        newExpense.setValue(expense);
    }

    public LiveData<ExpenseItem> getNewExpense() {
        return newExpense;
    }
}