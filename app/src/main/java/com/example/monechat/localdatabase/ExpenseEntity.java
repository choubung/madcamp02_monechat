package com.example.monechat.localdatabase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(tableName = "expense")
public class ExpenseEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @TypeConverters(DateTypeConverter.class)
    public Date date; // 소비 날짜

    public String category; // 카테고리 (주수입 부수입 기타수입)

    public String description; // 상세 내역

    public String amount; // 소비 금액

    public String note; // 메모

    // Getters and setters for each field can be added if needed
}
