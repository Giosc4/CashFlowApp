package com.example.cashflow.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class SQLiteDB(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    init {
        Log.d("SQLiteDB", "Database initialized")
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        if (!db.isReadOnly) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d("SQLiteDB", "Database upgraded from version $oldVersion to $newVersion")
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_ACCOUNT)
        db.execSQL(CREATE_TABLE_CITY)
        db.execSQL(CREATE_TABLE_TEMPLATE_TRANSACTIONS)
        db.execSQL(CREATE_TABLE_CATEGORY)
        db.execSQL(CREATE_TABLE_DEBITO)
        db.execSQL(CREATE_TABLE_CREDITO)
        db.execSQL(CREATE_TABLE_BUDGET)
        db.execSQL(CREATE_TABLE_TRANSACTION)
        db.execSQL(CREATE_TABLE_PLANNING)
        db.execSQL(CREATE_TRIGGER_UPDATE_BALANCE)
        Log.d("SQLiteDB", "Database created")
    }

    fun clearAllTableData(db: SQLiteDatabase) {
        db.execSQL("DELETE FROM " + TABLE_ACCOUNT)
        db.execSQL("DELETE FROM " + TABLE_CITY)
        db.execSQL("DELETE FROM " + TABLE_TRANSACTIONS)
        db.execSQL("DELETE FROM " + TABLE_CATEGORY)
        db.execSQL("DELETE FROM " + TABLE_BUDGET)
        db.execSQL("DELETE FROM " + TABLE_PLANNING)
        db.execSQL("DELETE FROM " + TABLE_TEMPLATE_TRANSACTIONS)
        db.execSQL("DELETE FROM " + TABLE_DEBITO)
        db.execSQL("DELETE FROM " + TABLE_CREDITO)
        Log.d("SQLiteDB", "All tables cleared")
    }


    fun deleteAllTables(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITY)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLANNING)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMPLATE_TRANSACTIONS)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEBITO)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CREDITO)
        Log.d("SQLiteDB", "All tables deleted")
    }


    companion object {
        // Database Version and Name
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "cashflow.db"

        // Table Names
        private const val TABLE_ACCOUNT = "Account"
        private const val TABLE_CITY = "City"
        private const val TABLE_TRANSACTIONS = "Transactions"
        private const val TABLE_CATEGORY = "Category"
        private const val TABLE_BUDGET = "Budget"
        private const val TABLE_PLANNING = "Pianificazione"
        private const val TABLE_TEMPLATE_TRANSACTIONS = "Template_Transazioni"
        private const val TABLE_DEBITO = "Debito"
        private const val TABLE_CREDITO = "Credito"

        // Common column names
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"

        // Account Table - column names
        private const val COLUMN_BALANCE = "balance"

        // City Table - column names
        private const val COLUMN_CITY_NAME = "city_name"
        private const val COLUMN_LATITUDE = "latitude"
        private const val COLUMN_LONGITUDE = "longitude"

        // Transactions Table - column names
        private const val COLUMN_TRANSACTION_ID = "transaction_id"
        private const val COLUMN_INCOME = "income"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_CITY_ID = "city_id"
        private const val COLUMN_CATEGORY_ID = "category_id"
        private const val COLUMN_ACCOUNT_ID = "account_id"

        // Category Table - column names
        private const val COLUMN_DESCRIPTION = "description"

        // Budget Table - column names
        // Note: Uses COLUMN_AMOUNT, COLUMN_NAME from Transactions
        // Planning Table - column names
        private const val COLUMN_END_DATE = "Data_Fine"
        private const val COLUMN_TEMPLATE_ID = "Template_ID"
        private const val COLUMN_REPETITION = "Ripetizione"

        // Debito and Credito Tables - column names
        private const val COLUMN_CONCESSION_DATE = "Data_Concessione"
        private const val COLUMN_EXTINCTION_DATE = "Data_Estinsione"

        val CREATE_TRIGGER_UPDATE_BALANCE = """
        CREATE TRIGGER UpdateAccountBalance AFTER INSERT ON $TABLE_TRANSACTIONS
        FOR EACH ROW
        BEGIN
            UPDATE $TABLE_ACCOUNT
            SET $COLUMN_BALANCE = $COLUMN_BALANCE + CASE
                WHEN NEW.$COLUMN_INCOME = 1 THEN NEW.$COLUMN_AMOUNT
                ELSE -NEW.$COLUMN_AMOUNT
            END
            WHERE $COLUMN_ID = NEW.$COLUMN_ACCOUNT_ID;
        END;
    """.trimIndent()


        const val CREATE_TABLE_ACCOUNT = "CREATE TABLE IF NOT EXISTS " + TABLE_ACCOUNT + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_BALANCE + " REAL NOT NULL );"
        const val CREATE_TABLE_CITY = "CREATE TABLE  IF NOT EXISTS " + TABLE_CITY + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CITY_NAME + " TEXT NOT NULL, " +
                COLUMN_LATITUDE + " REAL, " +
                COLUMN_LONGITUDE + " REAL );"
        const val CREATE_TABLE_CATEGORY = "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORY + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_DESCRIPTION + " TEXT );"
        const val CREATE_TABLE_TRANSACTION =
            "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSACTIONS + " ( " +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_INCOME + " INTEGER NOT NULL, " +
                    COLUMN_AMOUNT + " REAL NOT NULL, " +
                    COLUMN_DATE + " TEXT NOT NULL, " +
                    COLUMN_CITY_ID + " INTEGER, " +
                    COLUMN_CATEGORY_ID + " INTEGER NOT NULL, " +
                    COLUMN_ACCOUNT_ID + " INTEGER, " +
                    "FOREIGN KEY (" + COLUMN_CITY_ID + ") REFERENCES " + TABLE_CITY + "(" + COLUMN_ID + "), " +
                    "FOREIGN KEY (" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORY + "(" + COLUMN_ID + "), " +
                    "FOREIGN KEY (" + COLUMN_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNT + "(" + COLUMN_ID + ") ON DELETE CASCADE);"

        const val CREATE_TABLE_BUDGET = "CREATE TABLE IF NOT EXISTS " + TABLE_BUDGET + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CATEGORY_ID + " INTEGER, " +
                COLUMN_AMOUNT + " REAL NOT NULL, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORY + "(" + COLUMN_ID + ") ON DELETE CASCADE);"
        const val CREATE_TABLE_PLANNING =
            "CREATE TABLE IF NOT EXISTS $TABLE_PLANNING ( " +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_INCOME INTEGER NOT NULL, " +
                    "$COLUMN_AMOUNT REAL NOT NULL, " +
                    "$COLUMN_DATE TEXT NOT NULL, " +
                    "$COLUMN_CITY_ID INTEGER, " +
                    "$COLUMN_CATEGORY_ID INTEGER NOT NULL, " +
                    "$COLUMN_ACCOUNT_ID INTEGER, " +
                    "$COLUMN_REPETITION TEXT NOT NULL, " +
                    "$COLUMN_END_DATE TEXT NOT NULL, " +
                    "FOREIGN KEY ($COLUMN_CITY_ID) REFERENCES $TABLE_CITY($COLUMN_ID), " +
                    "FOREIGN KEY ($COLUMN_CATEGORY_ID) REFERENCES $TABLE_CATEGORY($COLUMN_ID), " +
                    "FOREIGN KEY ($COLUMN_ACCOUNT_ID) REFERENCES $TABLE_ACCOUNT($COLUMN_ID) ON DELETE CASCADE);"
        const val CREATE_TABLE_TEMPLATE_TRANSACTIONS =
            "CREATE TABLE IF NOT EXISTS $TABLE_TEMPLATE_TRANSACTIONS ( " +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_NAME TEXT NOT NULL, " +
                    "$COLUMN_INCOME INTEGER NOT NULL, " +
                    "$COLUMN_AMOUNT REAL NOT NULL, " +
                    "$COLUMN_CATEGORY_ID INTEGER NOT NULL, " +
                    "$COLUMN_ACCOUNT_ID INTEGER NOT NULL, " +
                    "FOREIGN KEY ($COLUMN_CATEGORY_ID) REFERENCES $TABLE_CATEGORY($COLUMN_ID), " +
                    "FOREIGN KEY ($COLUMN_ACCOUNT_ID) REFERENCES $TABLE_ACCOUNT($COLUMN_ID) );"
        const val CREATE_TABLE_DEBITO = "CREATE TABLE IF NOT EXISTS " + TABLE_DEBITO + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_AMOUNT + " REAL NOT NULL, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_CONCESSION_DATE + " TEXT, " +
                COLUMN_EXTINCTION_DATE + " TEXT, " +
                COLUMN_ACCOUNT_ID + " INTEGER, " +
                "FOREIGN KEY (" + COLUMN_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNT + "(" + COLUMN_ID + ") ON DELETE CASCADE);"
        const val CREATE_TABLE_CREDITO = "CREATE TABLE IF NOT EXISTS " + TABLE_CREDITO + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_AMOUNT + " REAL NOT NULL, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_CONCESSION_DATE + " TEXT, " +
                COLUMN_EXTINCTION_DATE + " TEXT, " +
                COLUMN_ACCOUNT_ID + " INTEGER, " +
                "FOREIGN KEY (" + COLUMN_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNT + "(" + COLUMN_ID + ") ON DELETE CASCADE);"
    }
}
