package com.example.cashflow.box

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.cashflow.R
import com.example.cashflow.db.*

class box_manager_fragment() :
    Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_box_manager, container, false)
        val linearContainer = view.findViewById<LinearLayout>(R.id.linearContainer)

        addBoxFragment(
            box_account_fragment(),
            linearContainer,
            "box_account_fragment"
        )
        addBoxFragment(
            box_transaction_fragment(),
            linearContainer,
            "box_transaction_fragment"
        )
        addBoxFragment(
            BoxCategoryFragment(),
            linearContainer,
            "box_category_fragment"
        )
        addBoxFragment(
            box_template_fragment(),
            linearContainer,
            "box_template_fragment"
        )
        addBoxFragment(
            box_budget_fragment(),
            linearContainer,
            "box_budget_fragment"
        )
        addBoxFragment(
            BoxDebitCreditFragment(true),
            linearContainer,
            "box_list_debito_fragment"
        )
        addBoxFragment(
            BoxDebitCreditFragment(false),
            linearContainer,
            "box_list_credito_fragment"
        )

        return view
    }

    private fun addBoxFragment(fragment: Fragment, container: LinearLayout, tag: String) {
        childFragmentManager.beginTransaction().apply {
            val frame = LinearLayout(context)
            frame.id = View.generateViewId()
            container.addView(frame)
            add(frame.id, fragment, tag)
            commit()
        }
    }
}
