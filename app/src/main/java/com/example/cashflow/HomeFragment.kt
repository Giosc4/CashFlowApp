package com.example.cashflow

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.cashflow.Posizione.DeviceLocationCallback
import com.example.cashflow.dataClass.Account
import com.example.cashflow.dataClass.City
import java.math.BigDecimal

class HomeFragment(var accounts: ArrayList<Account>?) : Fragment() {
    private var subtotalText = ""
    var myTextView: TextView? = null
    var btnAddAccount: Button? = null
    var btnAddTransaction: Button? = null
    var btnStatistics: Button? = null
    var btnAddBudget: Button? = null
    var btnAddDebitCredit: Button? = null
    var posizione: Posizione? = null
    var city: City? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val gridLayout = view.findViewById<GridLayout>(R.id.gridLayout)
        myTextView = view.findViewById(R.id.myTextView)
        btnAddAccount = view.findViewById(R.id.btnAddAccount)
        btnAddTransaction = view.findViewById(R.id.btnAddTransaction)
        btnStatistics = view.findViewById(R.id.btnStatistics)
        btnAddBudget = view.findViewById(R.id.btnAddBudget)
        btnAddDebitCredit = view.findViewById(R.id.btnAddDebitCredit)
        btnAddAccount?.setBackgroundColor(Color.parseColor("#37a63e"))
        btnAddTransaction?.setBackgroundColor(Color.parseColor("#37a63e"))
        btnStatistics?.setBackgroundColor(Color.parseColor("#37a63e"))
        btnAddBudget?.setBackgroundColor(Color.parseColor("#37a63e"))
        btnAddDebitCredit?.setBackgroundColor(Color.parseColor("#37a63e"))
        posizione = Posizione(requireContext())
        posizione!!.requestDeviceLocation(object : DeviceLocationCallback {
            override fun onLocationFetched(city: City?) {
                this@HomeFragment.city = city
            }

            override fun onLocationFetchFailed(e: Exception?) {
                // Gestisci l'errore in base alle tue esigenze
            }
        })
        if (accounts == null || accounts!!.isEmpty()) {
            return null
        }

        // Add account buttons dynamically
        for (account in accounts!!) {
            val button = Button(requireContext())
            button.text = account.name + ""
            button.setId(View.generateViewId())
            val params = GridLayout.LayoutParams()
            params.width = GridLayout.LayoutParams.WRAP_CONTENT
            params.height = GridLayout.LayoutParams.WRAP_CONTENT
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
            params.setMargins(8, 8, 8, 8)
            button.setLayoutParams(params)
            button.setOnClickListener {
                val accountDetailsFragment = AccountDetailsFragment(account)
                val fragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.linearContainer, accountDetailsFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
            gridLayout.addView(button)
        }
        btnAddAccount?.setOnClickListener(View.OnClickListener { // Handle click on "Nuovo Conto" button
            openFragment(NewAccountFragment(accounts!!))
        })
        btnAddTransaction?.setOnClickListener(View.OnClickListener { // Handle click on "Aggiungi Transazione" button
            openFragment(NewTransactionFragment(accounts!!, city))
        })
        btnStatistics?.setOnClickListener(View.OnClickListener { // Handle click on "Statistiche" button
            openFragment(StatisticsFragment(accounts!!))
        })
        btnAddBudget?.setOnClickListener(View.OnClickListener { // Handle click on "Nuovo Budget" button
            openFragment(NewBudgetFragment())
        })
        btnAddDebitCredit?.setOnClickListener(View.OnClickListener { // Handle click on "Nuovo Debito/Credito" button
            openFragment(NewDebitCreditFragment(accounts!!))
        })
        return view
    }

    override fun onResume() {
        super.onResume()
        subtotalText = subtotal
        myTextView!!.text = "Totale: $subtotalText"
        println("Subtotal: $subtotalText")
    }

    private val subtotal: String
        private get() {
            var sum = BigDecimal.ZERO
            for (account in accounts!!) {
                sum = sum.add(BigDecimal.valueOf(account.getBalance()))
            }
            return sum.setScale(2, BigDecimal.ROUND_HALF_UP).toString()
        }

    private fun openFragment(fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.linearContainer, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
