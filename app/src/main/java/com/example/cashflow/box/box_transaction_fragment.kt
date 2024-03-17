package com.example.cashflow.box

import android.graphics.Color
import android.os.Bundle
import android.util.Log
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

class box_transaction_fragment(private val readSQL: ReadSQL, private val writeSQL: WriteSQL) : Fragment() {

    private var noDataTextView: TextView? = null

    private fun addTransactionList(view: View, transactions: List<Transactions>?) {
        val gridLayout = view.findViewById<GridLayout>(R.id.gridLayout)
        // Ottieni le transazioni dal database

        transactions?.forEachIndexed { index: Int, transaction: Transactions ->
            val nameTextView = TextView(context).apply {


                text =
                    "${readSQL.getCategoryById(transaction.categoryId)?.name}"
                gravity = Gravity.CENTER
                // Configurazione del layout
                val params = GridLayout.LayoutParams()
                params.rowSpec = GridLayout.spec(index)
                params.columnSpec = GridLayout.spec(0, 1f)
                layoutParams = params
            }

            val amountTextView = TextView(context).apply {
                text = "${transaction.amountValue} €"
                gravity = Gravity.CENTER
                // Configurazione del layout
                val params = GridLayout.LayoutParams()
                params.rowSpec = GridLayout.spec(index)
                params.columnSpec = GridLayout.spec(1, 1f)
                layoutParams = params
            }

            // Alternare il colore di sfondo per le righe
            if (index % 2 == 0) {
                nameTextView.setBackgroundColor(Color.parseColor("#7ad95f"))
                amountTextView.setBackgroundColor(Color.parseColor("#7ad95f"))
            } else {
                nameTextView.setBackgroundColor(Color.parseColor("#e9F2ef"))
                amountTextView.setBackgroundColor(Color.parseColor("#e9F2ef"))
            }

            // Aggiungi i TextView al GridLayout
            gridLayout.addView(nameTextView)
            gridLayout.addView(amountTextView)
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
        noDataTextView = view.findViewById(R.id.noDataTextView)

        val transactions = readSQL.getAllTransactions()
        if (transactions == null || transactions.isEmpty()){
            noDataTextView?.visibility = View.VISIBLE
            Log.d("Transactions", "No transactions found")
        } else {
            noDataTextView?.visibility = View.GONE
            addTransactionList(view, transactions)
        }
    }
}
