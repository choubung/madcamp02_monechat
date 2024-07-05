package com.example.monechat.localdatabase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(tableName = "income")
public class IncomeEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @TypeConverters(DateTypeConverter.class)
    public Date date;

    public String source;

    public String description;

    public String amount;

    public String note;
}