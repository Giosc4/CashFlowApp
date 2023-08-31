package com.example.cashflow;


import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class Transactions {
    private Boolean income;
    private double amount;
    private String date;
    private String city;
    private CategoriesEnum category;

    public Transactions() {
        this.income = false;
        this.amount = 00.01;
        this.date = null;
        this.city = null;
        this.category = CategoriesEnum.FoodAndDrinks;
    }

    public Transactions(Boolean income, double amount, String date, String city, CategoriesEnum category) {
        this.income = income;
        if (income == false) {
            this.amount = -amount;
        } else {
            this.amount = amount;
        }
        this.date = date;
        this.city = city;
        this.category = category;
    }

    public CategoriesEnum getCategory() {
        return category;
    }

    public Boolean isIncome() {
        return income;
    }

    public double getAmount() {
        return  amount;
    }

    public String getDate() {
        return date;
    }

    public String getCity() {
        return city;
    }

    @Override
    public String toString() {
        return "Transactions{" +
                "income=" + income +
                ", amount=" + amount +
                ", date='" + date + '\'' +
                ", city='" + city + '\'' +
                ", category=" + category +
                '}';
    }

    public String printOnApp() {
        return  "amount: " + amount + " / " +
                "date: " + date + " / " +
                "city: " + city + " / " +
                "category: " + category.name();
    }

}
