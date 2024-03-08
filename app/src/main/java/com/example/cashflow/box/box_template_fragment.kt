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
import androidx.fragment.app.Fragment
import com.example.cashflow.R

class box_template_fragment : Fragment() {
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
        addButtonsBox(5, view)
    }
}