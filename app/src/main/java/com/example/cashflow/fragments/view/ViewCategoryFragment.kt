package com.example.cashflow.fragments.view

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.cashflow.R
import com.example.cashflow.dataClass.Category
import com.example.cashflow.dataClass.TemplateTransaction
import com.example.cashflow.db.ReadSQL
import com.example.cashflow.db.WriteSQL

class ViewCategoryFragment(private val readSQL: ReadSQL, private val writeSQL: WriteSQL) :
    Fragment() {

    private lateinit var categoriesGridLayout: GridLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_category, container, false)
        categoriesGridLayout = view.findViewById(R.id.gridLayout)
        val categories = readSQL.getCategories()

        categoriesGridLayout.rowCount = (categories.size + 1) / 2
        categoriesGridLayout.columnCount = 2

        categories.forEach { category ->
            addCategoryToGrid(category)
            Log.d("ViewCategoryFragment", "Categories amount: ${category.amountCategory} ")

        }
        return view
    }

    private fun addCategoryToGrid(category: Category) {
        val context = context ?: return

        val categoryInfoLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = GridLayout.LayoutParams(
                GridLayout.spec(
                    GridLayout.UNDEFINED,
                    GridLayout.FILL,
                    3f
                ), // Assegna un peso maggiore
                GridLayout.spec(0, GridLayout.FILL, 3f)
            ).also {
                it.width = 200
                val marginInPixels = (5 * resources.displayMetrics.density + 0.5f).toInt()
                it.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels)
            }
        }

        val categoryNameTextView = TextView(context).apply {
            text = category.name ?: "Unknown"
            setTypeface(null, Typeface.BOLD)
            textSize = 20f
            setTextColor(ContextCompat.getColor(context, R.color.black))
        }

        val descriptionTextView = TextView(context).apply {
            // Modifica al testo per riflettere meglio i valori di amountCategory
            text =
                "${category.description ?: "Nessuna descrizione"}\nTotale categoria €${category.amountCategory}"
            setTypeface(null, Typeface.NORMAL)
            textSize = 16f
        }

        categoryInfoLayout.addView(categoryNameTextView)
        categoryInfoLayout.addView(descriptionTextView)

        val modifyButton = Button(context).apply {
            text = "Modifica"
            setBackgroundColor(ContextCompat.getColor(context, R.color.green_light_background))
            setTextColor(ContextCompat.getColor(context, R.color.white))
            layoutParams = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f),
                GridLayout.spec(1, GridLayout.FILL, 1f)
            ).also {
                val widthInPixels = (60 * resources.displayMetrics.density).toInt()
                val heightInPixels = (10 * resources.displayMetrics.density).toInt()
                it.width = widthInPixels
                it.height = heightInPixels
                val marginInPixels = (5 * resources.displayMetrics.density + 0.5f).toInt()
                it.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels)
            }
            // Implementa la logica di modifica qui
        }

        categoriesGridLayout.addView(categoryInfoLayout)
        categoriesGridLayout.addView(modifyButton)
    }
}
