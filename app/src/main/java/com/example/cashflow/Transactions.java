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
                public void saveToJson(Context context, String filename) throws IOException {
                        Gson gson = new Gson();

                        // Converti l'oggetto Transactions in formato JSON
                        String json = gson.toJson(this);

                        //Checking the availability state of the External Storage.
                        String state = Environment.getExternalStorageState();
                        if (!Environment.MEDIA_MOUNTED.equals(state)) {
                                //If it isn't mounted - we can't write into it.
                                return;
                        }

                        // Specifica il percorso in cui desideri creare il file
                        File directory = new File(context.getExternalFilesDir(null), "my_data_directory");
                        System.out.println("PATH: " + directory);

                        // Creazione del file nella cartella di destinazione
                        File file = new File(directory, filename);
                        boolean created = file.createNewFile();
                        if (created) {
                                System.out.println("File creato con successo");
                        } else {
                                System.out.println("Errore durante la creazione del file");
                        }

                        // Write the JSON to the file
                        try (FileWriter writer = new FileWriter(file)) {
                                writer.write(json);
                        }
                }

        }
