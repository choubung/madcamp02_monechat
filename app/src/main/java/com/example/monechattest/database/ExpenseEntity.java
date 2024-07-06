package com.example.monechattest.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(tableName = "expense")
public class ExpenseEntity {
    @PrimaryKey(autoGenerate = true)
    public int idx;

    @TypeConverters(DateTypeConverter.class)
    public Date date; // 소비 날짜
    public String category; // 카테고리 (주수입 부수입 기타수입)
    public String description; // 상세 내역
    public String amount; // 소비 금액
    public boolean isSmartExpense; // 현명소비
    public String note; // 메모

    // 생성자 - 현명 소비 여부는 리사이클러 뷰에서 결정하므로 여기서는 설정하지 않음
    public ExpenseEntity(String description, Date date, String category, String amount, String note) {
        this.description = description;
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.note = note;
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
