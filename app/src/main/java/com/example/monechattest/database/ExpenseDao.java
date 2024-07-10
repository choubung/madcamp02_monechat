package com.example.monechattest.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExpenseDao {
    @Query("DELETE FROM expense WHERE idx = :idx")
    void delete(int idx);

    @Query("SELECT * FROM expense")
    List<ExpenseEntity> getAllExpense(); // 메서드 이름 수정됨

    @Query("SELECT * FROM expense WHERE strftime('%Y-%m', datetime(date / 1000, 'unixepoch', 'localtime')) = :yearMonth")
    List<ExpenseEntity> getExpensesByMonth(String yearMonth);

    @Query("SELECT SUM(amount) FROM expense WHERE date BETWEEN :startOfMonth AND :endOfMonth")
    LiveData<Double> getTotalForMonthLiveData(long startOfMonth, long endOfMonth);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(ExpenseEntity expense);

    @Update
    void update(ExpenseEntity expense);

    // 데이터가 존재하는지 확인하는 메서드
    @Query("SELECT COUNT(*) FROM expense")
    int getExpenseCount();

    @Query("SELECT strftime('%Y-%m', date) AS month, SUM(CAST(amount AS REAL)) AS total FROM expense GROUP BY month ORDER BY month")
    List<MonthlyExpense> getMonthlyExpenses(); // gpt코드 (240707)

    @Query("SELECT SUM(CAST(amount AS REAL)) AS total FROM expense WHERE strftime('%Y-%m', datetime(date / 1000, 'unixepoch')) = :yearMonth")
    double getTotalForMonth(String yearMonth); // gpt코드 (240707)

    @Query("SELECT SUM(CAST(amount AS REAL)) FROM expense WHERE strftime('%Y-%m', datetime(date / 1000, 'unixepoch')) = :yearMonth")
    LiveData<Double> getTotalForMonthLiveData(String yearMonth);

    // 카테고리별 소비 금액 합계를 가져오는 메서드 추가
    @Query("SELECT category, SUM(CAST(amount AS REAL)) AS total FROM expense GROUP BY category")
    List<CategoryExpense> getCategoryExpenses(); // gpt코드

    @Query("SELECT category, SUM(CAST(amount AS REAL)) AS total FROM expense WHERE strftime('%Y-%m', datetime(date / 1000, 'unixepoch', 'localtime')) = :yearMonth GROUP BY category")
    List<CategoryExpense> getCategoryExpensesByMonth(String yearMonth);
}
