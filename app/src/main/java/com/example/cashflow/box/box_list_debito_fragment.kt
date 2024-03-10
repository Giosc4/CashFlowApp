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
import com.example.cashflow.dataClass.Transactions
import java.util.Calendar

class box_list_debito_fragment : Fragment() {
    var gridLayout: GridLayout? = null
    var textViewTitle: TextView? = null
    private var debitTransactions: List<Transactions>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.box_fragment_credito_debito, container, false)
        gridLayout = view.findViewById(R.id.gridLayout)
        textViewTitle = view.findViewById(R.id.textViewTitle)
        textViewTitle?.setText("Debito (da dare)")

        // Popola la lista di transazioni debit
        debitTransactions = populateRecyclerViewWithExampleData()

        // Aggiungi le transazioni alla tabella GridLayout
        addTransactionList(view)
        return view
    }

    private fun addTransactionList(view: View) {

        // Dati di esempio delle transazioni di debito
        val transactions = arrayOf(
            arrayOf("Transazione 1", "€50.0"),
            arrayOf("Transazione 2", "€30.0"),
            arrayOf("Transazione 3", "€20.0")
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
                gridLayout!!.addView(textView)
            }
        }
    }

    private fun populateRecyclerViewWithExampleData(): List<Transactions> {
        // Esempio di dati di transazione
        val calendar = Calendar.getInstance()

        // Creazione di oggetti di transazione di debito di esempio
        val transaction1 = Transactions(false, 50.0, calendar, null, null)
        val transaction2 = Transactions(false, 30.0, calendar, null, null)
        val transaction3 = Transactions(false, 20.0, calendar, null, null)
        val debitTransactions = ArrayList<Transactions>()
        // Aggiungi le transazioni alla lista
        debitTransactions.add(transaction1)
        debitTransactions.add(transaction2)
        debitTransactions.add(transaction3)
        return debitTransactions
    }
}
