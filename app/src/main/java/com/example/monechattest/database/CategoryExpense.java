package com.example.monechattest.database;

public class CategoryExpense {
    public String category;
    public double total;

    public CategoryExpense(String category, double total) {
        this.category = category;
        this.total = total;
    }

    public String getCategory() {
        return category;
    }

    public double getTotal() {
        return total;
    }
}