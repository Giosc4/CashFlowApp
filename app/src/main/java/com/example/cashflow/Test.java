package com.example.cashflow;

import java.util.ArrayList;

public class Test {
    ArrayList<Account> accounts ;

public  Test(){
    this.accounts = new ArrayList<Account>();
    ArrayList<Transactions> listTrans = new ArrayList<Transactions>();
    listTrans.add(new Transactions());
    this.accounts.add(new Account("account 1", 44.00, listTrans ));
    this.accounts.add(new Account("account 2", 45.00, listTrans ));


}

    public ArrayList<Account> getList() {
    return this.accounts;
    }
}
