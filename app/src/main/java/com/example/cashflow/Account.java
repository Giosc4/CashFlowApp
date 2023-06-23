package com.example.cashflow;


import java.util.ArrayList;

        public class Account{

        private String name;
        private double balance;

        private ArrayList<Transactions>listTrans;

        public Account(){
        this.name="Accountempty";
        this.balance=0.0;
        listTrans=new ArrayList<>();
        }

        public Account(String name,double balance){
        this.name=name;
        this.balance=balance;
        }

        public String getName(){
        return name;
        }

        public void setName(String name){
        this.name=name;
        }

        public double getBalance(){
        return balance;
        }

        public void setBalance(double balance){
        this.balance=balance;
        }

        public ArrayList<Transactions>getListTrans(){
        return listTrans;
        }

        public void setListTrans(ArrayList<Transactions>listTrans){
        this.listTrans=listTrans;
        }


        }
