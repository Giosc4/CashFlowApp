package com.example.cashflow.fragments.view

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
import com.example.cashflow.DetailsActivity
import com.example.cashflow.ModifyActivity
import com.example.cashflow.R
import com.example.cashflow.dataClass.TemplateTransaction
import com.example.cashflow.db.ReadSQL

class ViewTemplateFragment(private val readSQL: ReadSQL) : Fragment() {

    private var noDataTextView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_template, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noDataTextView = view.findViewById(R.id.noDataTextView)

        val templates = readSQL.getAllTemplateTransactions()
        if (templates.isEmpty()) {
            noDataTextView?.visibility = View.VISIBLE
        } else {
            noDataTextView?.visibility = View.GONE
            addTemplateButtons(view, templates)

        }
    }

    private fun addTemplateButtons(view: View, templates: List<TemplateTransaction>) {
        val gridLayout = view.findViewById<GridLayout>(R.id.gridLayout)
        // Imposta il numero di colonne del GridLayout.
        // Dato che ora abbiamo due pulsanti per ogni riga, il numero di colonne dovrebbe essere 4.
        gridLayout.columnCount = 4

        templates.forEach { template ->
            // Pulsante Template
            val templateButton = Button(context).apply {
                layoutParams = GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
                ).apply {
                    width = 0 // Usa 0 per far sì che il LayoutParams.FILL sia rispettato
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    rightMargin = 20
                    leftMargin = 20
                    topMargin = 20
                    bottomMargin = 20
                    gravity = Gravity.CENTER
                }
                text = template.name
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.parseColor("#37a63e")) // Usa un colore definito in colors.xml o un valore hardcoded
                setOnClickListener {
                    // Logica per gestire il click sul pulsante del template
                }
            }
            gridLayout.addView(templateButton)

            // Pulsante Modifica
            val editButton = Button(context).apply {
                layoutParams = GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
                ).apply {
                    width = 0 // Usa 0 per far sì che il LayoutParams.FILL sia rispettato
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    rightMargin = 20
                    leftMargin = 20
                    topMargin = 20
                    bottomMargin = 20
                    gravity = Gravity.CENTER
                }
                text = "MODIFICA"
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.parseColor("#007bff")) // Usa un colore per il pulsante modifica
                setOnClickListener {
                    val intent = Intent(context, ModifyActivity::class.java)
                    intent.putExtra("FRAGMENT_ID", 3)
                    context?.startActivity(intent)
                }
                setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green_light_background
                    )
                )
                setTextColor(Color.WHITE)
            }

            gridLayout.addView(editButton)
        }
    }

}
