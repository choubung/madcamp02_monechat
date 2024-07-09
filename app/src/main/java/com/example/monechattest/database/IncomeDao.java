package com.example.monechattest.database;

import androidx.lifecycle.LiveData;
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
    List<IncomeEntity> getAllIncome(); // 메서드 이름 수정됨

    @Query("SELECT * FROM income WHERE strftime('%Y-%m', datetime(date / 1000, 'unixepoch', 'localtime')) = :yearMonth")
    List<IncomeEntity> getIncomesByMonth(String yearMonth);

    @Query("SELECT SUM(amount) FROM income WHERE date BETWEEN :startOfMonth AND :endOfMonth")
    LiveData<Double> getTotalForMonthLiveData(long startOfMonth, long endOfMonth);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(IncomeEntity income);

    @Update
    void update(IncomeEntity income);

    // 데이터가 존재하는지 확인하는 메서드
    @Query("SELECT COUNT(*) FROM income")
    int getIncomeCount();

    @Query("SELECT strftime('%Y-%m', date) AS month, SUM(CAST(amount AS REAL)) AS total FROM expense GROUP BY month ORDER BY month")
    List<MonthlyIncome> getMonthlyIncomes(); // gpt코드 (240707)

    @Query("SELECT SUM(CAST(amount AS REAL)) AS total FROM income WHERE strftime('%Y-%m', datetime(date / 1000, 'unixepoch')) = :yearMonth")
    double getTotalForMonth(String yearMonth); // gpt코드 (240707)

    @Query("SELECT SUM(CAST(amount AS REAL)) FROM income WHERE strftime('%Y-%m', datetime(date / 1000, 'unixepoch')) = :yearMonth")
    LiveData<Double> getTotalForMonthLiveData(String yearMonth);
}