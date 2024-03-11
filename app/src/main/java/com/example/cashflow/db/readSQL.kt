package com.example.cashflow.db

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.cashflow.dataClass.Account
import com.example.cashflow.dataClass.Category
import com.example.cashflow.dataClass.City
import com.example.cashflow.dataClass.Transactions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class readSQL(private val db: SQLiteDatabase?) {
    fun getCityNameById(cityId: Int): String? {
        var cityName: String? = null
        val cursor = db!!.query(
            "Cities", arrayOf("Name"),  // Colonna da restituire
            "ID = ?", arrayOf(cityId.toString()),  // Valori per la clausola WHERE
            null, null, null
        )
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex("Name")
            if (columnIndex != -1) {
                cityName = cursor.getString(columnIndex)
            } else {
                // Handle the case where the column doesn't exist in the cursor
            }
        }
        cursor.close()
        db.close()
        return cityName
    }

    fun getCategoryNameById(categoryId: Int): String? {
        var categoryName: String? = null
        val cursor = db!!.query(
            "Categories", arrayOf("Name"),  // Colonna da restituire
            "ID = ?", arrayOf(categoryId.toString()),  // Valori per la clausola WHERE
            null, null, null
        )
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex("Name")
            if (columnIndex != -1) {
                categoryName = cursor.getString(columnIndex)
            } else {
                // Handle the case where the column doesn't exist in the cursor
            }
        }
        cursor.close()
        db.close()
        return categoryName
    }


    fun getCityById(cityId: Int): City? {
        val cursor = db?.query(
            TABLE_CITY,
            arrayOf(COLUMN_ID, COLUMN_CITY_NAME, COLUMN_LATITUDE, COLUMN_LONGITUDE),
            "$COLUMN_ID = ?",
            arrayOf(cityId.toString()),
            null,
            null,
            null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                val idIndex = it.getColumnIndex(COLUMN_ID)
                val nameIndex = it.getColumnIndex(COLUMN_CITY_NAME)
                val latitudeIndex = it.getColumnIndex(COLUMN_LATITUDE)
                val longitudeIndex = it.getColumnIndex(COLUMN_LONGITUDE)

                if (idIndex != -1 && nameIndex != -1 && latitudeIndex != -1 && longitudeIndex != -1) {
                    val id = it.getInt(idIndex)
                    val name = it.getString(nameIndex)
                    val latitude = it.getDouble(latitudeIndex)
                    val longitude = it.getDouble(longitudeIndex)
                    return City(id, name, latitude, longitude)
                }
            }
        }
        return null
    }

    fun getCityByName(newCityName: String): City? {
        // Usa i placeholder '?' per evitare SQL Injection
        val cursor = db!!.rawQuery(
            "SELECT * FROM " + TABLE_CITY + " WHERE " + COLUMN_CITY_NAME + " = ?",
            arrayOf(newCityName)
        )
        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(COLUMN_ID)
            val cityNameIndex = cursor.getColumnIndex(COLUMN_CITY_NAME)
            val latitudeIndex = cursor.getColumnIndex(COLUMN_LATITUDE)
            val longitudeIndex = cursor.getColumnIndex(COLUMN_LONGITUDE)
            if (idIndex != -1 && cityNameIndex != -1 && latitudeIndex != -1 && longitudeIndex != -1) {
                val id = cursor.getInt(idIndex)
                val cityName = cursor.getString(cityNameIndex)
                val latitude = cursor.getDouble(latitudeIndex)
                val longitude = cursor.getDouble(longitudeIndex)
                cursor.close() // Chiudi il cursor prima del return
                return City(id, cityName, latitude, longitude)
            }
        }
        cursor.close() // Assicurati di chiudere il cursor anche se non ci sono risultati
        return null
    }

    fun getCategoryById(categoryId: Int): Category? {
        val cursor = db!!.query(
            TABLE_CATEGORY,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION),
            COLUMN_ID + " = ?",
            arrayOf(categoryId.toString()),
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(COLUMN_ID)
            val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
            val descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION)
            if (idIndex != -1 && nameIndex != -1 && descriptionIndex != -1) {
                val id = cursor.getInt(idIndex)
                val name = cursor.getString(nameIndex)
                val description =
                    if (cursor.isNull(descriptionIndex)) null else cursor.getString(descriptionIndex)
                cursor.close()
                return Category(id, name, description)
            }
        }
        cursor.close()
        return null
    }

    fun getAccounts(): ArrayList<Account> {
        val accounts = mutableListOf<Account>()
        db?.let { database ->
            val cursor = database.query(
                TABLE_ACCOUNT, // Il nome della tabella degli account
                arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_BALANCE), // Le colonne da restituire
                null, // La clausola WHERE, null per restituire tutte le righe
                null, // I valori per la clausola WHERE
                null, // groupBy
                null, // having
                null  // orderBy
            )
            while (cursor.moveToNext()) {
                val idIndex = cursor.getColumnIndex(COLUMN_ID)
                val id = if (idIndex != -1) cursor.getInt(idIndex) else -1

                val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
                val name = if (nameIndex != -1) cursor.getString(nameIndex) else ""

                val balanceIndex = cursor.getColumnIndex(COLUMN_BALANCE)
                val balance = if (balanceIndex != -1) cursor.getDouble(balanceIndex) else 0.0
                accounts.add(Account(id, name, balance))
            }
            cursor.close()
        }
        return ArrayList(accounts)
    }

    fun getAccountById(accountId: Int): Account? {
        val cursor = db?.query(
            TABLE_ACCOUNT,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_BALANCE),
            "$COLUMN_ID = ?",
            arrayOf(accountId.toString()),
            null, null, null
        )
        var account: Account? = null
        if (cursor != null && cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(COLUMN_ID)
            val id = if (idIndex != -1) cursor.getInt(idIndex) else -1

            val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
            val name = if (nameIndex != -1) cursor.getString(nameIndex) else ""

            val balanceIndex = cursor.getColumnIndex(COLUMN_BALANCE)
            val balance = if (balanceIndex != -1) cursor.getDouble(balanceIndex) else 0.0
            account = Account(id, name, balance)
        }
        cursor?.close()
        return account
    }

    fun doesAccountExist(accountName: String): Boolean {
        val cursor = db?.query(
            TABLE_ACCOUNT, // Tabella da cercare
            arrayOf(COLUMN_ID), // Colonne da restituire, in questo caso solo l'ID per verificare l'esistenza
            "$COLUMN_NAME = ?", // Clausola WHERE
            arrayOf(accountName), // Valori per la clausola WHERE
            null, null, null
        )
        val exists = cursor?.moveToFirst() == true
        cursor?.close()
        return exists
    }

    fun getCityFromID(cityId: Int): City? {
        db?.let { database ->
            val cursor = database.query(
                TABLE_CITY,
                arrayOf(COLUMN_ID, COLUMN_CITY_NAME, COLUMN_LATITUDE, COLUMN_LONGITUDE),
                "$COLUMN_ID = ?",
                arrayOf(cityId.toString()),
                null,
                null,
                null
            )
            cursor.use {
                if (it.moveToFirst()) {
                    val idIndex = it.getColumnIndex(COLUMN_ID)
                    val nameIndex = it.getColumnIndex(COLUMN_CITY_NAME)
                    val latitudeIndex = it.getColumnIndex(COLUMN_LATITUDE)
                    val longitudeIndex = it.getColumnIndex(COLUMN_LONGITUDE)

                    if (idIndex != -1 && nameIndex != -1 && latitudeIndex != -1 && longitudeIndex != -1) {
                        val id = it.getInt(idIndex)
                        val name = it.getString(nameIndex)
                        val latitude = it.getDouble(latitudeIndex)
                        val longitude = it.getDouble(longitudeIndex)
                        return City(id, name, latitude, longitude)
                    }
                }
            }
        }
        return null
    }



    fun getCategories(): ArrayList<Category> {
        val categories: MutableList<Category> = mutableListOf()
        db?.let { database ->
            val cursor = database.query(
                TABLE_CATEGORY, // Tabella da cui selezionare
                arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION), // Colonne da restituire
                null, // Clausola WHERE, null per selezionare tutte le righe
                null, // Valori per la clausola WHERE
                null, // groupBy
                null, // having
                null  // orderBy
            )
            while (cursor.moveToNext()) {
                val idIndex = cursor.getColumnIndex(COLUMN_ID)
                val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
                val descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION)
                if (idIndex != -1 && nameIndex != -1 && descriptionIndex != -1) {
                    val id = cursor.getInt(idIndex)
                    val name = cursor.getString(nameIndex)
                    val description = cursor.getString(descriptionIndex)
                    categories.add(Category(id, name, description))
                }
            }
            cursor.close()
        }
        return ArrayList(categories)
    }

    fun getIdByCityName(cityName: String): Int {
        db?.let { database ->
            val cursor = database.query(
                TABLE_CITY,
                arrayOf(COLUMN_ID),
                "$COLUMN_CITY_NAME = ?",
                arrayOf(cityName),
                null,
                null,
                null,
                "1"
            )
            cursor.use {
                if (it.moveToFirst()) {
                    val idIndex = it.getColumnIndex(COLUMN_ID)
                    return if (idIndex != -1) {
                        it.getInt(idIndex)
                    } else {
                        // Handle the case where the column doesn't exist in the cursor
                        -1
                    }
                }
            }
        }
        return -1
    }

    fun getCategoryFromID(categoryId: Int): Category? {
        db?.let { database ->
            val cursor = database.query(
                TABLE_CATEGORY,
                arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION),
                "$COLUMN_ID = ?",
                arrayOf(categoryId.toString()),
                null,
                null,
                null
            )
            cursor.use {
                if (it.moveToFirst()) {

                    val idIndex = cursor.getColumnIndex(COLUMN_ID)
                    val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
                    val descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION)
                    if (idIndex != -1 && nameIndex != -1 && descriptionIndex != -1) {
                        val id = cursor.getInt(idIndex)
                        val name = cursor.getString(nameIndex)
                        val description = cursor.getString(descriptionIndex)
                        return Category(id, name, description)
                    }
                }
            }
        }
        return null
    }


    fun getTransactionsByAccountId(accountId: Int): ArrayList<Transactions> {
        val transactionsList = ArrayList<Transactions>()
        val cursor = db?.query(
            TABLE_TRANSACTIONS,
            arrayOf(
                COLUMN_ID,
                COLUMN_INCOME,
                COLUMN_AMOUNT,
                COLUMN_DATE,
                COLUMN_CITY_ID,
                COLUMN_CATEGORY_ID,
                COLUMN_ACCOUNT_ID
            ),
            "$COLUMN_ACCOUNT_ID = ?",
            arrayOf(accountId.toString()),
            null, null, null
        )
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        while (cursor != null && cursor.moveToNext()) {
            val idIndex = cursor.getColumnIndex(COLUMN_ID)
            val incomeIndex = cursor.getColumnIndex(COLUMN_INCOME)
            val amountIndex = cursor.getColumnIndex(COLUMN_AMOUNT)
            val dateIndex = cursor.getColumnIndex(COLUMN_DATE)
            val cityIdIndex = cursor.getColumnIndex(COLUMN_CITY_ID)
            val categoryIdIndex = cursor.getColumnIndex(COLUMN_CATEGORY_ID)
            val accountIdIndex = cursor.getColumnIndex(COLUMN_ACCOUNT_ID)

            if (idIndex != -1 && incomeIndex != -1 && amountIndex != -1 && dateIndex != -1 && cityIdIndex != -1 && categoryIdIndex != -1 && accountIdIndex != -1) {
                val id = cursor.getInt(idIndex)
                val income = cursor.getInt(incomeIndex) > 0
                val amount = cursor.getDouble(amountIndex)
                val dateString = cursor.getString(dateIndex)
                val date = Calendar.getInstance()
                sdf.parse(dateString)?.let {
                    date.time = it
                }
                val cityId = cursor.getInt(cityIdIndex)
                val categoryId = cursor.getInt(categoryIdIndex)
                val accountId = cursor.getInt(accountIdIndex)
                transactionsList.add(
                    Transactions(
                        id,
                        income,
                        amount,
                        date,
                        cityId,
                        categoryId,
                        accountId
                    )
                )
            }
        }


        cursor?.close()
        return transactionsList
    }

    fun getCategoryIdByName(categoryName: String): Int {
        var categoryId = -1 // Valore di default se la categoria non viene trovata
        val cursor = db!!.query(
            "Categories", arrayOf("ID"),  // Colonne da restituire
            "Name = ?", arrayOf(categoryName),  // Valori per la clausola WHERE
            null, null, null
        )
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex("ID")
            if (columnIndex >= 0) { // Controlla che l'indice della colonna sia valido
                categoryId = cursor.getInt(columnIndex)
            }
        }
        cursor.close()
        db.close()
        return categoryId
    }

    fun getIdByAccountName(accountName: String): Int {
        val cursor = db!!.query(
            TABLE_ACCOUNT,
            arrayOf(COLUMN_ID),
            COLUMN_NAME + "=?",
            arrayOf(accountName),
            null,
            null,
            null
        )
        return if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") val accountId =
                cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
            cursor.close()
            accountId
        } else {
            -1 // or any default value
        }
    }
    fun getTransactionsByAccountIdAndCategory(accountId: Int, categoryId: Int, income: Boolean): List<Transactions> {
        val transactionsList = ArrayList<Transactions>()
        val transactions = getTransactionsByAccountId(accountId)
        for (transaction in transactions) {
            // Verifica se la transazione appartiene alla categoria specificata e se corrisponde al tipo di transazione (entrata o spesa)
            if (transaction.categoryId == categoryId && transaction.isIncome == income) {
                transactionsList.add(transaction)
            }
        }
        return transactionsList
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