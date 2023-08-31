package com.example.cashflow;


import java.util.ArrayList;

public class Account {

    private String name;
    private double balance;
    private ArrayList<Transactions> listTrans;

    public Account() {
        this.name = "Accountempty";
        listTrans = new ArrayList<>();
        updateBalance();
    }

    public Account(String name) {
        this.name = name;
        listTrans = new ArrayList<>();
        updateBalance();
    }

    public Account(String name, ArrayList<Transactions> listTrans) {
        this.name = name;
        this.listTrans = listTrans;
        updateBalance();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        updateBalance();
        return balance;
    }

    public ArrayList<Transactions> getListTrans() {
        return listTrans;
    }

    public void updateBalance() {
        double total = 0;
        if (listTrans != null) {
            for (Transactions trans : listTrans) {
                total = total + trans.getAmount();
            }
        }
        this.balance = total;
    }
    public boolean removeTransaction(Transactions transaction) {
        boolean response = listTrans.remove(transaction);
        updateBalance();
        return response;
    }

    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", balance=" + balance +
                ", listTrans=" + listTrans +
                '}';
    }
}
