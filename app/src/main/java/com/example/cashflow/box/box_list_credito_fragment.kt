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

import com.example.cashflow.dataClass.*
import com.example.cashflow.db.*

class box_list_credito_fragment(private val readSQL: readSQL, private val writeSQL: writeSQL) : Fragment() {
    var gridLayout: GridLayout? = null
    var textViewTitle: TextView? = null

    private var noDataTextView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.box_fragment_credito_debito, container, false)
        gridLayout = view.findViewById(R.id.gridLayout)
        textViewTitle = view.findViewById(R.id.textViewTitle)
        noDataTextView = view.findViewById(R.id.noDataTextView)
        textViewTitle?.setText("Credito (da ricevere)")



        val credits = readSQL.getAllCredits()

        val localNoDataTextView = noDataTextView
        if (credits.isEmpty()) {
            localNoDataTextView?.visibility = View.VISIBLE
        } else {
            localNoDataTextView?.visibility = View.GONE
            addCreditsList(view, credits)
        }
        return view
    }

    private fun addCreditsList(view: View, credits: List<Credito>) {
        // Imposta il numero di colonne per il GridLayout. Ad esempio, 2 per id e importo.
        gridLayout?.columnCount = 2

        // Per ogni debito nella lista, crea una nuova riga nel GridLayout
        credits.forEachIndexed { index, credito ->
            // Crea un TextView per l'id del debito o il nome
            val textViewName = TextView(context).apply {
                text = "${credito.name}: €${credito.amount}"
                gravity = Gravity.CENTER
                setBackgroundColor(
                    if (index % 2 == 0) Color.parseColor("#FFEBEE") else Color.parseColor(
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
                text = credito.extinctionDate
                gravity = Gravity.CENTER
                setBackgroundColor(
                    if (index % 2 == 0) Color.parseColor("#FFEBEE") else Color.parseColor(
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