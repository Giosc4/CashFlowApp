package com.example.cashflow.box

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import com.example.cashflow.DetailsActivity
import com.example.cashflow.R
import com.example.cashflow.dataClass.*
import com.example.cashflow.db.*

class BoxDebitCreditFragment(
    private val isDebit: Boolean // true per debito, false per credito
) : Fragment() {
    private var gridLayout: GridLayout? = null
    private var textViewTitle: TextView? = null
    private var noDataTextView: TextView? = null
    private var viewDebitoCreditotBtn: Button? = null
    private val viewModel: DataViewModel by viewModels()
    private var readSQL: ReadSQL? = null
    private var writeSQL: WriteSQL? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.box_fragment_credito_debito, container, false)
        gridLayout = view.findViewById(R.id.gridLayout)
        textViewTitle = view.findViewById(R.id.textViewTitle)
        noDataTextView = view.findViewById(R.id.noDataTextView)
        viewDebitoCreditotBtn = view.findViewById(R.id.viewDebitoCreditotBtn)

        readSQL = viewModel.getReadSQL()
        writeSQL = viewModel.getWriteSQL()

        if (isDebit) {
            textViewTitle?.setText("Debito (da dare)")
            viewDebitoCreditotBtn?.setText("Vedi Debito")
            val debits = readSQL!!.getAllDebits()
            if (debits.isEmpty()) {
                noDataTextView?.visibility = View.VISIBLE
            } else {
                noDataTextView?.visibility = View.GONE
                addDebitsList(view, debits)
            }

            viewDebitoCreditotBtn?.setOnClickListener {
                val intent = Intent(context, DetailsActivity::class.java)
                intent.putExtra("FRAGMENT_ID", 7)
                context?.startActivity(intent)
            }
        } else {
            textViewTitle?.setText("Credito (da ricevere)")
            viewDebitoCreditotBtn?.setText("Vedi Credito")
            val credits = readSQL!!.getAllCredits()
            if (credits.isEmpty()) {
                noDataTextView?.visibility = View.VISIBLE
            } else {
                noDataTextView?.visibility = View.GONE
                addCreditsList(view, credits)
            }

            viewDebitoCreditotBtn?.setOnClickListener {
                val intent = Intent(context, DetailsActivity::class.java)
                intent.putExtra("FRAGMENT_ID", 8)
                context?.startActivity(intent)
            }
        }
        return view
    }

    private fun addDebitsList(view: View, debits: List<Debito>) {
        gridLayout?.columnCount = 3

        debits.forEachIndexed { index, debito ->
            // Crea e configura un TextView per il nome o l'ID del debito
            val textViewName = TextView(context).apply {
                text = "${debito.name}: €${debito.amount}"
                gravity = Gravity.CENTER
                setBackgroundColor(
                    if (index % 2 == 0) Color.parseColor("#7ad95f") else Color.parseColor("#ECEFF1")
                )
                layoutParams = GridLayout.LayoutParams(
                    GridLayout.spec(index, 1),
                    GridLayout.spec(0, 1f)
                ).apply {
                    width = 0 // Utilizza GridLayout.LayoutParams per assegnare il peso
                    bottomMargin = 2
                    topMargin = 2
                    leftMargin = 2
                    rightMargin = 2
                }
            }
            gridLayout?.addView(textViewName)

            // Crea e configura un TextView per le date di concessione ed estinzione del debito
            val textViewDate = TextView(context).apply {
                text = "Da: ${debito.concessionDate}\nA: ${debito.extinctionDate}"
                gravity = Gravity.CENTER
                setBackgroundColor(
                    if (index % 2 == 0) Color.parseColor("#7ad95f") else Color.parseColor("#ECEFF1")
                )
                layoutParams = GridLayout.LayoutParams(
                    GridLayout.spec(index, 1),
                    GridLayout.spec(1, 1f)
                ).apply {
                    width = 0 // Utilizza GridLayout.LayoutParams per assegnare il peso
                    bottomMargin = 2
                    topMargin = 2
                    leftMargin = 2
                    rightMargin = 2
                }
            }
            gridLayout?.addView(textViewDate)
        }
    }

    private fun addCreditsList(view: View, credits: List<Credito>) {
        gridLayout?.columnCount = 3

        credits.forEachIndexed { index, credito ->
            // Aggiungi TextView per il nome/ID del credito
            val textViewName = TextView(context).apply {
                text = "${credito.name}: €${credito.amount}"
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
                    width = 0 // Assicura che occupi lo spazio correttamente
                    bottomMargin = 2
                    topMargin = 2
                    leftMargin = 2
                    rightMargin = 2
                }
            }
            gridLayout?.addView(textViewName)

            // Aggiungi TextView per la data
            val textViewDate = TextView(context).apply {
                text = "Da: ${credito.concessionDate}\nA: ${credito.extinctionDate}"
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
                    width = 0
                    bottomMargin = 2
                    topMargin = 2
                    leftMargin = 2
                    rightMargin = 2
                }
            }
            gridLayout?.addView(textViewDate)

            // Crea e aggiungi un Button per ogni credito
            val button = Button(context).apply {
                text = "Fine"
                val pixels = (5 * resources.displayMetrics.density + 0.5f).toInt()

                layoutParams = GridLayout.LayoutParams(
                    GridLayout.spec(index, 1),
                    GridLayout.spec(2, 1f)
                ).apply {
                    width = pixels
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    gravity = Gravity.CENTER
                    bottomMargin = 2
                    topMargin = 2
                    leftMargin = 2
                    rightMargin = 2
                }

                // Imposta un listener per il clic, se necessario
                setOnClickListener {
                    // Agisci quando il pulsante viene premuto, ad esempio mostrare dettagli
                }
            }
            gridLayout?.addView(button)
        }
    }
}
