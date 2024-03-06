package com.example.cashflow;

import com.example.cashflow.dataClass.Account;
import com.example.cashflow.dataClass.CategoriesEnum;
import com.example.cashflow.dataClass.City;
import com.example.cashflow.dataClass.Transactions;

import java.util.ArrayList;
import java.util.Calendar;

public class Test {
    ArrayList<Account> accounts;

    public Test() {
        this.accounts = new ArrayList<Account>();
        ArrayList<Transactions> listTrans = new ArrayList<Transactions>();
        Transactions trans = new Transactions(true, 1000.00, Calendar.getInstance(), new City(), CategoriesEnum.Salary);
        listTrans.add(trans);
        this.accounts.add(new Account("Bank", listTrans));
        this.accounts.add(new Account("Cash", listTrans));
        for (int i = 0; i < accounts.size(); i++) {
            System.out.println("Test: " + accounts.get(i).toString());
        }

    }

    public ArrayList<Account> getList() {
        return this.accounts;
    }
}
