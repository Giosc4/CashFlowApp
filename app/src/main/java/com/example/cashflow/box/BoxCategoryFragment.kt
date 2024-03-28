package com.example.cashflow.box

import android.content.Intent
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
import androidx.fragment.app.viewModels
import com.example.cashflow.DetailsActivity
import com.example.cashflow.R
import com.example.cashflow.dataClass.Category
import com.example.cashflow.db.DataViewModel
import com.example.cashflow.db.ReadSQL
import com.example.cashflow.db.WriteSQL

class BoxCategoryFragment() :
    Fragment() {

    private lateinit var categoriesGridLayout: GridLayout
    private var noDataTextView: TextView? = null
    private var viewCategoryBtn: View? = null
    private val viewModel: DataViewModel by viewModels()
    private var readSQL: ReadSQL? = null
    private var writeSQL: WriteSQL? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.box_fragment_category, container, false)
        categoriesGridLayout = view.findViewById(R.id.categoriesGridLayout)
        viewCategoryBtn = view.findViewById(R.id.viewCategoryBtn)
        readSQL = viewModel.getReadSQL()
        writeSQL = viewModel.getWriteSQL()
        loadCategories()

        viewCategoryBtn?.setOnClickListener {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("FRAGMENT_ID", 3)
            context?.startActivity(intent)
        }

        return view
    }

    private fun loadCategories() {
        val categories = readSQL?.getCategories()
        if (categories != null) {
            if (categories.isEmpty()) {
                noDataTextView?.visibility = View.VISIBLE
            } else {
                noDataTextView?.visibility = View.GONE
                val top4Categories = categories.take(4)

                categoriesGridLayout.rowCount = 2
                categoriesGridLayout.columnCount = 2

                for (category in top4Categories) {
                    addCategoryToGrid(category)
                }
            }
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
            if (category.description.isNullOrEmpty()) {
                text = "Nessuna descrizione" + "\nTotale categoria €" + category.amountCategory
            } else {
                text = category.description + "\nTotale categoria €" + category.amountCategory
            }

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
        val marginInPixels = (5 * resources.displayMetrics.density + 0.5f).toInt()

        // Aggiungi il LinearLayout al GridLayout
        categoriesGridLayout.addView(categoryLayout, GridLayout.LayoutParams().apply {
            width = 0
            height = GridLayout.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels)

        })
    }
}
