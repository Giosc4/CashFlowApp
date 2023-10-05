package com.example.cashflow;

import android.content.Context;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class JsonReadWrite {

    private ArrayList<Account> accounts;
    private String fileName;

    public JsonReadWrite(String fileName) {
        this.fileName = fileName;
        this.accounts = new ArrayList<>();
    }

    public JsonReadWrite(ArrayList<Account> accounts, String fileName) {
        this.fileName = fileName;
        this.accounts = accounts;
    }


    public void setList(ArrayList<Account> accounts, Context context) throws IOException {
        this.accounts = accounts;
        saveToJson(context);
    }

    public void saveToJson(Context context) throws IOException {
        // Convert the Transactions object to JSON format
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
        String json = gson.toJson(accounts);

        // Specify the path where you want to create the file
        File directory = context.getExternalFilesDir(null);
        if (directory == null) {
            throw new IOException("Cannot access external files directory");
        }

        directory = new File(directory, "my_data_directory");
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Cannot create directory: " + directory);
        }

        System.out.println("PATH: " + directory);

        // Create the file in the destination folder
        File file = new File(directory, fileName);

        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("File creato con successo.");
                } else {
                    System.out.println("Impossibile creare il file.");
                }
            } catch (IOException e) {
                System.out.println("Errore durante la creazione del file: " + e.getMessage());
            }
        }


        // Write the JSON to the file
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(json);
        }

        System.out.println("Content of the file: " + json);
    }


    public String accountsToJson() {
        Gson gson = new Gson();
        return gson.toJson(accounts);
    }


    // deve sostituire readJsonFromFile
    public ArrayList<Account> readAccountsFromJson(Context context) {
        // Read the JSON string from the file
        String json = null;
        try {
            json = readJsonFromFile(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Deserialize JSON as a list of Account objects
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Account>>() {}.getType();
        ArrayList<Account> loadedAccounts = gson.fromJson(json, type);

        return loadedAccounts;
    }


    public String readJsonFromFile(Context context) throws IOException {
        // Specifying the path of the file
        File directory = new File(context.getExternalFilesDir(null), "my_data_directory");
        File file = new File(directory, fileName);

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