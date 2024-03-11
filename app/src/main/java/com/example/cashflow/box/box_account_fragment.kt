package com.example.cashflow.box

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.cashflow.AccountDetailsFragment
import com.example.cashflow.R
import java.math.BigDecimal
import com.example.cashflow.dataClass.*
import com.example.cashflow.db.*

class box_account_fragment(private var accounts: ArrayList<Account>?) : Fragment() {

    private var subtotalTextView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.box_fragment_account, container, false)
        val gridLayout = view.findViewById<GridLayout>(R.id.gridLayout)
        subtotalTextView = view.findViewById<TextView>(R.id.totalAccounts)

        if (accounts == null && accounts!!.isEmpty()) {
            Log.d("AccountFragment", "No accounts")
            val textView = TextView(context)
            textView.text = getString(R.string.no_accounts)
            textView.layoutParams = GridLayout.LayoutParams().apply {
                width = GridLayout.LayoutParams.WRAP_CONTENT
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
            }
            gridLayout.addView(textView)
        } else {
            accounts?.let { accountList ->
                for (account in accountList) {
                    Log.d(
                        "AccountFragment",
                        "Account: " + account.name + " €" + account.balance
                    )
                    val button = Button(context).apply {
                        text = account.name + " €" + account.balance
                        id = View.generateViewId()
                        layoutParams = GridLayout.LayoutParams().apply {
                            width = GridLayout.LayoutParams.WRAP_CONTENT
                            height = GridLayout.LayoutParams.WRAP_CONTENT
                            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                            rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                            setMargins(8, 8, 8, 8)
                        }
                        setOnClickListener {
                            val accountDetailsFragment = AccountDetailsFragment()
                            fragmentManager?.beginTransaction()?.apply {
                                replace(R.id.linearContainer, accountDetailsFragment)
                                addToBackStack(null)
                                commit()
                            }
                        }
                    }
                    gridLayout.addView(button)
                }
            }
        }


        updateSubtotal()
        return view
    }

    override fun onResume() {
        super.onResume()
        updateSubtotal()
    }

    @SuppressLint("StringFormatInvalid")
    private fun updateSubtotal() {
        subtotalTextView?.text = getString(R.string.total_template, subtotal)
    }

    private val subtotal: String
        get() {
            var sum = BigDecimal.ZERO
            accounts?.forEach { account ->
                sum = sum.add(BigDecimal.valueOf(account.balance))
            }
            return sum.setScale(2, BigDecimal.ROUND_HALF_UP).toString()
        }
}
