package com.example.monechattest.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface IncomeDao {
    @Query("DELETE FROM income WHERE idx = :idx")
    void delete(int idx);

    @Query("SELECT * FROM income")
    List<IncomeEntity> getAllIncome();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(IncomeEntity income);

    @Update
    void update(IncomeEntity income);

    // 데이터가 존재하는지 확인하는 메서드
    @Query("SELECT COUNT(*) FROM income")
    int getIncomeCount();
}