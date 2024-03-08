package com.example.cashflow.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflow.R
import com.example.cashflow.dataClass.Account
import com.example.cashflow.dataClass.CategoriesEnum
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class Income_expense(private val isIncome: Boolean, private val accounts: ArrayList<Account>) :
    Fragment() {
    private var title: TextView? = null
    private var accountsCheckBox: CheckBox? = null
    private var accountsRecyclerView: RecyclerView? = null
    private var accountsAdapter: AccountsAdapter? = null
    private val selectedAccounts: ArrayList<Account>
    private var pieChart: PieChart? = null
    private var barChart: BarChart? = null

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
        pieChart = view.findViewById(R.id.pieChart)
        barChart = view.findViewById(R.id.barChart)
        title = view.findViewById(R.id.title)
        if (isIncome) {
            title?.setText("INCOME")
        } else {
            title?.setText("EXPENSE")
        }
        accountsCheckBox?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            // Controlla lo stato del checkbox e aggiorna l'adapter di conseguenza
            for (i in 0 until accountsAdapter!!.itemCount) {
                accountsAdapter!!.setSelected(i, isChecked)
            }

            // Aggiorna i grafici
            updateSelectedAccounts()
            initPieChart(selectedAccounts)
            initBarChart(selectedAccounts)
        })

        // Popola l'array di nomi degli account
        val accountNames = ArrayList<String>()
        for (account in accounts) {
            accountNames.add(account.name)
        }

        // Inizializza l'adapter personalizzato per il RecyclerView
        accountsAdapter = AccountsAdapter(accountNames)
        accountsRecyclerView?.setAdapter(accountsAdapter)

        // Imposta un layout manager per il RecyclerView
        accountsRecyclerView?.setLayoutManager(LinearLayoutManager(context))
        accountsAdapter!!.setOnItemClickListener { position: Int ->
            // Aggiorna i grafici quando viene selezionato un elemento nel RecyclerView
            updateSelectedAccounts()
            initPieChart(selectedAccounts)
            initBarChart(selectedAccounts)
        }
        if (selectedAccounts.isEmpty()) {
            accountsCheckBox?.setChecked(true)
            initPieChart(accounts)
            initBarChart(accounts)
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

    private fun initPieChart(selectedAccounts: ArrayList<Account>) {
        pieChart!!.description.isEnabled = false
        pieChart!!.isDrawHoleEnabled = true
        pieChart!!.setHoleColor(android.R.color.transparent)
        pieChart!!.transparentCircleRadius = 50f
        pieChart!!.isRotationEnabled = true
        pieChart!!.extraTopOffset = 30f
        pieChart!!.setUsePercentValues(true)
        val pieDataSet = PieDataSet(getIncomeOrExpensePieData(selectedAccounts), "")
        pieDataSet.setColors(*categoryColors)
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
        val colors = categoryColors
        barDataSet.setColors(*colors)
        val barData = BarData(barDataSet)
        barChart!!.setData(barData)
    }

    fun getIncomeOrExpensePieData(accounts: ArrayList<Account>): List<PieEntry> {
        val entries: MutableList<PieEntry> = ArrayList()
        val categories = CategoriesEnum.entries.toTypedArray()
        for (category in categories) {
            var totalAmount = 0f
            for (account in accounts) {
                for (transaction in account.listTrans) {
                    if (transaction.isIncome === isIncome && transaction.category == category) {
                        totalAmount += transaction.amountValue.toFloat()
                    }
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
        val categories = CategoriesEnum.entries.toTypedArray()
        for (i in categories.indices) {
            var totalAmount = 0f
            for (account in accounts) {
                for (transaction in account.listTrans) {
                    if (transaction.isIncome === isIncome && transaction.category == categories[i]) {
                        totalAmount += transaction.amountValue.toFloat()
                    }
                }
            }
            if (totalAmount > 0) {
                entries.add(BarEntry(i.toFloat(), totalAmount, categories[i].name))
            }
        }
        return entries
    }

    private val categoryColors: IntArray
        private get() {
            val categories = CategoriesEnum.entries.toTypedArray()
            val colors = IntArray(categories.size)

            // Assegna un colore univoco a ciascuna categoria
            for (i in categories.indices) {
                when (categories[i]) {
                    CategoriesEnum.FoodAndDrinks -> colors[i] = Color.BLUE
                    CategoriesEnum.Shopping -> colors[i] = Color.GREEN
                    CategoriesEnum.House -> colors[i] = Color.RED
                    CategoriesEnum.Transport -> colors[i] = Color.YELLOW
                    CategoriesEnum.LifeAndEntertainment -> colors[i] = Color.MAGENTA
                    CategoriesEnum.CommunicationAndPC -> colors[i] = Color.CYAN
                    CategoriesEnum.Salary -> colors[i] = Color.LTGRAY
                    CategoriesEnum.Gifts -> colors[i] = Color.DKGRAY
                    CategoriesEnum.Other -> colors[i] = Color.BLACK
                }
            }
            return colors
        }
}
