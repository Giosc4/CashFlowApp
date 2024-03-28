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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cashflow.ModifyActivity
import com.example.cashflow.R
import com.example.cashflow.dataClass.*
import com.example.cashflow.db.*

class ViewDebitCreditFragment(
    private val isDebit: Boolean // true per debito, false per credito
) : Fragment() {
    private lateinit var gridLayout: GridLayout
    private lateinit var textViewTitle: TextView
    private lateinit var noDataTextView: TextView

    private var counter = 0
    private val viewModel: DataViewModel by viewModels()
    private var readSQL: ReadSQL? = null
    private var writeSQL: WriteSQL? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_debit_credit, container, false)
        gridLayout = view.findViewById(R.id.gridLayout)
        textViewTitle = view.findViewById(R.id.textViewTitle)
        noDataTextView = view.findViewById(R.id.noDataTextView)
        readSQL = viewModel.getReadSQL()
        writeSQL = viewModel.getWriteSQL()

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
        val debits = readSQL!!.getAllDebits()
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
        val credits = readSQL!!.getAllCredits()
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
        val name: String
        var dataInzio: String = ""
        var dataFine: String = ""

        val amount: Double
        if (financialRecord is Debito) {
            name = financialRecord.name
            amount = financialRecord.amount
            dataInzio = financialRecord.concessionDate
            dataFine = financialRecord.extinctionDate
        } else if (financialRecord is Credito) {
            name = financialRecord.name
            amount = financialRecord.amount
        } else {
            return // Tipo non riconosciuto
        }

        val recordView = TextView(context).apply {
            text = if (dataFine.isEmpty()) {
                "$name: €$amount \n Data Inizio: $dataInzio"
            } else {
                "$name: €$amount \n Data Inizio: $dataInzio\nData Fine: $dataFine"
            }
            gravity = Gravity.CENTER
            layoutParams = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, 1f),
                GridLayout.spec(0, 1f) // Colonna 0
            ).also { params ->
                params.width = (120 * resources.displayMetrics.density).toInt()
                params.height = GridLayout.LayoutParams.WRAP_CONTENT
                params.setMargins(10, 10, 10, 10) // Imposta qui i margini
            }
            setBackgroundColor(
                if (counter % 2 == 0) {
                    ContextCompat.getColor(requireContext(), R.color.green_light_background)
                } else {
                    Color.parseColor("#e9F2ef")
                }
            )

        }

        val button = Button(context).apply {
            text = "Modifica"
            layoutParams = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, 1f),
                GridLayout.spec(1, 1f) // Colonna 1
            ).also { params ->
                params.width = 0
                params.height = (40 * resources.displayMetrics.density).toInt()
                params.setMargins(10, 10, 10, 10) // Imposta qui i margini
            }

            setOnClickListener {
                val intent = Intent(context, ModifyActivity::class.java)
                intent.putExtra("FRAGMENT_ID", 5)
                context?.startActivity(intent)
            }
            setBackgroundColor(
                if (counter % 2 == 0) {
                    ContextCompat.getColor(requireContext(), R.color.green_light_background)
                } else {
                    Color.parseColor("#e9F2ef")
                }
            )
        }
        counter++
        gridLayout.addView(recordView)
        gridLayout.addView(button)
    }
}
