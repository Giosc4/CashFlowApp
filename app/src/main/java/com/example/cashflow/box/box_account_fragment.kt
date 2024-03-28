package com.example.cashflow.box

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cashflow.DetailsActivity
import com.example.cashflow.R
import com.example.cashflow.dataClass.*
import com.example.cashflow.db.*


class box_account_fragment() : Fragment() {

    private var totalAccounts: TextView? = null
    private var accounts: ArrayList<Account>? = null
    private val viewModel: DataViewModel by viewModels()
    private var readSQL: ReadSQL? = null
    private var writeSQL: WriteSQL? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.box_fragment_account, container, false)
        val gridLayout = view.findViewById<GridLayout>(R.id.gridLayout)
        val noDataTextView = view.findViewById<TextView>(R.id.noDataTextView)
        totalAccounts = view.findViewById(R.id.totalAccounts)

        readSQL = viewModel.getReadSQL()
        writeSQL = viewModel.getWriteSQL()

        accounts = readSQL!!.getAccounts()

        var totalBalance = 0.0
        accounts?.let { accountList ->
            for (account in accountList) {
                totalBalance += account.balance
            }
        }
        totalAccounts?.text = "Totale: €$totalBalance"
        val greenColor = ContextCompat.getColor(
            requireContext(),
            R.color.buttonColor
        ) // Assumi che R.color.green sia definito nel tuo file colors.xml
        totalAccounts?.setTextColor(greenColor)
        totalAccounts?.setTypeface(null, Typeface.BOLD)
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
                        setBackgroundColor(
                            ContextCompat.getColor(requireContext(), R.color.green_light_background)
                        )
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        setOnClickListener {
                            Log.d("AccountFragment", "Button clicked: " + account.name)
                            val intent = Intent(requireContext(), DetailsActivity::class.java)
                            intent.putExtra("FRAGMENT_ID", 1)
                            Log.d("DetailsActivity", "Account ID: " + account.id)
                            intent.putExtra("ACCOUNT_ID", account.id)
                            startActivity(intent)
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