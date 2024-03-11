package com.example.cashflow.db;


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


}
