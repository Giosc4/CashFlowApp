package com.example.cashflow.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.cashflow.dataClass.Account;
import com.example.cashflow.dataClass.Category;
import com.example.cashflow.dataClass.City;
import com.example.cashflow.dataClass.Transactions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class readSQL {

    // Table Names
    private static final String TABLE_ACCOUNT = "Account";
    private static final String TABLE_CITY = "City";
    private static final String TABLE_TRANSACTIONS = "Transactions";
    private static final String TABLE_CATEGORY = "Category";
    private static final String TABLE_SAVING = "Risparmio";
    private static final String TABLE_BUDGET = "Budget";
    private static final String TABLE_PLANNING = "Pianificazione";
    private static final String TABLE_TEMPLATE_TRANSACTIONS = "Template_Transazioni";
    private static final String TABLE_DEBITO = "Debito";
    private static final String TABLE_CREDITO = "Credito";

    // Common column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";

    // Account Table - column names
    private static final String COLUMN_BALANCE = "balance";

    // City Table - column names
    private static final String COLUMN_CITY_NAME = "city_name";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";

    // Transactions Table - column names
    private static final String COLUMN_INCOME = "income";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_CITY_ID = "city_id";
    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_ACCOUNT_ID = "account_id";

    // Category Table - column names
    private static final String COLUMN_DESCRIPTION = "description";

    // Saving Table - column names
    private static final String COLUMN_START_DATE = "Data_Inizio";
    private static final String COLUMN_END_DATE = "Data_Fine";

    // Budget Table - column names
    // Note: Uses COLUMN_AMOUNT, COLUMN_NAME from Transactions

    // Planning Table - column names
    private static final String COLUMN_TEMPLATE_ID = "Template_ID";
    private static final String COLUMN_REPETITION = "Ripetizione";

    // Debito and Credito Tables - column names
    private static final String COLUMN_CONCESSION_DATE = "Data_Concessione";
    private static final String COLUMN_EXTINCTION_DATE = "Data_Estinsione";


    private SQLiteDatabase db;

    public readSQL(SQLiteDatabase db) {
        this.db = db;
    }


    public String getCityNameById(int cityId) {
        String cityName = null;
        Cursor cursor = db.query("Cities", // Nome della tabella
                new String[]{"Name"}, // Colonna da restituire
                "ID = ?", // Clausola WHERE
                new String[]{String.valueOf(cityId)}, // Valori per la clausola WHERE
                null, null, null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("Name");
            if (columnIndex != -1) {
                cityName = cursor.getString(columnIndex);
            } else {
                // Handle the case where the column doesn't exist in the cursor
            }
        }
        cursor.close();
        db.close();
        return cityName;
    }


    public String getCategoryNameById(int categoryId) {
        String categoryName = null;
        Cursor cursor = db.query("Categories", // Nome della tabella
                new String[]{"Name"}, // Colonna da restituire
                "ID = ?", // Clausola WHERE
                new String[]{String.valueOf(categoryId)}, // Valori per la clausola WHERE
                null, null, null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("Name");
            if (columnIndex != -1) {
                categoryName = cursor.getString(columnIndex);
            } else {
                // Handle the case where the column doesn't exist in the cursor
            }
        }
        cursor.close();
        db.close();
        return categoryName;
    }


    public String[] getCategories() {

        Cursor cursor = db.query("Categories", new String[]{"Name"}, null, null, null, null, null);
        String[] categories = new String[cursor.getCount()];
        int i = 0;
        while (cursor.moveToNext()) {
            categories[i] = cursor.getString(0);
            i++;
        }
        cursor.close();
        db.close();
        return categories;
    }

    public ArrayList<Account> getAllAccounts() {
        ArrayList<Account> accounts = new ArrayList<>();
        if (db == null) {
            Log.e("DatabaseError", "Il database non è stato inizializzato.");
            return accounts; // Ritorna la lista vuota o gestisci l'errore come preferisci
        }
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ACCOUNT, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                int balanceIndex = cursor.getColumnIndex(COLUMN_BALANCE);

                if (idIndex != -1 && nameIndex != -1 && balanceIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    double balance = cursor.getDouble(balanceIndex);
                    Account account = new Account(id, name, balance);
                    accounts.add(account);
                } else {
                    // Gestisci il caso in cui le colonne non esistono nel cursore
                }
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return accounts;
    }


    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CATEGORY, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);

                if (idIndex != -1 && nameIndex != -1 && descriptionIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    String description = cursor.getString(descriptionIndex);
                    Category category = new Category(id, name, description);
                    categories.add(category);
                } else {
                    // Handle the case where the columns don't exist in the cursor
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    public List<City> getAllCities() {
        List<City> cities = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CITY, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int cityNameIndex = cursor.getColumnIndex(COLUMN_CITY_NAME);
                int latitudeIndex = cursor.getColumnIndex(COLUMN_LATITUDE);
                int longitudeIndex = cursor.getColumnIndex(COLUMN_LONGITUDE);

                if (idIndex != -1 && cityNameIndex != -1 && latitudeIndex != -1 && longitudeIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String cityName = cursor.getString(cityNameIndex);
                    double latitude = cursor.getDouble(latitudeIndex);
                    double longitude = cursor.getDouble(longitudeIndex);
                    City city = new City(id, cityName, latitude, longitude);
                    cities.add(city);
                } else {
                    // Handle the case where the columns don't exist in the cursor
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cities;
    }


    public ArrayList<Transactions> getAllTransactions() throws ParseException {
        ArrayList<Transactions> transactions = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TRANSACTIONS, null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int incomeIndex = cursor.getColumnIndex(COLUMN_INCOME);
                int amountIndex = cursor.getColumnIndex(COLUMN_AMOUNT);
                int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
                int cityIdIndex = cursor.getColumnIndex(COLUMN_CITY_ID);
                int categoryIdIndex = cursor.getColumnIndex(COLUMN_CATEGORY_ID);
                int accountIdIndex = cursor.getColumnIndex(COLUMN_ACCOUNT_ID);

                if (idIndex != -1 && incomeIndex != -1 && amountIndex != -1 && dateIndex != -1 && cityIdIndex != -1 && categoryIdIndex != -1 && accountIdIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    int income = cursor.getInt(incomeIndex);
                    double amount = cursor.getDouble(amountIndex);
                    String dateString = cursor.getString(dateIndex);
                    Date date = sdf.parse(dateString);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int cityId = cursor.getInt(cityIdIndex);
                    int categoryId = cursor.getInt(categoryIdIndex);
                    int accountId = cursor.getInt(accountIdIndex);
                    Transactions transaction = new Transactions(id, income > 0, amount, calendar, cityId, categoryId, accountId);
                    transactions.add(transaction);
                } else {
                    // Handle the case where the columns don't exist in the cursor
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return transactions;
    }

    public City getCityById(int cityId) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CITY + " WHERE " + COLUMN_ID + " = " + cityId, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int cityNameIndex = cursor.getColumnIndex(COLUMN_CITY_NAME);
            int latitudeIndex = cursor.getColumnIndex(COLUMN_LATITUDE);
            int longitudeIndex = cursor.getColumnIndex(COLUMN_LONGITUDE);

            if (idIndex != -1 && cityNameIndex != -1 && latitudeIndex != -1 && longitudeIndex != -1) {
                int id = cursor.getInt(idIndex);
                String cityName = cursor.getString(cityNameIndex);
                double latitude = cursor.getDouble(latitudeIndex);
                double longitude = cursor.getDouble(longitudeIndex);
                return new City(id, cityName, latitude, longitude);
            } else {
                // Handle the case where the columns don't exist in the cursor
            }
        }
        cursor.close();
        return null;
    }

    public City getCityByName(String newCityName) {
        // Usa i placeholder '?' per evitare SQL Injection
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CITY + " WHERE " + COLUMN_CITY_NAME + " = ?", new String[]{newCityName});
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int cityNameIndex = cursor.getColumnIndex(COLUMN_CITY_NAME);
            int latitudeIndex = cursor.getColumnIndex(COLUMN_LATITUDE);
            int longitudeIndex = cursor.getColumnIndex(COLUMN_LONGITUDE);

            if (idIndex != -1 && cityNameIndex != -1 && latitudeIndex != -1 && longitudeIndex != -1) {
                int id = cursor.getInt(idIndex);
                String cityName = cursor.getString(cityNameIndex);
                double latitude = cursor.getDouble(latitudeIndex);
                double longitude = cursor.getDouble(longitudeIndex);
                cursor.close(); // Chiudi il cursor prima del return
                return new City(id, cityName, latitude, longitude);
            }
        }
        cursor.close(); // Assicurati di chiudere il cursor anche se non ci sono risultati
        return null;
    }


    public Category getCategoryById(int categoryId) {
        Cursor cursor = db.query(TABLE_CATEGORY, new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION}, COLUMN_ID + " = ?", new String[]{String.valueOf(categoryId)}, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
            int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);

            if (idIndex != -1 && nameIndex != -1 && descriptionIndex != -1) {
                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                String description = cursor.isNull(descriptionIndex) ? null : cursor.getString(descriptionIndex);
                cursor.close();
                return new Category(id, name, description);
            }
        }
        cursor.close();
        return null;
    }

    public int getCategoryIdByName(String categoryName) {
        int categoryId = -1; // Valore di default se la categoria non viene trovata
        Cursor cursor = db.query("Categories", // Nome della tabella
                new String[]{"ID"}, // Colonne da restituire
                "Name = ?", // Clausola WHERE
                new String[]{categoryName}, // Valori per la clausola WHERE
                null, null, null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("ID");
            if (columnIndex >= 0) { // Controlla che l'indice della colonna sia valido
                categoryId = cursor.getInt(columnIndex);
            }
        }
        cursor.close();
        db.close();
        return categoryId;
    }

    public int getIdByAccountName(String accountName) {
        Cursor cursor = db.query(TABLE_ACCOUNT, new String[]{COLUMN_ID}, COLUMN_NAME + "=?", new String[]{accountName}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int accountId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            cursor.close();
            return accountId;
        } else {
            return -1; // or any default value
        }
    }

}
