package com.example.cashflow.box

import android.graphics.Color
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

class BoxCategoryFragment(private val readSQL: ReadSQL, private val writeSQL: WriteSQL) :
    Fragment() {

    private lateinit var categoriesGridLayout: GridLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.box_fragment_category, container, false)
        categoriesGridLayout = view.findViewById(R.id.categoriesGridLayout)

        loadCategories()

        return view
    }

    private fun loadCategories() {
        val categories = readSQL.getCategories()

        val top6Categories = categories.take(6)

        categoriesGridLayout.rowCount = 3
        categoriesGridLayout.columnCount = 2

        for (category in top6Categories) {
            addCategoryToGrid(category)
        }
    }

    private fun addCategoryToGrid(category: Category) {
        val categoryNameTextView = TextView(context).apply {
            text = category.name ?: "Unknown"
            setTypeface(null, Typeface.BOLD)
            textSize = 20f
            setTextColor(ContextCompat.getColor(context, R.color.buttonColor))
        }

        val descriptionTextView = TextView(context).apply {
            text = category.description ?: "No description"
            setTypeface(null, Typeface.NORMAL)
            textSize = 16f
            // Configura layout e stile come desiderato
        }

        // Crea un LinearLayout per contenere il categoryNameTextView e il descriptionTextView
        val categoryLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            addView(categoryNameTextView)
            addView(descriptionTextView)
        }

        // Aggiungi il LinearLayout al GridLayout
        categoriesGridLayout.addView(categoryLayout, GridLayout.LayoutParams().apply {
            width = 0
            height = GridLayout.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
        })
    }
}
