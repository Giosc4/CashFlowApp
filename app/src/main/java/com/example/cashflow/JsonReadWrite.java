package com.example.cashflow;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class JsonReadWrite {

    private ArrayList<Account> accounts;

    public JsonReadWrite(){
        accounts = new ArrayList<>();
    }

    public JsonReadWrite(ArrayList<Account> accounts){
        this.accounts = accounts;
    }


    public void addTransaction(Context context, boolean income, String number, String date, String location, String accountSelected) {
        Transactions newTrans= new  Transactions(income, number, date, location);

        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getName().equals(accountSelected)){
                accounts.get(i).getListTrans().add(newTrans);
                System.out.println("New Transaction: " + newTrans.toString());
            }
        }
        try {
            this.saveToJson(context,"test2122.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void saveToJson(Context context, String filename) throws IOException {
        Gson gson = new Gson();

        // Convert the Transactions object to JSON format
        String json = gson.toJson(this);

        // Specify the path where you want to create the file
        File directory = new File(context.getExternalFilesDir(null), "my_data_directory");
        System.out.println("PATH: " + directory);

        // Create the file in the destination folder
        File file = new File(directory, filename);
        boolean created = file.createNewFile();
        if (created) {
            System.out.println("File successfully created");
        } else {
            System.out.println("Error during file creation");
        }

        // Write the JSON to the file
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(json);
        }
    }
    public String readJsonFromFile(Context context, String filename) throws IOException {
        // Specifying the path of the file
        File directory = new File(context.getExternalFilesDir(null), "my_data_directory");
        File file = new File(directory, filename);

        // Check if file exists
        if (!file.exists()) {
            System.out.println("File not found");
            return null;
        }

        // Read the JSON from the file
        StringBuilder jsonContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
        }

        return jsonContent.toString();
    }

}
