package com.example.cashflow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cashflow.dataClass.*
import com.example.cashflow.db.*
import com.example.cashflow.fragments.*

import com.example.cashflow.statistics.*

class MenuManagerFragment() : Fragment() {

    private lateinit var db: SQLiteDB
    private lateinit var readSQL: ReadSQL
    private lateinit var writeSQL: WriteSQL
    private var selectedMenuId: Int = 0
    private lateinit var city: City

    companion object {
        fun newInstance(selectedMenuId: Int, city: City) = MenuManagerFragment().apply {
            arguments = Bundle().apply {
                putInt("selectedMenuId", selectedMenuId)
                putSerializable("city", city)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            city = it.getSerializable("city") as City
            selectedMenuId = it.getInt("selectedMenuId")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.menu_manager_fragment, container, false)

        db = SQLiteDB(requireContext())
        readSQL = ReadSQL(db.writableDatabase)
        writeSQL = WriteSQL(db.writableDatabase)

        loadFragmentBasedOnMenuId()
        return view
    }

    private fun loadFragmentBasedOnMenuId() {
        Log.d("MenuManagerFragment", "Caricamento fragment basato sul menuId $selectedMenuId")
        val fragment = when (selectedMenuId) {

            R.id.nav_home -> {
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                null
            }

            R.id.new_conto -> NewAccountFragment(readSQL, writeSQL)
            R.id.new_transaction -> NewTransactionFragment(readSQL, writeSQL, city)
            R.id.new_budget -> NewBudgetFragment(readSQL, writeSQL)
            R.id.new_debit_credit -> NewDebitCreditFragment(readSQL, writeSQL)
            R.id.line_chart -> Line_chart(readSQL, writeSQL)
            R.id.maps -> MapFragment(readSQL, writeSQL)
            R.id.income_chart -> IncomeExpenseFragment(true, readSQL)
            R.id.expense_chart -> IncomeExpenseFragment(false, readSQL)
            R.id.new_category -> NewCategoryFragment(readSQL, writeSQL)
            else -> {
                Log.d(
                    "MenuManagerFragment",
                    "Nessun caso corrispondente trovato per $selectedMenuId"
                )
                null
            }

        }
        Log.d("MenuManagerFragment", "Caricamento fragment city ${city.toString()}")

        fragment?.let {
            childFragmentManager.beginTransaction().apply {
                replace(R.id.linearContainer, it)
                commit()
            }
        } ?: Log.d("MenuManagerFragment", "Fragment è null")
    }
}
