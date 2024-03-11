package com.example.cashflow.dataClass;

public class Saving {
    private int id;
    private double amount;
    private int accountId;
    private String startDate;
    private String endDate;

    // Constructors, Getters, Setters

    public Saving(int id, double amount, int accountId, String startDate, String endDate) {
        this.id = id;
        this.amount = amount;
        this.accountId = accountId;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
