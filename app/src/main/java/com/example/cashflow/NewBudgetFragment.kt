package com.example.cashflow

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.cashflow.dataClass.*
import com.example.cashflow.db.SQLiteDB
import com.example.cashflow.db.readSQL
import com.example.cashflow.db.writeSQL

class NewBudgetFragment : Fragment() {
    private var editTextNome: EditText? = null
    private var spinnerCategoria: Spinner? = null
    private var editTextImporto: EditText? = null
    private var buttonSalva: Button? = null
    private var categories: ArrayList<Category>? = null

    private lateinit var db: SQLiteDB
    private lateinit var readSql: readSQL
    private lateinit var writeSql: writeSQL
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_budget, container, false)
        editTextNome = view.findViewById(R.id.editTextNome)
        spinnerCategoria = view.findViewById(R.id.spinnerCategoria)
        editTextImporto = view.findViewById(R.id.editTextImporto)
        buttonSalva = view.findViewById(R.id.buttonSalva)

        db = SQLiteDB(context)
        readSql = readSQL(db.writableDatabase)
        writeSql = writeSQL(db.writableDatabase)

        categories = readSql.getCategories()
        val categoryNames = categories?.map { it.name }
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames ?: listOf())
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoria?.adapter = categoryAdapter


        editTextImporto?.setFilters(arrayOf(
            InputFilter { source, start, end, dest, dstart, dend -> // Check if the input contains a decimal point
                var hasDecimalSeparator = dest.toString().contains(".")

                // Get the current number of decimal places
                var decimalPlaces = 0
                if (hasDecimalSeparator) {
                    val split =
                        dest.toString().split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    if (split.size > 1) {
                        decimalPlaces = split[1].length
                    }
                }

                // Check if the input is a valid decimal number
                for (i in start until end) {
                    val inputChar = source[i]

                    // Allow digits and a decimal point
                    if (!Character.isDigit(inputChar) && inputChar != '.') {
                        return@InputFilter ""
                    }

                    // Allow only two decimal places
                    if (hasDecimalSeparator && decimalPlaces >= 2) {
                        return@InputFilter ""
                    }

                    // Increment the decimal places count if a decimal point is encountered
                    if (inputChar == '.') {
                        hasDecimalSeparator = true
                    } else if (hasDecimalSeparator) {
                        decimalPlaces++
                    }
                }
                null
            }
        ))
        buttonSalva?.setOnClickListener(View.OnClickListener { salvaDati() })
        return view
    }

    private fun salvaDati() {
        // Ottieni i dati dagli elementi UI
        val nome = editTextNome!!.getText().toString()
        val categoria = spinnerCategoria!!.getSelectedItem().toString()
        val importo = editTextImporto!!.getText().toString()
    }
}
