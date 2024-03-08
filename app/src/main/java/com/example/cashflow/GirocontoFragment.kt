package com.example.cashflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.cashflow.dataClass.Account

class GirocontoFragment(private val accounts: ArrayList<Account>) : Fragment() {
    private var accountSpinnerProv: Spinner? = null
    private var accountSpinnerArrivo: Spinner? = null
    private var saveGirocontoButton: Button? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_giroconto, container, false)
        accountSpinnerProv = view.findViewById(R.id.accountSpinnerProv)
        accountSpinnerArrivo = view.findViewById(R.id.accountSpinnerArrivo)
        saveGirocontoButton = view.findViewById(R.id.saveGirocontoButton)

        //SPINNER ACCOUNTS
        val accountNames = ArrayList<String>()
        for (account in accounts) {
            accountNames.add(account.name)
        }
        val dataAdapterProv =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, accountNames)
        dataAdapterProv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        accountSpinnerProv?.setAdapter(dataAdapterProv)
        accountSpinnerProv?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedAccountProv = parent.getItemAtPosition(position).toString()
                val accountNamesArrivo = ArrayList(accountNames)
                accountNamesArrivo.remove(selectedAccountProv)
                val dataAdapterArrivo = ArrayAdapter(
                    context!!,
                    android.R.layout.simple_spinner_item,
                    accountNamesArrivo
                )
                dataAdapterArrivo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                accountSpinnerArrivo?.setAdapter(dataAdapterArrivo)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                // Handle nothing selected
            }
        })
        saveGirocontoButton?.setOnClickListener(View.OnClickListener { // Handle click for saving giroconto
            val selectedAccountProv = accountSpinnerProv?.getSelectedItem().toString()
            val selectedAccountArrivo = accountSpinnerArrivo?.getSelectedItem().toString()
            // Perform actions with selected accounts
            requireFragmentManager().popBackStack()
        })
        return view
    }
}
