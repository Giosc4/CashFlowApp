package com.example.cashflow.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.cashflow.dataClass.Account
import com.example.cashflow.dataClass.Transactions

class writeSQL(private val db: SQLiteDatabase) {
    fun createAccount(account_name: String?, balance: Double): Boolean {
        val cv = ContentValues()
        cv.put(COLUMN_NAME, account_name)
        cv.put(COLUMN_BALANCE, balance)
        val insert = db.insert(TABLE_ACCOUNT, null, cv)
        return if (insert == -1L) {
            Log.e("SQLiteDB", "Failed to insert row")
            false
        } else {
            true
        }
    }

    // Cancella un Account
    fun deleteAccount(accountId: Int) {
        db.delete(TABLE_ACCOUNT, COLUMN_ID + " = ?", arrayOf(accountId.toString()))
    }

    fun createCity(cityName: String?, latitude: Double, longitude: Double) {
        val values = ContentValues()
        values.put(
            COLUMN_CITY_NAME,
            cityName
        ) // Usa la costante che corrisponde al nome della colonna
        values.put(COLUMN_LATITUDE, latitude)
        values.put(COLUMN_LONGITUDE, longitude)
        db.insert(TABLE_CITY, null, values)
    }

    fun createCategory(name: String?, description: String?): Boolean {
        val cv = ContentValues()
        cv.put(COLUMN_NAME, name)
        cv.put(COLUMN_DESCRIPTION, description)
        val insert = db.insert(TABLE_CATEGORY, null, cv)
        return if (insert == -1L) {
            Log.e("SQLiteDB", "Failed to insert category")
            false
        } else {
            true
        }
    }

    fun createTransaction(
        income: Int,
        amount: Double,
        date: String?,
        cityId: Int?,
        categoryId: Int,
        accountId: Int?
    ): Boolean {
        val cv = ContentValues()
        cv.put(COLUMN_INCOME, income)
        cv.put(COLUMN_AMOUNT, amount)
        cv.put(COLUMN_DATE, date)
        cv.put(COLUMN_CITY_ID, cityId)
        cv.put(COLUMN_CATEGORY_ID, categoryId)
        cv.put(COLUMN_ACCOUNT_ID, accountId)
        val insert = db.insert(TABLE_TRANSACTIONS, null, cv)
        return if (insert == -1L) {
            Log.e("SQLiteDB", "Failed to insert transaction")
            false
        } else {
            true
        }
    }
    fun updateAccountName(accountId: Int, newName: String) {
        val values = ContentValues().apply {
            put(COLUMN_NAME, newName) // Aggiorna il nome con il nuovo valore
        }
        val updatedRows = db.update(
            TABLE_ACCOUNT, // Tabella in cui effettuare l'aggiornamento
            values, // Valori da aggiornare
            "$COLUMN_ID = ?", // Clausola WHERE
            arrayOf(accountId.toString()) // Valori per la clausola WHERE
        )
        if (updatedRows > 0) {
            Log.d("SQLiteDB", "Account updated successfully.")
        } else {
            Log.e("SQLiteDB", "Failed to update account.")
        }
    }

    fun insertTransaction(newTrans: Transactions): Long {
        val values = ContentValues().apply {
            put(COLUMN_INCOME, if (newTrans.isIncome) 1 else 0) // SQLite non ha un tipo booleano, quindi usiamo 1 per true e 0 per false
            put(COLUMN_AMOUNT, newTrans.amountValue)
            put(COLUMN_DATE, newTrans.date.timeInMillis)
            put(COLUMN_CITY_ID, newTrans.cityId)
            put(COLUMN_CATEGORY_ID, newTrans.categoryId)
            put(COLUMN_ACCOUNT_ID, newTrans.accountId)
        }

        // Inserisce la nuova transazione nel database e restituisce l'ID della riga appena inserita, oppure -1 in caso di errore
        val newRowId = db.insert(TABLE_TRANSACTIONS, null, values)

        if (newRowId == -1L) {
            Log.e("SQLiteDB", "Failed to insert new transaction")
        } else {
            Log.d("SQLiteDB", "Transaction inserted successfully with ID: $newRowId")
        }

        return newRowId
    }


    fun updateTransaction(newTrans: Transactions) {
        // Ottieni il database in modalità scrittura

        // Crea un nuovo mappa di valori, dove i nomi delle colonne sono le chiavi
        val values = ContentValues()
        values.put(
            COLUMN_INCOME,
            if (newTrans.isIncome) 1 else 0
        ) // SQLite non ha un tipo boolean, quindi usiamo 1 per true e 0 per false
        values.put(COLUMN_AMOUNT, newTrans.amountValue)
        values.put(
            COLUMN_DATE,
            newTrans.date.getTimeInMillis()
        ) // Assumendo che getDate() restituisca un Calendar
        values.put(COLUMN_CITY_ID, newTrans.cityId)
        values.put(COLUMN_CATEGORY_ID, newTrans.categoryId)
        values.put(COLUMN_ACCOUNT_ID, newTrans.accountId)

        // Aggiornamento della riga, ritorna il numero di righe aggiornate
        val count = db.update(
            TABLE_TRANSACTIONS,
            values,
            COLUMN_ID + " = ?",
            arrayOf(newTrans.id.toString())
        )
        if (count > 0) {
            Log.d("SQLiteDB", "Transazione aggiornata con successo")
        } else {
            Log.e("SQLiteDB", "Errore nell'aggiornamento della transazione")
        }
    }

    fun deleteTransaction(transactionId: Int) {
        // Elimina la transazione dato il suo ID
        val deletedRows =
            db.delete(TABLE_TRANSACTIONS, COLUMN_ID + " = ?", arrayOf(transactionId.toString()))
        if (deletedRows > 0) {
            Log.d("SQLiteDB", "Transazione eliminata con successo")
        } else {
            Log.e("SQLiteDB", "Errore nell'eliminazione della transazione")
        }
    }

    // Aggiorna un Account esistente
    fun updateAccount(account: Account): Int {
        val values = ContentValues()
        values.put(COLUMN_NAME, account.name)
        values.put(COLUMN_BALANCE, account.balance)

        // Aggiornamento dell'account basato sull'ID
        return db.update(TABLE_ACCOUNT, values, COLUMN_ID + " = ?", arrayOf(account.id.toString()))
    }

    fun addTransaction(transaction: Transactions): Long {
        val values = ContentValues()
        values.put("Income", if (transaction.isIncome) 1 else 0)
        values.put("Amount", transaction.amountValue)
        values.put("Date", transaction.date.getTimeInMillis())
        values.put("CityId", transaction.cityId)
        values.put("CategoryId", transaction.categoryId)
        values.put(
            "AccountId",
            transaction.accountId
        ) // Assumendo che Transactions abbia questo campo
        return db.insert(TABLE_TRANSACTIONS, null, values)
    }

    fun updateAccountBalance(accountName: String, amount: Double) {
        db.execSQL(
            "UPDATE Accounts SET Balance = Balance + ? WHERE Name = ?",
            arrayOf<Any>(amount, accountName)
        )
    }

    companion object {
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
    }
}
