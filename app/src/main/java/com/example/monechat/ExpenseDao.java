package com.example.monechat;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert
    void insert(ExpenseEntity expense);

    @Update
    void update(ExpenseEntity expense);

    @Delete
    void delete(ExpenseEntity expense);

    @Query("SELECT * FROM expense WHERE id = :id")
    ExpenseEntity getExpenseById(int id);

    @Query("SELECT * FROM expense ORDER BY date DESC")
    List<ExpenseEntity> getAllExpenses();
}