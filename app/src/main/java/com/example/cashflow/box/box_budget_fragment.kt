package com.example.cashflow.box

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.cashflow.R
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

import com.example.cashflow.dataClass.*
import com.example.cashflow.db.*

class box_budget_fragment : Fragment() {
    private var horizontalBarChart: HorizontalBarChart? = null
    private var noDataTextView: TextView? = null
    private lateinit var db: SQLiteDB
    private lateinit var readSQL: readSQL
    private lateinit var writeSQL: writeSQL

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate il layout per questo fragment
        val view = inflater.inflate(R.layout.box_fragment_budget, container, false)
        horizontalBarChart = view.findViewById(R.id.barraOrizzontale)
        noDataTextView = view.findViewById(R.id.noDataTextView)

        db = SQLiteDB(requireContext())
        readSQL = readSQL(db.readableDatabase)

        setupChart()

        val budgetData = readSQL.getBudgetData()
        if (budgetData == null ) {
            noDataTextView?.visibility = View.VISIBLE
            horizontalBarChart?.visibility = View.GONE
        } else {
            noDataTextView?.visibility = View.GONE
            horizontalBarChart?.visibility = View.VISIBLE
            setupChart()
        }

            return view
    }

    private fun setupChart() {
        // Dati fittizi per dimostrazione
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, 50f)) // Esempio: valore corrente è 50
        val maxVal = 48f // Esempio: valore massimo raggiungibile è 100
        val dataSet = BarDataSet(entries, "Label")
        dataSet.setColor(Color.GREEN) // Colore iniziale delle barre
        dataSet.setValueTextColor(Color.BLACK) // Colore del testo dei valori

        // Controllo per cambiare il colore se il valore sfora il massimo
        if (entries[0].y > maxVal) {
            dataSet.setColor(Color.RED) // Cambia il colore in rosso se sforato
        }
        val barData = BarData(dataSet)
        horizontalBarChart!!.setData(barData)
        horizontalBarChart!!.setFitBars(true) // rende le barre dell'istogramma adattarsi
        horizontalBarChart!!.description.isEnabled = false // Disabilita la descrizione del grafico
        horizontalBarChart!!.invalidate() // refresh del grafico
    }
}
