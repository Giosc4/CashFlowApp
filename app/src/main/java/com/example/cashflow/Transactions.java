package com.example.cashflow;


import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

        public class Transactions{
        private Boolean income;
        private String amount;
        private String  date;
        private String city;

        // AGGIUNGERE CATEGORIA


        public Transactions(){
        this.income=false;
        this.amount="00.01";
        this.date=null;
        this.city=null;
        }

        //income, number,  date,  location
        public Transactions(Boolean income, String amount, String date, String city){
                this.income=income;
                this.amount=amount;
                this.date=date;
                this.city=city;
        }


                @Override
                public String toString() {
                        return "Transactions{" +
                                "income=" + income +
                                ", amount='" + amount + '\'' +
                                ", date='" + date + '\'' +
                                ", city='" + city + '\'' +
                                '}';
                }
        }
