package com.example.cashflow.fragments.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cashflow.ModifyActivity
import com.example.cashflow.R
import com.example.cashflow.dataClass.Budget
import com.example.cashflow.db.DataViewModel
import com.example.cashflow.db.*
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate

class ViewBudgetFragment() : Fragment() {
    private var noDataTextView: TextView? = null
    private lateinit var chartsContainer: LinearLayout

    private val viewModel: DataViewModel by viewModels()
    private var readSQL: ReadSQL? = null
    private var writeSQL: WriteSQL? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_budget, container, false)

        noDataTextView = view.findViewById(R.id.noDataTextView)
        chartsContainer = view.findViewById(R.id.chartsContainer)

        readSQL = viewModel.getReadSQL()
        writeSQL = viewModel.getWriteSQL()

        loadBudgetCharts()

        return view
    }

    private fun loadBudgetCharts() {
        val budgets = readSQL!!.getBudgetData()
        if (budgets.isEmpty()) {
            noDataTextView?.visibility = View.VISIBLE
        } else {
            noDataTextView?.visibility = View.GONE
            budgets.forEach { budget ->
                // Nuovo LinearLayout per il grafico e il pulsante
                val chartAndButtonContainer = LinearLayout(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    orientation = LinearLayout.HORIZONTAL
                }

                val chart = HorizontalBarChart(context).apply {
                    layoutParams =
                        LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                            .also {
                                it.setMargins(0, 10, 0, 10)
                            }
                    // Configurazione del grafico
                }
                setupChart(chart, budget)

                // Pulsante di fianco al grafico
                val button = Button(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        it.gravity = Gravity.CENTER_VERTICAL
                    }
                    text = "MODIFICA"
                    setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.green_light_background
                        )
                    )
                    setTextColor(Color.WHITE)
                }
                button.setOnClickListener {
                    val intent = Intent(context, ModifyActivity::class.java)
                    intent.putExtra("FRAGMENT_ID", 4)
                    intent.putExtra("BUDGET_ID", budget.id)
                    context?.startActivity(intent)
                }

                // Aggiunta del grafico e del pulsante al container
                chartAndButtonContainer.addView(chart)
                chartAndButtonContainer.addView(button)

                // Aggiunta del container al layout principale
                chartsContainer.addView(chartAndButtonContainer)
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
