package com.example.cashflow.box

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.cashflow.DetailsActivity
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
    private var viewBudgetBtn: Button? = null

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
        viewBudgetBtn = view.findViewById(R.id.viewBudgetBtn)

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

        viewBudgetBtn?.setOnClickListener {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("FRAGMENT_ID", 6)
            context?.startActivity(intent)
        }

        return view
    }

    private fun setupChart(chart: HorizontalBarChart?) {
        chart?.description?.isEnabled = false
        chart?.setDrawGridBackground(false)
        chart?.setTouchEnabled(true)
        chart?.isDragEnabled = true
        chart?.setScaleEnabled(true)
        chart?.setPinchZoom(true)
    }


    private fun createBarCharts(budgetDataList: List<Budget>) {
        val chartList = listOf(horizontalBarChart1, horizontalBarChart2, horizontalBarChart3)

        for ((index, budgetData) in budgetDataList.withIndex()) {
            if (index < chartList.size) {
                val chart = chartList[index]
                val entries = ArrayList<BarEntry>()

                // Assume che readSQL.getCategoryAmountCategory(categoryId: Int) sia un metodo che
                // restituisce l'amountCategory per la data categoria.
                // Devi implementare questo metodo nel tuo ReadSQL se non esiste già.
                val amountCategory = readSQL.getCategoryAmountCategory(budgetData.categoryId)

                // Usa l'indice del grafico come l'etichetta sull'asse X (potresti voler usare una etichetta più significativa)
                entries.add(BarEntry(index.toFloat(), amountCategory.toFloat()))

                val dataSet = BarDataSet(entries, budgetData.name)
                if (amountCategory > budgetData.amount) {
                    dataSet.setColor(Color.RED) // Spesa supera il budget
                } else {
                    dataSet.setColor(Color.GREEN) // Spesa entro il budget
                }
                dataSet.setValueTextColor(Color.BLACK)
                dataSet.setDrawValues(false)
                dataSet.setDrawIcons(false)

                val barData = BarData(dataSet)
                chart?.data = barData
                chart?.setFitBars(true)
                chart?.invalidate() // Aggiorna il grafico con i nuovi dati
            }
        }
    }

}