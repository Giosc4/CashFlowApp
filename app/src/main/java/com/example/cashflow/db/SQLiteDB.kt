package com.example.cashflow.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class SQLiteDB(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    init {
        Log.d("SQLiteDB", "Database created")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d("SQLiteDB", "Database upgraded from version $oldVersion to $newVersion")
        populateSampleData(db)

    }
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_ACCOUNT)
        db.execSQL(CREATE_TABLE_CITY)
        db.execSQL(CREATE_TABLE_TEMPLATE_TRANSACTIONS)
        db.execSQL(CREATE_TABLE_CATEGORY)
        db.execSQL(CREATE_TABLE_DEBITO)
        db.execSQL(CREATE_TABLE_CREDITO)
        db.execSQL(CREATE_TABLE_BUDGET)
        db.execSQL(CREATE_TABLE_SAVING)
        db.execSQL(CREATE_TABLE_TRANSACTION)
        db.execSQL(CREATE_TABLE_PLANNING)
        db.execSQL(CREATE_TRIGGER_UPDATE_BALANCE)
        Log.d("SQLiteDB", "Database created")
        populateSampleData(db)


    }

    fun clearAllTableData(db: SQLiteDatabase) {
        db.execSQL("DELETE FROM " + TABLE_ACCOUNT)
        db.execSQL("DELETE FROM " + TABLE_CITY)
        db.execSQL("DELETE FROM " + TABLE_TRANSACTIONS)
        db.execSQL("DELETE FROM " + TABLE_CATEGORY)
        db.execSQL("DELETE FROM " + TABLE_SAVING)
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVING)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLANNING)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMPLATE_TRANSACTIONS)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEBITO)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CREDITO)
        Log.d("SQLiteDB", "All tables deleted")
    }

    fun populateSampleData(db: SQLiteDatabase) {
        Log.d("SQLiteDB", "Populating sample data")
        // Dati di esempio per Account
        val insertAccountData = """
        INSERT INTO Account (name, balance) VALUES 
        ('Conto Corrente', 1200.00),
        ('Libretto Risparmio', 5000.00);
    """.trimIndent()
        db.execSQL(insertAccountData)

        // Dati di esempio per City
        val insertCityData = """
        INSERT INTO City (city_name, latitude, longitude) VALUES 
        ('Roma', 41.9028, 12.4964),
        ('Milano', 45.4642, 9.1900);
    """.trimIndent()
        db.execSQL(insertCityData)

        // Dati di esempio per Category
        val insertCategoryData = """
        INSERT INTO Category (name, description) VALUES 
        ('Alimentari', 'Spese per cibo e generi alimentari'),
        ('Intrattenimento', 'Spese per attività ricreative e divertimento');
    """.trimIndent()
        db.execSQL(insertCategoryData)

        // Aggiungi qui gli insert per le altre tabelle seguendo il pattern sopra

        // Esempio per Transactions (assicurati che gli ID delle foreign key corrispondano ai dati inseriti nelle altre tabelle)
        val insertTransactionsData = """
        INSERT INTO Transactions (income, amount, date, city_id, category_id, account_id) VALUES 
        (0, 50.00, '2024-03-15', 1, 1, 1),
        (1, 100.00, '2024-03-16', 2, 2, 2);
    """.trimIndent()
        db.execSQL(insertTransactionsData)

        // Dati di esempio per Saving
        val insertSavingData = """
        INSERT INTO Risparmio (amount, account_id, Data_Inizio, Data_Fine) VALUES 
        (2000.00, 1, '2024-01-01', '2024-12-31'),
        (1500.00, 2, '2024-01-01', '2024-06-30');
    """.trimIndent()
        db.execSQL(insertSavingData)

        // Dati di esempio per Budget
        val insertBudgetData = """
        INSERT INTO Budget (category_id, amount, name) VALUES 
        (1, 300.00, 'Budget Alimentari Marzo'),
        (2, 150.00, 'Budget Intrattenimento Marzo');
    """.trimIndent()
        db.execSQL(insertBudgetData)

        // Dati di esempio per Planning
        val insertPlanningData = """
        INSERT INTO Pianificazione (Template_ID, Ripetizione, Data_Fine) VALUES 
        (1, 'Mensile', '2024-12-31'),
        (2, 'Settimanale', '2024-12-31');
    """.trimIndent()
        db.execSQL(insertPlanningData)

        // Dati di esempio per Template_Transazioni
        val insertTemplateTransactionsData = """
        INSERT INTO Template_Transazioni (name, income, amount, category_id, account_id) VALUES 
        ('Stipendio', 1, 1500.00, 1, 1),
        ('Affitto', 0, -500.00, 2, 1);
    """.trimIndent()
        db.execSQL(insertTemplateTransactionsData)

        // Dati di esempio per Debito
        val insertDebitoData = """
        INSERT INTO Debito (amount, name, Data_Concessione, Data_Estinsione, account_id) VALUES 
        (1000.00, 'Prestito Auto', '2023-01-01', '2024-01-01', 1),
        (500.00, 'Prestito Personale', '2023-06-01', '2024-06-01', 2);
    """.trimIndent()
        db.execSQL(insertDebitoData)

        // Dati di esempio per Credito
        val insertCreditoData = """
        INSERT INTO Credito (amount, name, Data_Concessione, Data_Estinsione, account_id) VALUES 
        (2000.00, 'Linea di Credito', '2023-01-01', '2025-01-01', 1),
        (1000.00, 'Carta di Credito', '2023-02-01', '2024-02-01', 2);
    """.trimIndent()
        db.execSQL(insertCreditoData)
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
        private const val TABLE_SAVING = "Risparmio"
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
        private const val COLUMN_INCOME = "income"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_CITY_ID = "city_id"
        private const val COLUMN_CATEGORY_ID = "category_id"
        private const val COLUMN_ACCOUNT_ID = "account_id"

        // Category Table - column names
        private const val COLUMN_DESCRIPTION = "description"

        // Saving Table - column names
        private const val COLUMN_START_DATE = "Data_Inizio"
        private const val COLUMN_END_DATE = "Data_Fine"

        // Budget Table - column names
        // Note: Uses COLUMN_AMOUNT, COLUMN_NAME from Transactions
        // Planning Table - column names
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
                    COLUMN_INCOME + " INTEGER NOT NULL, " +  // SQLite non ha BOOLEAN, si usa INTEGER con 0 (false) e 1 (true)
                    COLUMN_AMOUNT + " REAL NOT NULL, " +
                    COLUMN_DATE + " TEXT NOT NULL, " +
                    COLUMN_CITY_ID + " INTEGER, " +
                    COLUMN_CATEGORY_ID + " INTEGER NOT NULL, " +
                    COLUMN_ACCOUNT_ID + " INTEGER, " +
                    "FOREIGN KEY (" + COLUMN_CITY_ID + ") REFERENCES " + TABLE_CITY + "(" + COLUMN_ID + "), " +
                    "FOREIGN KEY (" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORY + "(" + COLUMN_ID + "), " +
                    "FOREIGN KEY (" + COLUMN_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNT + "(" + COLUMN_ID + ") );"

        // Modifica le variabili esistenti per utilizzare le nuove costanti
        const val CREATE_TABLE_SAVING = "CREATE TABLE IF NOT EXISTS " + TABLE_SAVING + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_AMOUNT + " REAL NOT NULL, " +
                COLUMN_ACCOUNT_ID + " INTEGER, " +
                COLUMN_START_DATE + " TEXT, " +
                COLUMN_END_DATE + " TEXT, " +
                "FOREIGN KEY (" + COLUMN_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNT + "(" + COLUMN_ID + ") );"
        const val CREATE_TABLE_BUDGET = "CREATE TABLE IF NOT EXISTS " + TABLE_BUDGET + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CATEGORY_ID + " INTEGER, " +
                COLUMN_AMOUNT + " REAL NOT NULL, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORY + "(" + COLUMN_ID + ") );"
        const val CREATE_TABLE_PLANNING = "CREATE TABLE IF NOT EXISTS " + TABLE_PLANNING + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TEMPLATE_ID + " INTEGER, " +
                COLUMN_REPETITION + " TEXT, " +
                COLUMN_END_DATE + " TEXT, " +
                "FOREIGN KEY (" + COLUMN_TEMPLATE_ID + ") REFERENCES Template_Transazioni(" + COLUMN_ID + ") );"
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
                "FOREIGN KEY (" + COLUMN_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNT + "(" + COLUMN_ID + ") );"
        const val CREATE_TABLE_CREDITO = "CREATE TABLE IF NOT EXISTS " + TABLE_CREDITO + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_AMOUNT + " REAL NOT NULL, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_CONCESSION_DATE + " TEXT, " +
                COLUMN_EXTINCTION_DATE + " TEXT, " +
                COLUMN_ACCOUNT_ID + " INTEGER, " +
                "FOREIGN KEY (" + COLUMN_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNT + "(" + COLUMN_ID + ") );"
    }
}
