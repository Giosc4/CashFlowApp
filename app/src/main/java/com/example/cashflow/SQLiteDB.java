package com.example.cashflow;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.cashflow.dataClass.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SQLiteDB extends SQLiteOpenHelper {


    // Database Version and Name
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "cashflow.db";

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

    public static final String CREATE_TABLE_ACCOUNT =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ACCOUNT + " ( " +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_BALANCE + " REAL NOT NULL );";

    public static final String CREATE_TABLE_CITY =
            "CREATE TABLE  IF NOT EXISTS " + TABLE_CITY + " ( " +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CITY_NAME + " TEXT NOT NULL, " +
                    COLUMN_LATITUDE + " REAL, " +
                    COLUMN_LONGITUDE + " REAL );";

    public static final String CREATE_TABLE_CATEGORY =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORY + " ( " +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_DESCRIPTION + " TEXT );";

    public static final String CREATE_TABLE_TRANSACTION =
            "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSACTIONS + " ( " +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_INCOME + " INTEGER NOT NULL, " + // SQLite non ha BOOLEAN, si usa INTEGER con 0 (false) e 1 (true)
                    COLUMN_AMOUNT + " REAL NOT NULL, " +
                    COLUMN_DATE + " TEXT NOT NULL, " +
                    COLUMN_CITY_ID + " INTEGER, " +
                    COLUMN_CATEGORY_ID + " INTEGER NOT NULL, " +
                    COLUMN_ACCOUNT_ID + " INTEGER, " +
                    "FOREIGN KEY (" + COLUMN_CITY_ID + ") REFERENCES " + TABLE_CITY + "(" + COLUMN_ID + "), " +
                    "FOREIGN KEY (" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORY + "(" + COLUMN_ID + "), " +
                    "FOREIGN KEY (" + COLUMN_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNT + "(" + COLUMN_ID + ") );";
    // Modifica le variabili esistenti per utilizzare le nuove costanti
    public static final String CREATE_TABLE_SAVING =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SAVING + " ( " +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_AMOUNT + " REAL NOT NULL, " +
                    COLUMN_ACCOUNT_ID + " INTEGER, " +
                    COLUMN_START_DATE + " TEXT, " +
                    COLUMN_END_DATE + " TEXT, " +
                    "FOREIGN KEY (" + COLUMN_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNT + "(" + COLUMN_ID + ") );";

    public static final String CREATE_TABLE_BUDGET =
            "CREATE TABLE IF NOT EXISTS " + TABLE_BUDGET + " ( " +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CATEGORY_ID + " INTEGER, " +
                    COLUMN_AMOUNT + " REAL NOT NULL, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    "FOREIGN KEY (" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORY + "(" + COLUMN_ID + ") );";

    public static final String CREATE_TABLE_PLANNING =
            "CREATE TABLE IF NOT EXISTS " + TABLE_PLANNING + " ( " +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TEMPLATE_ID + " INTEGER, " +
                    COLUMN_REPETITION + " TEXT, " +
                    COLUMN_END_DATE + " TEXT, " +
                    "FOREIGN KEY (" + COLUMN_TEMPLATE_ID + ") REFERENCES Template_Transazioni(" + COLUMN_ID + ") );";


    public static final String CREATE_TABLE_TEMPLATE_TRANSACTIONS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_TEMPLATE_TRANSACTIONS + " ( " +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL );";

    public static final String CREATE_TABLE_DEBITO =
            "CREATE TABLE IF NOT EXISTS " + TABLE_DEBITO + " ( " +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_AMOUNT + " REAL NOT NULL, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_CONCESSION_DATE + " TEXT, " +
                    COLUMN_EXTINCTION_DATE + " TEXT, " +
                    COLUMN_ACCOUNT_ID + " INTEGER, " +
                    "FOREIGN KEY (" + COLUMN_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNT + "(" + COLUMN_ID + ") );";

    public static final String CREATE_TABLE_CREDITO =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CREDITO + " ( " +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_AMOUNT + " REAL NOT NULL, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_CONCESSION_DATE + " TEXT, " +
                    COLUMN_EXTINCTION_DATE + " TEXT, " +
                    COLUMN_ACCOUNT_ID + " INTEGER, " +
                    "FOREIGN KEY (" + COLUMN_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNT + "(" + COLUMN_ID + ") );";


    // Standard constructor
    public SQLiteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        Log.d("SQLiteDB", "Database created");
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ACCOUNT);
        db.execSQL(CREATE_TABLE_CITY);
        db.execSQL(CREATE_TABLE_TEMPLATE_TRANSACTIONS);
        db.execSQL(CREATE_TABLE_CATEGORY);
        db.execSQL(CREATE_TABLE_DEBITO);
        db.execSQL(CREATE_TABLE_CREDITO);
        db.execSQL(CREATE_TABLE_BUDGET);
        db.execSQL(CREATE_TABLE_SAVING);
        db.execSQL(CREATE_TABLE_TRANSACTION);
        db.execSQL(CREATE_TABLE_PLANNING);

    }

    public void clearAllTableData(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + TABLE_ACCOUNT);
        db.execSQL("DELETE FROM " + TABLE_CITY);
        db.execSQL("DELETE FROM " + TABLE_TRANSACTIONS);
        db.execSQL("DELETE FROM " + TABLE_CATEGORY);
        db.execSQL("DELETE FROM " + TABLE_SAVING);
        db.execSQL("DELETE FROM " + TABLE_BUDGET);
        db.execSQL("DELETE FROM " + TABLE_PLANNING);
        db.execSQL("DELETE FROM " + TABLE_TEMPLATE_TRANSACTIONS);
        db.execSQL("DELETE FROM " + TABLE_DEBITO);
        db.execSQL("DELETE FROM " + TABLE_CREDITO);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Elimina tutte le tabelle esistenti
        deleteAllTables(db);

        // Ricrea il database
        onCreate(db);
    }

    public void deleteAllTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLANNING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMPLATE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEBITO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CREDITO);
    }


    // Aggiorna un Account esistente
    public int updateAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, account.getName());
        values.put(COLUMN_BALANCE, account.getBalance());

        // Aggiornamento dell'account basato sull'ID
        return db.update(TABLE_ACCOUNT, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(account.getId())});
    }


    public boolean createAccount(String account_name, double balance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, account_name);
        cv.put(COLUMN_BALANCE, balance);
        long insert = db.insert(TABLE_ACCOUNT, null, cv);
        if (insert == -1) {
            Log.e("SQLiteDB", "Failed to insert row");
            return false;
        } else {
            return true;
        }
    }

    // Cancella un Account
    public void deleteAccount(int accountId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCOUNT, COLUMN_ID + " = ?",
                new String[]{String.valueOf(accountId)});
        db.close();
    }

    public ArrayList<Account> getAllAccounts() {
        ArrayList<Account> accounts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ACCOUNT, null);

        if (cursor.moveToFirst()) {
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
                    // Handle the case where the columns don't exist in the cursor
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return accounts;
    }


    public boolean createCity(String cityName, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CITY_NAME, cityName);
        cv.put(COLUMN_LATITUDE, latitude);
        cv.put(COLUMN_LONGITUDE, longitude);
        long insert = db.insert(TABLE_CITY, null, cv);
        if (insert == -1) {
            Log.e("SQLiteDB", "Failed to insert city");
            return false;
        } else {
            return true;
        }
    }

    public List<City> getAllCities() {
        List<City> cities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
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

    public boolean createCategory(String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_DESCRIPTION, description);
        long insert = db.insert(TABLE_CATEGORY, null, cv);
        if (insert == -1) {
            Log.e("SQLiteDB", "Failed to insert category");
            return false;
        } else {
            return true;
        }
    }

    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
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


    public boolean createTransaction(int income, double amount, String date, Integer cityId, int categoryId, Integer accountId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_INCOME, income);
        cv.put(COLUMN_AMOUNT, amount);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_CITY_ID, cityId);
        cv.put(COLUMN_CATEGORY_ID, categoryId);
        cv.put(COLUMN_ACCOUNT_ID, accountId);
        long insert = db.insert(TABLE_TRANSACTIONS, null, cv);
        if (insert == -1) {
            Log.e("SQLiteDB", "Failed to insert transaction");
            return false;
        } else {
            return true;
        }
    }

    public ArrayList<Transactions> getAllTransactions() throws ParseException {
        ArrayList<Transactions> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
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
        SQLiteDatabase db = this.getReadableDatabase();
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
        SQLiteDatabase db = this.getReadableDatabase();
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


    public void updateTransaction(Transactions newTrans) {
        // Ottieni il database in modalità scrittura
        SQLiteDatabase db = this.getWritableDatabase();

        // Crea un nuovo mappa di valori, dove i nomi delle colonne sono le chiavi
        ContentValues values = new ContentValues();
        values.put(COLUMN_INCOME, newTrans.isIncome() ? 1 : 0); // SQLite non ha un tipo boolean, quindi usiamo 1 per true e 0 per false
        values.put(COLUMN_AMOUNT, newTrans.getAmount());
        values.put(COLUMN_DATE, newTrans.getDate().getTimeInMillis()); // Assumendo che getDate() restituisca un Calendar
        values.put(COLUMN_CITY_ID, newTrans.getCityId());
        values.put(COLUMN_CATEGORY_ID, newTrans.getCategoryId());
        values.put(COLUMN_ACCOUNT_ID, newTrans.getAccountId());

        // Aggiornamento della riga, ritorna il numero di righe aggiornate
        int count = db.update(TABLE_TRANSACTIONS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(newTrans.getId())});

        if (count > 0) {
            Log.d("SQLiteDB", "Transazione aggiornata con successo");
        } else {
            Log.e("SQLiteDB", "Errore nell'aggiornamento della transazione");
        }

        // Chiudi il database
        db.close();
    }

    public void deleteTransaction(int transactionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Elimina la transazione dato il suo ID
        int deletedRows = db.delete(TABLE_TRANSACTIONS, COLUMN_ID + " = ?", new String[]{String.valueOf(transactionId)});
        if (deletedRows > 0) {
            Log.d("SQLiteDB", "Transazione eliminata con successo");
        } else {
            Log.e("SQLiteDB", "Errore nell'eliminazione della transazione");
        }
        db.close();
    }

    public Category getCategoryById(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
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
        SQLiteDatabase db = this.getReadableDatabase();
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
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACCOUNT, new String[]{COLUMN_ID}, COLUMN_NAME + "=?", new String[]{accountName}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int accountId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            cursor.close();
            return accountId;
        } else {
            return -1; // or any default value
        }
    }

    public void createPosition() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        City citta = new City();
        values.put("Name", citta.getNameCity());
        values.put("Latitude", citta.getLatitude());
        values.put("Longitude", citta.getLongitude());
        db.insert("Cities", null, values);
        db.close();

    }

    public long addTransaction(Transactions transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Income", transaction.isIncome() ? 1 : 0);
        values.put("Amount", transaction.getAmount());
        values.put("Date", transaction.getDate().getTimeInMillis());
        values.put("CityId", transaction.getCityId());
        values.put("CategoryId", transaction.getCategoryId());
        values.put("AccountId", transaction.getAccountId()); // Assumendo che Transactions abbia questo campo

        long transactionId = db.insert("Transactions", null, values);
        db.close();
        return transactionId;
    }

    public void updateAccountBalance(String accountName, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Accounts SET Balance = Balance + ? WHERE Name = ?",
                new Object[]{amount, accountName});
        db.close();
    }

    public String getCityNameById(int cityId) {
        SQLiteDatabase db = this.getReadableDatabase();
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
        SQLiteDatabase db = this.getReadableDatabase();
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
        SQLiteDatabase db = this.getReadableDatabase();
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


}
