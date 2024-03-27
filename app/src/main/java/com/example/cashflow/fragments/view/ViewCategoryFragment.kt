package com.example.cashflow.fragments.view

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.cashflow.R
import com.example.cashflow.dataClass.Category
import com.example.cashflow.db.ReadSQL
import com.example.cashflow.db.WriteSQL

class ViewCategoryFragment(private val readSQL: ReadSQL, private val writeSQL: WriteSQL) : Fragment() {

    private lateinit var categoriesGridLayout: GridLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_category, container, false)
        categoriesGridLayout = view.findViewById(R.id.gridLayout)
        loadCategories()
        return view
    }

    private fun loadCategories() {
        val categories = readSQL.getCategories()

        categoriesGridLayout.rowCount = (categories.size + 1) / 2
        categoriesGridLayout.columnCount = 2

        categories.forEach { category ->
            addCategoryToGrid(category)
        }
    }

    private fun addCategoryToGrid(category: Category) {
        val context = context ?: return

        val categoryNameTextView = TextView(context).apply {
            text = category.name ?: "Unknown"
            setTypeface(null, Typeface.BOLD)
            textSize = 20f
            setTextColor(ContextCompat.getColor(context, R.color.black)) // Assicurati che il colore esista in colors.xml
        }

        val descriptionTextView = TextView(context).apply {
            text = if (category.description.isNullOrEmpty()) {
                "Nessuna descrizione\nTotale categoria €${category.amountCategory}"
            } else {
                "${category.description}\nTotale categoria €${category.amountCategory}"
            }
            setTypeface(null, Typeface.NORMAL)
            textSize = 16f
        }

        val categoryLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            addView(categoryNameTextView)
            addView(descriptionTextView)
        }

        val marginInPixels = (5 * resources.displayMetrics.density + 0.5f).toInt()
        categoriesGridLayout.addView(categoryLayout, GridLayout.LayoutParams().apply {
            width = 0
            height = GridLayout.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels)
        })
    }
}
