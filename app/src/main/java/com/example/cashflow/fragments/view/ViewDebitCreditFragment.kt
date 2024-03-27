package com.example.cashflow.box

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.cashflow.R
import com.example.cashflow.dataClass.Debito
import com.example.cashflow.dataClass.Credito
import com.example.cashflow.db.ReadSQL

class ViewDebitCreditFragment(
    private val readSQL: ReadSQL,
    private val isDebit: Boolean // true per debito, false per credito
) : Fragment() {
    private lateinit var gridLayout: GridLayout
    private lateinit var textViewTitle: TextView
    private lateinit var noDataTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.box_fragment_credito_debito, container, false)
        gridLayout = view.findViewById(R.id.gridLayout)
        textViewTitle = view.findViewById(R.id.textViewTitle)
        noDataTextView = view.findViewById(R.id.noDataTextView)

        setupFragment()

        return view
    }

    private fun setupFragment() {
        if (isDebit) {
            textViewTitle.text = "Lista Debiti"
            loadDebits()
        } else {
            textViewTitle.text = "Lista Crediti"
            loadCredits()
        }
    }

    private fun loadDebits() {
        val debits = readSQL.getAllDebits()
        if (debits.isEmpty()) {
            noDataTextView.visibility = View.VISIBLE
        } else {
            noDataTextView.visibility = View.GONE
            debits.forEach { debito ->
                addFinancialRecordToGridLayout(debito)
            }
        }
    }

    private fun loadCredits() {
        val credits = readSQL.getAllCredits()
        if (credits.isEmpty()) {
            noDataTextView.visibility = View.VISIBLE
        } else {
            noDataTextView.visibility = View.GONE
            credits.forEach { credito ->
                addFinancialRecordToGridLayout(credito)
            }
        }
    }

    private fun addFinancialRecordToGridLayout(financialRecord: Any) {
        // Qui potresti dover fare un cast in base a se `financialRecord` è un `Debito` o un `Credito`
        // Per esempio:
        val name: String
        val amount: Double
        if (financialRecord is Debito) {
            name = financialRecord.name
            amount = financialRecord.amount
        } else if (financialRecord is Credito) {
            name = financialRecord.name
            amount = financialRecord.amount
        } else {
            return // Tipo non riconosciuto
        }

        val recordView = TextView(context).apply {
            text = "$name: €$amount"
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#ECEFF1"))
            layoutParams = GridLayout.LayoutParams().apply {
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                width = 0 // Usa un valore specifico o match_parent
                height = GridLayout.LayoutParams.WRAP_CONTENT
                bottomMargin = 10
                topMargin = 10
            }
        }

        gridLayout.addView(recordView)
    }
}
