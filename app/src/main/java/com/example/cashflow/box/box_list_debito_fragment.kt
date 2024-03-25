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
import com.example.cashflow.db.ReadSQL
import com.example.cashflow.db.WriteSQL

class box_list_debito_fragment(private val readSQL: ReadSQL, private val writeSQL: WriteSQL) : Fragment() {
    var gridLayout: GridLayout? = null
    var textViewTitle: TextView? = null
    private var noDataTextView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.box_fragment_credito_debito, container, false)
        gridLayout = view.findViewById(R.id.gridLayout)
        textViewTitle = view.findViewById(R.id.textViewTitle)
        noDataTextView = view.findViewById(R.id.noDataTextView)
        textViewTitle?.setText("Debito (da dare)")

        val debits = readSQL.getAllDebits()

        val localNoDataTextView = noDataTextView
        if (debits.isEmpty()) {
            localNoDataTextView?.visibility = View.VISIBLE
        } else {
            localNoDataTextView?.visibility = View.GONE
            addDebitsList(view, debits)
        }
        return view
    }

    private fun addDebitsList(view: View, debits: List<Debito>) {
        // Imposta il numero di colonne per il GridLayout. Ad esempio, 2 per id e importo.
        gridLayout?.columnCount = 2

        // Per ogni debito nella lista, crea una nuova riga nel GridLayout
        debits.forEachIndexed { index, debito ->
            // Crea un TextView per l'id del debito o il nome
            val textViewName = TextView(context).apply {
                text = "${debito.name}: €${debito.amount}"
                gravity = Gravity.CENTER
                setBackgroundColor(
                    if (index % 2 == 0) Color.parseColor("#7ad95f") else Color.parseColor(
                        "#ECEFF1"
                    )
                )
                layoutParams = GridLayout.LayoutParams(
                    GridLayout.spec(index, 1),
                    GridLayout.spec(0, 1f)
                ).apply {
                    width = 0 // Usare GridLayout.LayoutParams per assegnare peso
                    bottomMargin = 2
                    topMargin = 2
                    leftMargin = 2
                    rightMargin = 2
                }
            }
            gridLayout?.addView(textViewName)

            // Crea un TextView per la data di estinzione del debito
            val textViewDate = TextView(context).apply {
                text = "Da: ${debito.concessionDate}\nA: ${debito.extinctionDate}"
                gravity = Gravity.CENTER
                setBackgroundColor(
                    if (index % 2 == 0) Color.parseColor("#7ad95f") else Color.parseColor(
                        "#ECEFF1"
                    )
                )
                layoutParams = GridLayout.LayoutParams(
                    GridLayout.spec(index, 1),
                    GridLayout.spec(1, 1f)
                ).apply {
                    width = 0 // Usare GridLayout.LayoutParams per assegnare peso
                    bottomMargin = 2
                    topMargin = 2
                    leftMargin = 2
                    rightMargin = 2
                }
            }
            gridLayout?.addView(textViewDate)
        }
    }


}
