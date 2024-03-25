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
import com.example.cashflow.fragments.AccountDetailsFragment
import com.example.cashflow.R
import java.math.BigDecimal
import com.example.cashflow.dataClass.*
import com.example.cashflow.db.*

class box_account_fragment(private val readSQL: ReadSQL, private val writeSQL: WriteSQL) :
    Fragment() {

    private var totalAccounts: TextView? = null
    private var accounts: ArrayList<Account>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.box_fragment_account, container, false)
        val gridLayout = view.findViewById<GridLayout>(R.id.gridLayout)
        val noDataTextView = view.findViewById<TextView>(R.id.noDataTextView)
        totalAccounts = view.findViewById(R.id.totalAccounts)

        accounts = readSQL.getAccounts()

        var totalBalance = 0.0
        accounts?.let { accountList ->
            for (account in accountList) {
                totalBalance += account.balance
            }
        }
        totalAccounts?.text = "Totale: €$totalBalance"

        if (accounts.isNullOrEmpty()) {
            Log.d("AccountFragment", "No accounts")
            noDataTextView?.visibility = View.VISIBLE
            totalAccounts?.visibility = View.GONE
            gridLayout.visibility = View.GONE
        } else {
            noDataTextView?.visibility = View.GONE
            totalAccounts?.visibility = View.VISIBLE
            gridLayout?.visibility = View.VISIBLE
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
                            Log.d("AccountFragment", "Button clicked: " + account.name)
                            val accountDetailsFragment =
                                AccountDetailsFragment.newInstance(account.id)
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.linearContainer, accountDetailsFragment)
                                .addToBackStack(null) // Optional: Aggiunge la transazione al back stack
                                .commit()

                        }
                    }
                    gridLayout.addView(button)
                }
            }
        }
        return view
    }
    override fun onResume() {
        super.onResume()
    }
}