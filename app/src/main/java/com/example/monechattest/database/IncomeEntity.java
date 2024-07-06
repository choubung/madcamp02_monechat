package com.example.monechattest.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(tableName = "income")
public class IncomeEntity {
    @PrimaryKey(autoGenerate = true)
    public int idx;

    @TypeConverters(DateTypeConverter.class)
    public Date date;
    public String category; // 카테고리
    public String description; // 상세 내역
    public String amount; // 수입 금액
    public String note; // 메모

    public IncomeEntity(String description, Date date, String category, String amount, String note) {
        this.description = description;
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.note = note;
    }

    // getter and setter
    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}