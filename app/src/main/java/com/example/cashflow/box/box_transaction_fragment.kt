package com.example.cashflow.box

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cashflow.DetailsActivity
import com.example.cashflow.R

import com.example.cashflow.dataClass.*
import com.example.cashflow.db.*

class box_transaction_fragment() :
    Fragment() {

    private var noDataTextView: TextView? = null
    private var gridLayout: GridLayout? = null
    private var viewTransBtn: Button? = null
    private val viewModel: DataViewModel by viewModels()
    private var readSQL: ReadSQL? = null
    private var writeSQL: WriteSQL? = null


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
        gridLayout = view.findViewById(R.id.gridLayout)
        viewTransBtn = view.findViewById(R.id.viewTransBtn)

        readSQL = viewModel.getReadSQL()
        writeSQL = viewModel.getWriteSQL()

        val transactions = readSQL!!.getAllTransactions()
        if (transactions == null || transactions.isEmpty()) {
            noDataTextView?.visibility = View.VISIBLE
            Log.d("Transactions", "No transactions found")
        } else {
            noDataTextView?.visibility = View.GONE

            transactions?.forEachIndexed { index: Int, transaction: Transactions ->
                val contoTextView = TextView(context).apply {
                    text = readSQL!!.getAccountById(transaction.accountId)?.name
                    gravity = Gravity.CENTER
                    // Configurazione del layout
                    val params = GridLayout.LayoutParams()
                    params.rowSpec = GridLayout.spec(index)
                    params.columnSpec = GridLayout.spec(0, 1f)
                    layoutParams = params
                }

                val nameTextView = TextView(context).apply {
                    text =
                        "${readSQL!!.getCategoryById(transaction.categoryId)?.name}"
                    gravity = Gravity.CENTER
                    // Configurazione del layout
                    val params = GridLayout.LayoutParams()
                    params.rowSpec = GridLayout.spec(index)
                    params.columnSpec = GridLayout.spec(1, 1f)
                    layoutParams = params
                }

                val amountTextView = TextView(context).apply {
                    if (transaction.isIncome)
                        text = "+${transaction.amountValue} €"
                    else
                        text = "-${transaction.amountValue} €"
                    gravity = Gravity.CENTER
                    // Configurazione del layout
                    val params = GridLayout.LayoutParams()
                    params.rowSpec = GridLayout.spec(index)
                    params.columnSpec = GridLayout.spec(2, 1f)
                    layoutParams = params
                }

                // Alternare il colore di sfondo per le righe
                if (index % 2 == 0) {
                    nameTextView.setBackgroundColor(Color.parseColor("#7ad95f"))
                    amountTextView.setBackgroundColor(Color.parseColor("#7ad95f"))
                    contoTextView.setBackgroundColor(Color.parseColor("#7ad95f"))
                } else {
                    nameTextView.setBackgroundColor(Color.parseColor("#e9F2ef"))
                    amountTextView.setBackgroundColor(Color.parseColor("#e9F2ef"))
                    contoTextView.setBackgroundColor(Color.parseColor("#e9F2ef"))
                }

                // Aggiungi i TextView al GridLayout
                gridLayout!!.addView(contoTextView)
                gridLayout!!.addView(nameTextView)
                gridLayout!!.addView(amountTextView)

            }
        }

        viewTransBtn?.setOnClickListener {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("FRAGMENT_ID", 2)
            context?.startActivity(intent)
        }
    }
}
