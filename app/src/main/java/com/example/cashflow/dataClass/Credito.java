package com.example.cashflow.dataClass;
public class Credito {
    private int id;
    private double amount;
    private String name;
    private String concessionDate;
    private String extinctionDate;
    private int accountId;

    // Constructors, Getters, Setters

    public Credito(int id, double amount, String name, String concessionDate, String extinctionDate, int accountId) {
        this.id = id;
        this.amount = amount;
        this.name = name;
        this.concessionDate = concessionDate;
        this.extinctionDate = extinctionDate;
        this.accountId = accountId;
    }
}

