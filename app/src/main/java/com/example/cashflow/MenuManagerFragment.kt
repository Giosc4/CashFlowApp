package com.example.cashflow

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.*
import android.widget.Toast

import java.io.*
import java.text.SimpleDateFormat
import java.util.*

import com.example.cashflow.dataClass.*
import com.example.cashflow.db.*
import com.example.cashflow.fragments.create.*
import com.example.cashflow.fragments.statistics.*
import com.example.cashflow.fragments.view.ViewTransactionsFragment

class MenuManagerFragment() : Fragment() {

    private lateinit var db: SQLiteDB
    private lateinit var readSQL: ReadSQL
    private lateinit var writeSQL: WriteSQL
    private var selectedMenuId: Int = 0
    private lateinit var city: City

    companion object {
        fun newInstance(selectedMenuId: Int, city: City) = MenuManagerFragment().apply {
            arguments = Bundle().apply {
                putInt("selectedMenuId", selectedMenuId)
                putSerializable("city", city)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            city = it.getSerializable("city") as City
            selectedMenuId = it.getInt("selectedMenuId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.menu_manager_fragment, container, false)

        db = SQLiteDB(requireContext())
        readSQL = ReadSQL(db.writableDatabase)
        writeSQL = WriteSQL(db.writableDatabase)

        loadFragmentBasedOnMenuId()
        return view
    }

    private fun loadFragmentBasedOnMenuId() {
        Log.d("MenuManagerFragment", "Caricamento fragment basato sul menuId $selectedMenuId")
        val fragment = when (selectedMenuId) {

            R.id.nav_home -> {
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                null
            }

            R.id.new_conto -> NewAccountFragment(readSQL, writeSQL)
            R.id.new_transaction -> NewTransactionFragment(readSQL, writeSQL, city)
            R.id.new_budget -> NewBudgetFragment(readSQL, writeSQL)
            R.id.new_debit_credit -> NewDebitCreditFragment(readSQL, writeSQL)
            R.id.line_chart -> Line_chart(readSQL, writeSQL)
            R.id.maps -> MapFragment(readSQL, writeSQL)
            R.id.income_chart -> IncomeExpenseFragment(true, readSQL)
            R.id.expense_chart -> IncomeExpenseFragment(false, readSQL)
            R.id.new_category -> NewCategoryFragment(readSQL, writeSQL)
            R.id.save_csv -> {
                saveToCSV()
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                null
            }
            R.id.list_category -> ViewTransactionsFragment(readSQL, writeSQL)


            else -> {
                Log.d(
                    "MenuManagerFragment",
                    "Nessun caso corrispondente trovato per $selectedMenuId"
                )
                null
            }

        }
        Log.d("MenuManagerFragment", "Caricamento fragment city ${city.nameCity}")

        fragment?.let {
            childFragmentManager.beginTransaction().apply {
                replace(R.id.linearContainer, it)
                commit()
            }
        } ?: Log.d("MenuManagerFragment", "Fragment è null")
    }


    fun saveToCSV(): Boolean {
        val accounts = readSQL.getAccounts()

        // Check if external storage is available and writable
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED != state) {
            return false
        }

        // Get the directory path for the "Download" folder
        val downloadDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadDirectory.exists() && !downloadDirectory.mkdirs()) {
            throw IOException("Cannot create 'Download' directory")
        }

        // Generate a unique file name using the current timestamp
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(Date())
        val fileName = "CashFlowApp_$timestamp.csv"

        // Specify the file path
        val file = File(downloadDirectory, fileName)
        try {
            FileWriter(file).use { writer ->
                // Write CSV header
                writer.write("Name, Balance, Transaction Type, Amount, Date, City, Category\n")

                // Write account data to the file
                for (account in accounts) {
                    val accountName = account.name
                    val accountBalance = account.balance
                    val transactions = readSQL.getTransactionsByAccountId(account.id)
                    for (transaction in transactions) {
                        val transactionType = if (transaction.isIncome) "INCOME" else "EXPENSE"
                        val transactionAmount = transaction.amountValue
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        val transactionDate = dateFormat.format(transaction.date.time)
                        val city = readSQL.getCityFromID(transaction.cityId)
                        val cityName = city?.nameCity ?: "Unknown"
                        val category = readSQL.getCategoryFromID(transaction.categoryId)
                        val categoryName = category?.name ?: "Unknown"
                        val csvData = String.format(
                            "%s,%.2f,%s,%.2f,%s,%s,%s\n",
                            accountName,
                            accountBalance,
                            transactionType,
                            transactionAmount,
                            transactionDate,
                            cityName,
                            categoryName
                        )
                        writer.write(csvData)
                    }
                }
            }
            Toast.makeText(
                requireContext(),
                "File CSV salvato con successo",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: IOException) {
            // Handle the exception
            e.printStackTrace()
            return false
        }
        return true
    }
}
