package com.example.monechattest.tab1;

import java.io.Serializable;
import java.util.Date;

public class ExpenseItem implements Serializable {
    private int idx;
    private Date date;
    private String category, description, amount, note;
    private boolean isSmartExpense;

    public ExpenseItem(int idx, Date date, String category, String description, String amount, boolean isSmartExpense) {
        this.idx = idx;
        this.date = date;
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.isSmartExpense = isSmartExpense;
    }

    public ExpenseItem(Date date, String category, String description, String amount, boolean isSmartExpense, String note) {
        this.date = date;
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.isSmartExpense = isSmartExpense;
        this.note = note;
    }

    public ExpenseItem(int id, String description, Date date, String category, String amount, boolean isSmartExpense) {
        this.idx = id;
        this.description = description;
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.isSmartExpense = isSmartExpense;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public boolean isSmartExpense() {
        return isSmartExpense;
    }

    public void setSmartExpense(boolean smartExpense) {
        isSmartExpense = smartExpense;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}