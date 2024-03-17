package com.example.cashflow.statistics

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflow.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.example.cashflow.dataClass.*
import com.example.cashflow.db.*


class Income_expense(private val isIncome: Boolean, private val readSQL: ReadSQL, private val writeSQL: WriteSQL)  :
    Fragment() {
    private var title: TextView? = null
    private var accountsCheckBox: CheckBox? = null
    private var accountsRecyclerView: RecyclerView? = null
    private var accountsAdapter: AccountsAdapter? = null
    private var noDataTextView: TextView? = null
    private val selectedAccounts: ArrayList<Account>
    private var pieChart: PieChart? = null
    private var barChart: BarChart? = null
    
    private val accounts: ArrayList<Account> = readSQL.getAccounts()

    init {
        selectedAccounts = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_income_expense, container, false)
        accountsCheckBox = view.findViewById(R.id.accountsCheckBox)
        accountsRecyclerView = view.findViewById(R.id.accountsRecyclerView)
        noDataTextView = view.findViewById(R.id.noDataTextView)

        pieChart = view.findViewById(R.id.pieChart)
        barChart = view.findViewById(R.id.barChart)

        title = view.findViewById(R.id.title)
        if (isIncome) {
            title?.setText("INCOME")
        } else {
            title?.setText("EXPENSE")
        }

        val accountNames = ArrayList<String>()
        for (account in accounts) {
            account.name?.let { accountNames.add(it) }
        }

        val localNoDataTextView = noDataTextView
        if (hasData(selectedAccounts)) {
            pieChart?.visibility = View.VISIBLE
            barChart?.visibility = View.VISIBLE
            accountsRecyclerView?.visibility = View.VISIBLE
            accountsCheckBox?.visibility = View.VISIBLE
            localNoDataTextView?.visibility = View.GONE
            initPieChart(selectedAccounts)
            initBarChart(selectedAccounts)
        } else {
            pieChart?.visibility = View.GONE
            barChart?.visibility = View.GONE
            accountsRecyclerView?.visibility = View.GONE
            accountsCheckBox?.visibility = View.GONE
            localNoDataTextView?.visibility = View.VISIBLE
            val noDataMessage = "Non ci sono dati disponibili."
            Log.d(
                "IncomeExpenseFragment",
                noDataMessage
            )
        }
        return view
    }

    private fun updateSelectedAccounts() {
        selectedAccounts.clear()
        for (i in 0 until accountsAdapter!!.itemCount) {
            if (accountsAdapter!!.isSelected(i)) {
                selectedAccounts.add(accounts[i])
            }
        }
    }

    private fun hasData(selectedAccounts: ArrayList<Account>): Boolean {
        for (account in selectedAccounts) {
            if (isIncome) {
                if (readSQL.getIncomeTransactionsByAccount(account.id).isNotEmpty()) return true
            } else {
                if (readSQL.getExpenseTransactionsByAccount(account.id).isNotEmpty()) return true
            }
        }
        return false
    }

    private fun initPieChart(selectedAccounts: ArrayList<Account>) {
        pieChart!!.description.isEnabled = false
        pieChart!!.isDrawHoleEnabled = true
        pieChart!!.setHoleColor(android.R.color.transparent)
        pieChart!!.transparentCircleRadius = 50f
        pieChart!!.isRotationEnabled = true
        pieChart!!.extraTopOffset = 30f
        pieChart!!.setUsePercentValues(true)
        val pieDataSet = PieDataSet(getIncomeOrExpensePieData(selectedAccounts), "")
        pieDataSet.setDrawValues(false)

        // Configura la legenda
        val legend = pieChart!!.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.isWordWrapEnabled = true
        val pieData = PieData(pieDataSet)
        pieChart!!.setData(pieData)
    }

    // Inizializza il grafico a barre
    private fun initBarChart(selectedAccounts: ArrayList<Account>) {
        barChart!!.description.isEnabled = false
        barChart!!.setDrawBarShadow(false)
        barChart!!.setDrawValueAboveBar(true)
        barChart!!.legend.isEnabled = false
        val barDataSet = BarDataSet(getIncomeOrExpenseBarData(selectedAccounts), "")
        val barData = BarData(barDataSet)
        barChart!!.setData(barData)
    }

    fun getIncomeOrExpensePieData(accounts: ArrayList<Account>): List<PieEntry> {
        val entries: MutableList<PieEntry> = ArrayList()

        // Recupera tutte le categorie dal database
        val categories = readSQL.getCategories()
        for (category in categories) {
            var totalAmount = 0f

            // Calcola il totale per categoria attraverso tutti gli account selezionati
            for (account in accounts) {
                val transactions =
                    readSQL.getTransactionsByAccountIdAndCategory(account.id, category.id, isIncome)
                for (transaction in transactions) {
                    totalAmount += transaction.amountValue.toFloat()
                }
            }

            if (totalAmount > 0) {
                entries.add(PieEntry(totalAmount, category.name))
            }
        }
        return entries
    }


    fun getIncomeOrExpenseBarData(accounts: ArrayList<Account>): List<BarEntry> {
        val entries: MutableList<BarEntry> = ArrayList()

        val categories = readSQL.getCategories()
        for (i in categories.indices) {
            var totalAmount = 0f

            for (account in accounts) {
                val transactions = readSQL.getTransactionsByAccountIdAndCategory(
                    account.id,
                    categories[i].id,
                    isIncome
                )
                for (transaction in transactions) {
                    totalAmount += transaction.amountValue.toFloat()
                }
            }

            if (totalAmount > 0) {
                entries.add(BarEntry(i.toFloat(), totalAmount))
            }
        }
        return entries
    }

}
