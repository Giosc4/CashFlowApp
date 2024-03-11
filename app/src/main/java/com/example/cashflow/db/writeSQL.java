package com.example.cashflow.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.cashflow.dataClass.Account;
import com.example.cashflow.dataClass.City;
import com.example.cashflow.dataClass.Transactions;

public class writeSQL {

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

    public writeSQL(SQLiteDatabase db) {
        this.db = db;
    }


    public boolean createAccount(String account_name, double balance) {
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
        db.delete(TABLE_ACCOUNT, COLUMN_ID + " = ?",
                new String[]{String.valueOf(accountId)});
    }

    public void createCity(String cityName, double latitude, double longitude) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CITY_NAME, cityName); // Usa la costante che corrisponde al nome della colonna
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        db.insert(TABLE_CITY, null, values);
    }




    public boolean createCategory(String name, String description) {
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

    public boolean createTransaction(int income, double amount, String date, Integer cityId, int categoryId, Integer accountId) {
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


    public void updateTransaction(Transactions newTrans) {
        // Ottieni il database in modalità scrittura

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

    }

    public void deleteTransaction(int transactionId) {
        // Elimina la transazione dato il suo ID
        int deletedRows = db.delete(TABLE_TRANSACTIONS, COLUMN_ID + " = ?", new String[]{String.valueOf(transactionId)});
        if (deletedRows > 0) {
            Log.d("SQLiteDB", "Transazione eliminata con successo");
        } else {
            Log.e("SQLiteDB", "Errore nell'eliminazione della transazione");
        }
    }

    // Aggiorna un Account esistente
    public int updateAccount(Account account) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, account.getName());
        values.put(COLUMN_BALANCE, account.getBalance());

        // Aggiornamento dell'account basato sull'ID
        return db.update(TABLE_ACCOUNT, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(account.getId())});
    }



    public long addTransaction(Transactions transaction) {
        ContentValues values = new ContentValues();
        values.put("Income", transaction.isIncome() ? 1 : 0);
        values.put("Amount", transaction.getAmount());
        values.put("Date", transaction.getDate().getTimeInMillis());
        values.put("CityId", transaction.getCityId());
        values.put("CategoryId", transaction.getCategoryId());
        values.put("AccountId", transaction.getAccountId()); // Assumendo che Transactions abbia questo campo

        long transactionId = db.insert(TABLE_TRANSACTIONS, null, values);
        return transactionId;
    }

    public void updateAccountBalance(String accountName, double amount) {
        db.execSQL("UPDATE Accounts SET Balance = Balance + ? WHERE Name = ?",
                new Object[]{amount, accountName});
    }
}
