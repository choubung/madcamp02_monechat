package com.example.monechattest.tab1;

import java.io.Serializable;
import java.util.Date;

public class IncomeItem implements Serializable {
    private int idx;
    private Date date;
    private String category, description, amount, note;

    public IncomeItem(int idx, Date date, String category, String description, String amount, String note) {
        this.idx = idx;
        this.date = date;
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.note = note;
    }

    // getter and setter

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
