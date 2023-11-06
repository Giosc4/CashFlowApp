package com.example.cashflow.dataClass;

import com.example.cashflow.dataClass.CategoriesEnum;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class Transactions {
    private Boolean income;
    private double amount;
    private Calendar date;
    private City city;
    private CategoriesEnum category;

    public Transactions() {
        this.income = false;
        this.amount = 00.01;
        this.date = Calendar.getInstance();
        this.city = new City();

        this.category = CategoriesEnum.FoodAndDrinks;
    }

    public Transactions(Boolean income, double amount, Calendar date, City city, CategoriesEnum category) {
        this.income = income;
        this.amount = amount;
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
        if (isIncome()) {
            return amount;
        } else {
            return -amount;
        }
    }

    public double getAmountValue() {
        return amount;
    }

    public Calendar getDate() {
        return date;
    }

    public City getCity() {
        return this.city;
    }


    @Override
    public String toString() {
        String cityString = (city != null) ? city.toString() : "Nessuna città";
        return "Transactions{" +
                "isIncome=" + isIncome() +
                ", amount=" + amount +
                ", date=" + date +
                ", city=" + cityString +
                ", category=" + category +
                '}';
    }


    public String printOnApp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String formattedDate = dateFormat.format(date.getTime());

        String cityString = (city != null) ? city.getNameCity() : "Città sconosciuta";

        if (income) {
            return "INCOME " + "Amount: " + amount + "\n" +
                    "Date: " + formattedDate + "\n" +
                    "City: " + cityString + "\n" +
                    "Category: " + category.name();
        } else {
            return "EXPENSE " + "Amount: " + amount + "\n" +
                    "Date: " + formattedDate + "\n" +
                    "City: " + cityString + "\n" +
                    "Category: " + category.name();
        }
    }
}
