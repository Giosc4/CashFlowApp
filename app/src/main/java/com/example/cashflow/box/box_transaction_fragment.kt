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
import com.example.cashflow.R

import com.example.cashflow.dataClass.*
import com.example.cashflow.db.*

class box_transaction_fragment : Fragment() {
    private fun addTransactionList(view: View) {
        val gridLayout = view.findViewById<GridLayout>(R.id.gridLayout)
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.box_fragment_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addTransactionList(view)
    }
}
