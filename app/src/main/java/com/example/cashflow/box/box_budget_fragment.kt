package com.example.cashflow.box

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.cashflow.R
import com.example.cashflow.dataClass.Budget
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlin.collections.isNullOrEmpty

import com.example.cashflow.db.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class box_budget_fragment(private val readSQL: ReadSQL, private val writeSQL: WriteSQL) :
    Fragment() {
    private var horizontalBarChart1: HorizontalBarChart? = null
    private var horizontalBarChart2: HorizontalBarChart? = null
    private var horizontalBarChart3: HorizontalBarChart? = null
    private var noDataTextView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate il layout per questo fragment
        val view = inflater.inflate(R.layout.box_fragment_budget, container, false)

        horizontalBarChart1 = view.findViewById(R.id.barraOrizzontale1)
        horizontalBarChart2 = view.findViewById(R.id.barraOrizzontale2)
        horizontalBarChart3 = view.findViewById(R.id.barraOrizzontale3)
        noDataTextView = view.findViewById(R.id.noDataTextView)

        setupChart(horizontalBarChart1)
        setupChart(horizontalBarChart2)
        setupChart(horizontalBarChart3)

        val budgetDataList = readSQL.getBudgetData()
        if (budgetDataList.isEmpty()) {
            noDataTextView?.visibility = View.VISIBLE
            horizontalBarChart1?.visibility = View.GONE
            horizontalBarChart2?.visibility = View.GONE
            horizontalBarChart3?.visibility = View.GONE
        } else {
            noDataTextView?.visibility = View.GONE
            horizontalBarChart1?.visibility = View.VISIBLE
            horizontalBarChart2?.visibility = View.VISIBLE
            horizontalBarChart3?.visibility = View.VISIBLE
            createBarCharts(budgetDataList)
        }

        return view
    }

    private fun setupChart(chart: HorizontalBarChart?) {
        chart?.description?.isEnabled = false // Disable chart description
        chart?.setDrawGridBackground(false) // Disable grid background
        chart?.setTouchEnabled(true) // Enable touch gestures
        chart?.isDragEnabled = true // Enable drag gestures
        chart?.setScaleEnabled(true) // Enable scaling and dragging
        chart?.setPinchZoom(true) // Enable pinch zoom
    }

    private fun createBarCharts(budgetDataList: List<Budget>) {
        val chartList = listOf(
            horizontalBarChart1,
            horizontalBarChart2,
            horizontalBarChart3,
        )

        for ((index, budgetData) in budgetDataList.withIndex()) {
            if (index < chartList.size) {
                val chart = chartList[index]
                val entries = ArrayList<BarEntry>()

                val transactionsSum = readSQL.getTransactionsSumForCategory(budgetData.categoryId)
                val percentage = transactionsSum / budgetData.amount * 100

                entries.add(BarEntry(index.toFloat(), transactionsSum))

                val dataSet = BarDataSet(
                    entries,
                    budgetData.name
                ) // Usa il nome del Budget come label per il dataSet.
                if (percentage > 100) {
                    dataSet.setColor(Color.RED)
                } else {
                    dataSet.setColor(Color.GREEN)
                }
                dataSet.setValueTextColor(Color.BLACK)
                dataSet.setDrawValues(false) // Continua a non mostrare i valori sopra le barre.
                dataSet.setDrawIcons(false)

                val barData = BarData(dataSet)
                chart?.data = barData
                chart?.setFitBars(true)

                // Configura la legenda
                val legend = chart?.legend
                legend?.isEnabled = true // Abilita la legenda
                // Qui puoi aggiungere altre configurazioni per la legenda, se necessario

                chart?.invalidate() // Aggiorna il grafico
            }
        }
    }

}