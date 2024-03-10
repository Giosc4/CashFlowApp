package com.example.cashflow.box

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflow.R
import com.example.cashflow.dataClass.Transactions

class box_list_credito_fragment : Fragment() {
    var gridLayout: GridLayout? = null
    var textViewTitle: TextView? = null
    private val transactionsRecyclerView: RecyclerView? = null

    // Lista di transazioni debit
    private val creditoTransactions: List<Transactions>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.box_fragment_credito_debito, container, false)
        gridLayout = view.findViewById(R.id.gridLayout)
        textViewTitle = view.findViewById(R.id.textViewTitle)
        textViewTitle?.setText("Credito (da ricevere)")
        addTransactionList(view)
        return view
    }

    private fun addTransactionList(view: View) {
        val gridLayout = view.findViewById<ViewGroup>(R.id.gridLayout)

        // Dati fittizi delle transazioni
        val transactions = arrayOf(
            arrayOf("Caffè", "€2"),
            arrayOf("Libro", "€15"),
            arrayOf("Biglietto cinema", "€8"),
            arrayOf("Abbonamento palestra", "€30")
        )
        for (i in transactions.indices) {
            for (j in transactions[i].indices) {
                val textView = TextView(context)
                textView.text = transactions[i][j]
//                textView.setPadding(10, 10, 10, 10)
                textView.setGravity(Gravity.CENTER)
                val params = GridLayout.LayoutParams()
                params.rowSpec = GridLayout.spec(i)
                params.columnSpec = GridLayout.spec(j, 1f)
                textView.setLayoutParams(params)

                // Alternare il colore di sfondo per le righe
                if (i % 2 == 0) {
                    textView.setBackgroundColor(Color.parseColor("#7ad95f"))
                } else {
                    textView.setBackgroundColor(Color.parseColor("#e9F2ef"))
                }
                gridLayout.addView(textView)
            }
        }
    }
}