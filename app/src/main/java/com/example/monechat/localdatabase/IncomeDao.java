package com.example.monechat.localdatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface IncomeDao {
    @Insert
    void insert(IncomeEntity income);

    @Update
    void update(IncomeEntity income);

    @Delete
    void delete(IncomeEntity income);

    @Query("SELECT * FROM income WHERE id = :id")
    IncomeEntity getIncomeById(int id);

    @Query("SELECT * FROM income ORDER BY date DESC")
    List<IncomeEntity> getAllIncomes();
}