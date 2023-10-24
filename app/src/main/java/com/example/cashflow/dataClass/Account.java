package com.example.cashflow.dataClass;


import java.text.DecimalFormat;
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
        DecimalFormat decimalFormat = new DecimalFormat("#.##"); // Imposta il formato a due decimali
        String formattedBalance = decimalFormat.format(balance);
        return Double.parseDouble(formattedBalance);
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

    public boolean addTransaction(Transactions transaction) {
        boolean response = listTrans.add(transaction);
        if (response) {
            updateBalance();
        }
        return response;
    }
    public boolean removeTransaction(Transactions transaction) {
        boolean response = listTrans.remove(transaction);
        if (response) {
            updateBalance();
        }
        return response;
    }

    public boolean editTransaction(Transactions transactionOriginal, Transactions newTrans) {
        int index = listTrans.indexOf(transactionOriginal);
        if (index >= 0) {
            listTrans.set(index, newTrans);
            updateBalance();
            return true;
        }
        return false;
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
