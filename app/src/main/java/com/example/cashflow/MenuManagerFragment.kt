package com.example.cashflow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cashflow.dataClass.*
import com.example.cashflow.db.*
import com.example.cashflow.fragments.*
import com.example.cashflow.statistics.*

class MenuManagerFragment : Fragment() {

    private lateinit var readSQL: readSQL
    private lateinit var writeSQL: writeSQL
    private lateinit var city: City
    private var selectedMenuId: Int = 0

    companion object {
        fun newInstance(selectedMenuId: Int, readSQLInstance: readSQL, writeSQLInstance: writeSQL, city: City): MenuManagerFragment {
            val fragment = MenuManagerFragment()
            fragment.readSQL = readSQLInstance
            fragment.writeSQL = writeSQLInstance
            fragment.selectedMenuId = selectedMenuId
            fragment.city = city
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.menu_manager_fragment, container, false)
        loadFragmentBasedOnMenuId()
        return view
    }

    private fun loadFragmentBasedOnMenuId() {
        val fragment = when (selectedMenuId) {
            R.id.new_conto -> NewAccountFragment(readSQL, writeSQL)
            R.id.new_transaction -> NewTransactionFragment(readSQL, writeSQL, city)
            R.id.new_budget -> NewBudgetFragment(readSQL, writeSQL)
            R.id.new_debit_credit -> NewDebitCreditFragment(readSQL, writeSQL)
            R.id.line_chart -> Line_chart(readSQL, writeSQL)
            R.id.maps -> MapFragment(readSQL, writeSQL)
            R.id.income_chart -> Income_expense(true, readSQL, writeSQL)
            R.id.expense_chart -> Income_expense(false, readSQL, writeSQL)
            R.id.new_category -> NewCategoryFragment(readSQL, writeSQL)
            else -> null
        }

        fragment?.let {
            childFragmentManager.beginTransaction().apply {
                replace(R.id.linearContainer, it) // Assicurati che questo ID sia corretto
                commit()
            }
        }
    }
}
