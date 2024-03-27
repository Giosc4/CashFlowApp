package com.example.cashflow.fragments.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.cashflow.R
import com.example.cashflow.dataClass.Budget
import com.example.cashflow.db.ReadSQL
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate

class ViewBudgetFragment(private val readSQL: ReadSQL) : Fragment() {
    private var noDataTextView: TextView? = null
    private lateinit var chartsContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_budget, container, false)

        noDataTextView = view.findViewById(R.id.noDataTextView)
        chartsContainer = view.findViewById(R.id.chartsContainer)

        loadBudgetCharts()

        return view
    }

    private fun loadBudgetCharts() {
        val budgets = readSQL.getBudgetData()
        if (budgets.isEmpty()) {
            noDataTextView?.visibility = View.VISIBLE
        } else {
            noDataTextView?.visibility = View.GONE
            budgets.forEach { budget ->
                val chart = HorizontalBarChart(context).apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                        it.setMargins(0, 10, 0, 10)
                    }
                    // Configurazione del grafico (puoi personalizzarla come preferisci)
                }
                setupChart(chart, budget)
                chartsContainer.addView(chart)
            }
        }
    }

    private fun setupChart(chart: HorizontalBarChart, budget: Budget) {
        val entries = ArrayList<BarEntry>()
        // Supponendo che la tua classe Budget abbia questi campi
        entries.add(BarEntry(0f, budget.amount.toFloat()))

        val dataSet = BarDataSet(entries, "Budget: ${budget.name}")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        val data = BarData(dataSet)
        chart.data = data

        chart.setFitBars(true)
        chart.description.isEnabled = false
        chart.invalidate() // refresh the chart
    }
}
