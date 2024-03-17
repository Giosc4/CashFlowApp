package com.example.cashflow.box

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
import com.example.cashflow.R

import com.example.cashflow.dataClass.*
import com.example.cashflow.db.*

class box_template_fragment(private val readSQL: readSQL, private val writeSQL: writeSQL) : Fragment() {

    private var noDataTextView: TextView? = null


    private fun addButtonsBox(numberOfButtons: Int, view: View) {
        val gridLayout = view.findViewById<GridLayout>(R.id.gridLayout)
        gridLayout.setColumnCount(2)
        for (i in 0 until numberOfButtons) {
            val button = Button(context)
            val params = GridLayout.LayoutParams()
            params.width = 300
            params.height = 100
            params.rightMargin = 20
            params.leftMargin = 20
            params.topMargin = 20
            params.setGravity(Gravity.CENTER)
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            button.setLayoutParams(params)
            button.text = "Button " + (i + 1)
            button.setTextColor(Color.WHITE)
            button.setBackgroundColor(Color.parseColor("#37a63e"))
            button.setId(View.generateViewId())
            val buttonId = i + 1 // Identificativo univoco per il pulsante, basato sull'indice i
            button.tag = buttonId // Imposta il tag del pulsante con il suo identificativo
            button.setOnClickListener { v -> // Recupera l'identificativo dal tag del pulsante
                val id = v.tag as Int
                Log.d("Button", "Button $id clicked")
            }
            gridLayout.addView(button)
        }
    }
    private fun addTemplateButtons(view: View,templates: List<TemplateTransaction>) {
        val gridLayout = view.findViewById<GridLayout>(R.id.gridLayout)
        gridLayout.setColumnCount(2)

        templates.forEachIndexed { index, template ->
            val button = Button(context).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 300
                    height = 100
                    rightMargin = 20
                    leftMargin = 20
                    topMargin = 20
                    setGravity(Gravity.CENTER)
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                }
                text = template.name
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.parseColor("#37a63e"))
                setId(View.generateViewId())
                tag = template.id // Imposta il tag del pulsante con l'ID del template
                setOnClickListener { v ->
                    val id = v.tag as Int
                    Log.d("Button", "Template $id clicked")
                }
            }
            gridLayout.addView(button)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla il layout per questo fragment
        return inflater.inflate(R.layout.box_fragment_template, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noDataTextView = view.findViewById(R.id.noDataTextView)

        val templates = readSQL.getAllTemplateTransactions()
        if (templates.isEmpty() ) {
            noDataTextView?.visibility = View.VISIBLE
        } else {
            noDataTextView?.visibility = View.GONE
            addTemplateButtons(view, templates)
        }
    }
}