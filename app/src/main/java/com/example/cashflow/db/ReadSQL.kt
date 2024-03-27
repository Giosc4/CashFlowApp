package com.example.cashflow.db

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.cashflow.dataClass.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReadSQL(private val db: SQLiteDatabase?) {

    fun getCityIdByName(cityName: String): Int {
        var cityId = -1 // Default value if city is not found
        db?.let { database ->
            val cursor = database.query(
                TABLE_CITY, // The table to query
                arrayOf(COLUMN_ID), // The columns to return
                "$COLUMN_CITY_NAME = ?", // The columns for the WHERE clause
                arrayOf(cityName), // The values for the WHERE clause
                null, // group by
                null, // having
                null // order by
            )
            if (cursor.moveToFirst()) {
                val idIndex = cursor.getColumnIndex(COLUMN_ID)
                if (idIndex != -1) {
                    cityId = cursor.getInt(idIndex)
                }
            }
            cursor.close()
        }
        return cityId
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

    fun getAllTemplateTransactions(): List<TemplateTransaction> {
        val templateTransactionsList = mutableListOf<TemplateTransaction>()
        db?.let { database ->
            val cursor = database.query(
                TABLE_TEMPLATE_TRANSACTIONS, // The table to query
                null, // Passing null will return all columns
                null, // No WHERE clause, returning all rows
                null, // No WHERE clause values
                null, // No GROUP BY
                null, // No HAVING
                null  // No ORDER BY
            )

            while (cursor.moveToNext()) {
                val idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID)
                val nameIndex = cursor.getColumnIndexOrThrow(COLUMN_NAME)
                val incomeIndex = cursor.getColumnIndexOrThrow(COLUMN_INCOME)
                val amountIndex = cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)
                val categoryIdIndex = cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)
                val accountIdIndex = cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT_ID)

                val id = cursor.getInt(idIndex)
                val name = cursor.getString(nameIndex)
                val income = cursor.getInt(incomeIndex) > 0 // Convert to Boolean
                val amount = cursor.getDouble(amountIndex)
                val categoryId = cursor.getInt(categoryIdIndex)
                val accountId = cursor.getInt(accountIdIndex)

                templateTransactionsList.add(
                    TemplateTransaction(
                        id = id,
                        name = name,
                        income = income,
                        amount = amount,
                        category_id = categoryId,
                        account_id = accountId
                    )
                )
            }
            cursor.close()
        }
        return templateTransactionsList
    }


    fun getAllTransactions(): ArrayList<Transactions> {
        val transactionsList = ArrayList<Transactions>()
        val cursor = db?.query(
            TABLE_TRANSACTIONS,
            null, // null indica che vogliamo tutte le colonne
            null, // nessuna clausola WHERE, quindi restituisce tutte le righe
            null,
            null,
            null,
            null
        )
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        while (cursor != null && cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val income = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INCOME)) > 0
            val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
            val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE))
            val date = Calendar.getInstance()
            date.timeInMillis = timestamp

            val cityId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CITY_ID))
            val categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID))
            val accountId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT_ID))

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

        cursor?.close()
        return transactionsList
    }

    fun getAllDebits(): List<Debito> {
        val debitsList = mutableListOf<Debito>()

        // Assicurati che il database non sia nullo
        db?.let { database ->
            // Esegue la query per selezionare tutti i debiti
            val cursor = database.query(
                TABLE_DEBITO,
                null, // Seleziona tutte le colonne
                null, // Nessuna clausola WHERE, quindi seleziona tutte le righe
                null,
                null,
                null,
                null
            )

            // Itera su tutti i risultati della query e costruisce la lista di debiti
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val concessionDate =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONCESSION_DATE))
                val extinctionDate =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXTINCTION_DATE))
                val accountId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT_ID))

                // Crea un nuovo oggetto Debito con i valori recuperati e aggiungilo alla lista
                debitsList.add(Debito(id, amount, name, concessionDate, extinctionDate, accountId))
            }

            // Chiudi il cursore dopo aver finito di utilizzarlo
            cursor.close()
        }

        // Restituisce la lista di debiti
        return debitsList
    }

    fun getBudgetData(categoryId: Int? = null): List<Budget> {
        val budgetList = mutableListOf<Budget>()

        db?.let { database ->
            // Costruisci la query basata sulla presenza o assenza di categoryId
            val selectionArgs = categoryId?.toString()?.let { arrayOf(it) }
            val selection = categoryId?.let { "$COLUMN_CATEGORY_ID = ?" }

            val cursor = database.query(
                TABLE_BUDGET,
                arrayOf(COLUMN_ID, COLUMN_CATEGORY_ID, COLUMN_AMOUNT, COLUMN_NAME),
                selection,
                selectionArgs,
                null,
                null,
                null
            )
            cursor.use { cur ->
                while (cur.moveToNext()) {
                    val id = cur.getInt(cur.getColumnIndexOrThrow(COLUMN_ID))
                    val categoryId = cur.getInt(cur.getColumnIndexOrThrow(COLUMN_CATEGORY_ID))
                    val amount = cur.getDouble(cur.getColumnIndexOrThrow(COLUMN_AMOUNT))
                    val name = cur.getString(cur.getColumnIndexOrThrow(COLUMN_NAME))
                    budgetList.add(Budget(id, categoryId, amount, name))
                }
            }
        }
        return budgetList
    }

    fun getCategoryAmountCategory(categoryId: Int): Double {
        var amountCategory = 0.0 // Valore di default se non trovato

        db?.let { database ->
            val cursor = database.query(
                TABLE_CATEGORY, // La tabella da interrogare
                arrayOf("amount_category"), // La colonna da restituire
                "$COLUMN_ID = ?", // La clausola WHERE
                arrayOf(categoryId.toString()), // I valori per la clausola WHERE
                null, // group by
                null, // having
                null // order by
            )
            if (cursor.moveToFirst()) {
                val amountCategoryIndex = cursor.getColumnIndex("amount_category")
                if (amountCategoryIndex != -1) {
                    amountCategory = cursor.getDouble(amountCategoryIndex)
                }
            }
            cursor.close()
        }
        return amountCategory
    }


    fun getAllCredits(): List<Credito> {
        val creditsList = mutableListOf<Credito>()

        // Assicurati che il database non sia nullo
        db?.let { database ->
            // Esegue la query per selezionare tutti i debiti
            val cursor = database.query(
                TABLE_CREDITO,
                null, // Seleziona tutte le colonne
                null, // Nessuna clausola WHERE, quindi seleziona tutte le righe
                null,
                null,
                null,
                null
            )

            // Itera su tutti i risultati della query e costruisce la lista di debiti
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val concessionDate =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONCESSION_DATE))
                val extinctionDate =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXTINCTION_DATE))
                val accountId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT_ID))

                // Crea un nuovo oggetto Debito con i valori recuperati e aggiungilo alla lista
                creditsList.add(
                    Credito(
                        id,
                        amount,
                        name,
                        concessionDate,
                        extinctionDate,
                        accountId
                    )
                )
            }

            // Chiudi il cursore dopo aver finito di utilizzarlo
            cursor.close()
        }

        // Restituisce la lista di debiti
        return creditsList
    }

    fun getCategoryById(categoryId: Int): Category? {
        val cursor = db!!.query(
            TABLE_CATEGORY,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_AMOUNT_CATEGORY , COLUMN_DESCRIPTION),
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
            val amountCategoryIndex = cursor.getColumnIndex(COLUMN_AMOUNT_CATEGORY)
            if (idIndex != -1 && nameIndex != -1 && descriptionIndex != -1) {
                val id = cursor.getInt(idIndex)
                val name = cursor.getString(nameIndex)
                val description =
                    if (cursor.isNull(descriptionIndex)) null else cursor.getString(descriptionIndex)
                val amountCategory = cursor.getDouble(amountCategoryIndex)
                cursor.close()
                return Category(id, name, description, amountCategory)
            }
        }
        Log.d("getCategoryById", "Searching for categoryId: $categoryId")
        if (cursor.moveToFirst()) {
            // Dopo aver trovato la categoria, logga il nome trovato
            Log.d("getCategoryById", "Category found: $categoryId.name ")
        } else {
            Log.d("getCategoryById", "Category not found for ID: $categoryId")
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
                arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_AMOUNT_CATEGORY, COLUMN_DESCRIPTION), // Colonne da restituire
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
                val amountCategoryIndex = cursor.getColumnIndex(COLUMN_AMOUNT_CATEGORY)
                if (idIndex != -1 && nameIndex != -1 && descriptionIndex != -1) {
                    val id = cursor.getInt(idIndex)
                    val name = cursor.getString(nameIndex)
                    val description = cursor.getString(descriptionIndex)
                    val amountCategory = cursor.getDouble(amountCategoryIndex)
                    categories.add(Category(id, name, description, amountCategory))
                    Log.d("getCategories", "Category amountCategory: $amountCategory")
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
                arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_AMOUNT_CATEGORY ,COLUMN_DESCRIPTION),
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
                    val amountCategoryIndex = cursor.getColumnIndex(COLUMN_AMOUNT_CATEGORY)
                    if (idIndex != -1 && nameIndex != -1 && descriptionIndex != -1) {
                        val id = cursor.getInt(idIndex)
                        val name = cursor.getString(nameIndex)
                        val description = cursor.getString(descriptionIndex)
                        val amountCategory = cursor.getDouble(amountCategoryIndex)
                        return Category(id, name, description, amountCategory)
                    }
                }
            }
        }
        return null
    }

    fun getAccountByTransactionId(transactionId: Int): Account? {
        // First, fetch the transaction to get the accountId
        val transaction = getTransactionById(transactionId) ?: return null
        val accountId = transaction.accountId

        // Now fetch the account by its ID
        return getAccountById(accountId)
    }

    fun getTransactionById(transactionId: Int): Transactions? {
        val cursor = db?.query(
            TABLE_TRANSACTIONS,
            null, // Fetching all columns
            "$COLUMN_ID = ?", // Where clause
            arrayOf(transactionId.toString()), // Where parameters
            null, null, null
        )

        return cursor?.use {
            if (it.moveToFirst()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID))
                val income = it.getInt(it.getColumnIndexOrThrow(COLUMN_INCOME)) > 0
                val amount = it.getDouble(it.getColumnIndexOrThrow(COLUMN_AMOUNT))
                val dateMillis = it.getLong(it.getColumnIndexOrThrow(COLUMN_DATE))
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = dateMillis
                }
                val cityId = it.getInt(it.getColumnIndexOrThrow(COLUMN_CITY_ID))
                val categoryId = it.getInt(it.getColumnIndexOrThrow(COLUMN_CATEGORY_ID))
                val accountId = it.getInt(it.getColumnIndexOrThrow(COLUMN_ACCOUNT_ID))

                Transactions(id, income, amount, calendar, cityId, categoryId, accountId)
            } else null
        }
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
                val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                val date = Calendar.getInstance()
                date.timeInMillis = timestamp

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
        var categoryId = -1
        db?.let { database ->
            val cursor = database.query(
                "Category",
                arrayOf("id"),
                "name = ?", arrayOf(categoryName),
                null, null, null
            )
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex("id")
                if (columnIndex != -1) {
                    categoryId = cursor.getInt(columnIndex)
                }
            }
            cursor.close()
        }
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

    fun getTransactionsSumForCategory(categoryId: Int): Float {
        var sum = 0.0f

        val transactions = getTransactionsByCategoryId(categoryId)
        for (transaction in transactions) {
            sum += if (transaction.isIncome) transaction.amountValue.toFloat() else -transaction.amountValue.toFloat()
        }

        return sum
    }


    fun getTransactionsByCategoryId(categoryId: Int): List<Transactions> {
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
            "$COLUMN_CATEGORY_ID = ?",
            arrayOf(categoryId.toString()),
            null, null, null
        )
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        while (cursor != null && cursor.moveToNext()) {
            val idIndex = cursor.getColumnIndex(COLUMN_ID)
            val incomeIndex = cursor.getColumnIndex(COLUMN_INCOME)
            val amountIndex = cursor.getColumnIndex(COLUMN_AMOUNT)
            val dateIndex = cursor.getColumnIndex(COLUMN_DATE)
            val cityIdIndex = cursor.getColumnIndex(COLUMN_CITY_ID)
            val accountIdIndex = cursor.getColumnIndex(COLUMN_ACCOUNT_ID)

            if (idIndex != -1 && incomeIndex != -1 && amountIndex != -1 && dateIndex != -1 && cityIdIndex != -1 && accountIdIndex != -1) {
                val id = cursor.getInt(idIndex)
                val income = cursor.getInt(incomeIndex) > 0
                val amount = cursor.getDouble(amountIndex)
                val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                val date = Calendar.getInstance()
                date.timeInMillis = timestamp

                val cityId = cursor.getInt(cityIdIndex)
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


    fun getTransactionsByAccountIdAndCategory(
        accountId: Int,
        categoryId: Int,
        income: Boolean
    ): List<Transactions> {
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
        private const val COLUMN_AMOUNT_CATEGORY = "amount_category"

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
