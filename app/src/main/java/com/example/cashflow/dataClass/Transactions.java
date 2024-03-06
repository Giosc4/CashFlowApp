package com.example.cashflow.dataClass;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class Transactions {
    private int id;
    private boolean income; // True for income, false for expense
    private double amount;
    private Calendar date;
    private int cityId;
    private int categoryId;
    private int accountId;

    public Transactions(boolean income, double amount, Calendar date, int cityId, int categoryId, int accountId) {
        this.income = income;
        this.amount = amount;
        this.date = date;
        this.cityId = cityId;
        this.categoryId = categoryId;
        this.accountId = accountId;
    }

    public Transactions(int id, boolean income, double amount, Calendar date, int cityId, int categoryId, int accountId) {
        this.id = id;
        this.income = income;
        this.amount = amount;
        this.date = date;
        this.cityId = cityId;
        this.categoryId = categoryId;
        this.accountId = accountId;
    }

    // Getter and Setter Methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIncome() {
        return income;
    }

    public void setIncome(boolean income) {
        this.income = income;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return "Transactions{" +
                "id=" + id +
                ", income=" + income +
                ", amount=" + amount +
                ", date=" + dateFormat.format(date.getTime()) +
                ", cityId=" + cityId +
                ", categoryId=" + categoryId +
                ", accountId=" + accountId +
                '}';
    }

    public String printOnApp(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return "Transactions{" +
                "id=" + id +
                ", income=" + (income ? "Income" : "Expense") +
                ", amount=" + amount +
                ", date=" + dateFormat.format(date.getTime()) +
                ", cityId=" + cityId +
                ", categoryId=" + categoryId +
                ", accountId=" + accountId +
                '}';
    }

    public double getAmountValue() {
        return amount;
    }
}
