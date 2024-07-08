package com.example.monechattest.database;
//gpt 코드 (240707)
public class MonthlyExpense {
    public String month;
    public double total;

    // 필요한 경우 생성자, getter, setter 추가
    public MonthlyExpense(String month, double total) {
        this.month = month;
        this.total = total;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
