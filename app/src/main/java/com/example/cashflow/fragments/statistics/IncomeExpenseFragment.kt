package com.example.cashflow.fragments.statistics

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflow.R
import com.example.cashflow.dataClass.Account
import com.example.cashflow.db.DataViewModel
import com.example.cashflow.db.ReadSQL
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class IncomeExpenseFragment(
    private val isIncome: Boolean,
) : Fragment() {
    private var title: TextView? = null
    private var accountsCheckBox: CheckBox? = null
    private var accountsRecyclerView: RecyclerView? = null
    private var accountsAdapter: AccountsAdapter? = null
    private var pieChart: PieChart? = null
    private var barChart: BarChart? = null
    private lateinit var accounts: ArrayList<Account>

    private val viewModel: DataViewModel by viewModels()
    private val readSQL = viewModel.getReadSQL()
    private val writeSQL = viewModel.getWriteSQL()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_income_expense, container, false)
        initializeViews(view)
        setupTitle()
        setupAccountsCheckBox()
        loadAccounts()
        setupAccountsRecyclerView()
        initializeCharts()
        return view
    }

    private fun initializeViews(view: View) {
        title = view.findViewById(R.id.title)
        accountsCheckBox = view.findViewById(R.id.accountsCheckBox)
        accountsRecyclerView = view.findViewById(R.id.accountsRecyclerView)
        pieChart = view.findViewById(R.id.pieChart)
        barChart = view.findViewById(R.id.barChart)
    }

    private fun setupTitle() {
        title?.text = if (isIncome) "INCOME" else "EXPENSE"
    }

    private fun setupAccountsCheckBox() {
        accountsCheckBox?.setOnCheckedChangeListener { _, isChecked ->
            accountsAdapter?.selectAll(isChecked)
            updateCharts()
        }
    }

    private fun loadAccounts() {
        accounts = readSQL.getAccounts() // Implementa questo metodo nel tuo ReadSQL
    }

    private fun setupAccountsRecyclerView() {
        val accountNames = accounts.map { it.name ?: "" }
        accountsAdapter = AccountsAdapter(accountNames).apply {
            setOnItemClickListener {
                updateCharts()
            }
        }
        accountsRecyclerView?.adapter = accountsAdapter
        accountsRecyclerView?.layoutManager = LinearLayoutManager(context)
    }

    private fun initializeCharts() {
        accountsCheckBox?.isChecked = true
        updateCharts()
    }

    private fun updateCharts() {
        val selectedAccounts = accountsAdapter?.getSelectedAccounts(accounts) ?: accounts
        initPieChart(selectedAccounts)
        initBarChart(selectedAccounts)
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
        pieDataSet.setColors(colors.toList())
        pieDataSet.setDrawValues(false)

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
        barDataSet.setColors(colors.toList())

    }

    private val colors = arrayOf(
        Color.RED,
        Color.GREEN,
        Color.BLUE,
        Color.YELLOW,
        Color.CYAN,
        Color.MAGENTA,
        Color.LTGRAY,
        Color.DKGRAY,
        Color.BLACK,
        Color.WHITE
    )

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