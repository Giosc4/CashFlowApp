package com.example.cashflow

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cashflow.dataClass.Account
import com.example.cashflow.statistics.Income_expense
import com.example.cashflow.statistics.Line_chart
import com.example.cashflow.statistics.MapFragment
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StatisticsFragment(private val accounts: ArrayList<Account>) : Fragment() {
    private var btnLineChart: Button? = null
    private var google_maps: Button? = null
    private var incomeButton: Button? = null
    private var expenseButton: Button? = null
    private var btnCSVFileDownload: Button? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)
        btnLineChart = view.findViewById(R.id.btnLineChart)
        google_maps = view.findViewById(R.id.google_maps)
        incomeButton = view.findViewById(R.id.incomeButton)
        expenseButton = view.findViewById(R.id.expenseButton)
        btnCSVFileDownload = view.findViewById(R.id.btnCSVFileDownload)
        btnLineChart?.setOnClickListener(View.OnClickListener {
            val barChartFragment = Line_chart(accounts)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.linearContainer, barChartFragment)
                .commit()
        })
        google_maps?.setOnClickListener(View.OnClickListener {
            val mapFragment = MapFragment(accounts)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.linearContainer, mapFragment)
                .commit()
        })
        incomeButton?.setOnClickListener(View.OnClickListener {
            val incomeExpense = Income_expense(true, accounts)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.linearContainer, incomeExpense)
                .commit()
        })
        expenseButton?.setOnClickListener(View.OnClickListener {
            val incomeExpense = Income_expense(false, accounts)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.linearContainer, incomeExpense)
                .commit()
        })
        btnCSVFileDownload?.setOnClickListener(View.OnClickListener { // Call the function to save the CSV file
            try {
                if (saveToCSV(accounts)) {
                    Toast.makeText(activity, "File CSV Salvato", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Inserisci il nome dell'account", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        })
        return view
    }

    @Throws(IOException::class)
    fun saveToCSV(accounts: ArrayList<Account>): Boolean {
        // Check if external storage is available and writable
        val state = Environment.getExternalStorageState()
        return if (Environment.MEDIA_MOUNTED == state) {
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
                        val accountBalance = account.getBalance()
                        for (transaction in account.listTrans) {
                            val transactionType = if (transaction.isIncome) "INCOME" else "EXPENSE"
                            val transactionAmount = transaction.amountValue
                            val transactionDate = transaction.date.time.toString()
                            val cityName = transaction.city.nameCity
                            val category = transaction.category.name
                            val csvData = String.format(
                                "%s,%.2f,%s,%.2f,%s,%s,%s\n",
                                accountName,
                                accountBalance,
                                transactionType,
                                transactionAmount,
                                transactionDate,
                                cityName,
                                category
                            )
                            writer.write(csvData)
                        }
                    }
                }
            } catch (e: IOException) {
                // Handle the exception
                e.printStackTrace()
            }
            true
        } else {
            false
        }
    }
}
