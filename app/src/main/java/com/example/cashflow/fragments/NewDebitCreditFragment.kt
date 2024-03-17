package com.example.cashflow.fragments

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
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cashflow.R
import com.example.cashflow.dataClass.*
import com.example.cashflow.db.ReadSQL
import com.example.cashflow.db.WriteSQL

class NewDebitCreditFragment(private val readSQL: ReadSQL, private val writeSQL: WriteSQL) : Fragment() {
    private var editTextName: EditText? = null
    private var editTextAmount: EditText? = null
    private var editTextContact: EditText? = null
    private var editTextDescription: EditText? = null
    private var accountSpinner: Spinner? = null
    private var buttonStartDate: Button? = null
    private var buttonEndDate: Button? = null
    private var buttonNewDebit: Button? = null
    private var buttonNewCredit: Button? = null
    private var accounts: ArrayList<Account>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_debit_credit, container, false)

        // Initialize UI components
        editTextName = view.findViewById(R.id.editTextName)
        editTextAmount = view.findViewById(R.id.editTextAmount)
        editTextContact = view.findViewById(R.id.editTextContact)
        editTextDescription = view.findViewById(R.id.editTextDescription)
        accountSpinner = view.findViewById(R.id.accountSpinner)
        buttonStartDate = view.findViewById(R.id.buttonStartDate)
        buttonEndDate = view.findViewById(R.id.buttonEndDate)
        buttonNewDebit = view.findViewById(R.id.buttonNewDebit)
        buttonNewCredit = view.findViewById(R.id.buttonNewCredit)
        editTextAmount?.setFilters(arrayOf(
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

        accounts = readSQL.getAccounts()

        //SPINNER ACCOUNTS
        val accountNames = ArrayList<String>()
        for (account in accounts!!) {
            accountNames.add(account.name ?: "")
        }
        val dataAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, accountNames)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        accountSpinner?.setAdapter(dataAdapter)
        accountSpinner?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedAccount = parent.getItemAtPosition(position).toString()
                /*
                NON SUCCEDE NULLA PERCHè NEL METODO saveTransaction() VIENE SEELEZIONATO CON accountSpinner.getSelectedItem
               String accountSelected = accountSpinner.getSelectedItem() != null ? accountSpinner.getSelectedItem().toString() : "";
                 */
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                // SE NULLA è SELEZIONATO ALLORA VIENE PRESO IL PRIMO ACCOUNT
            }
        })

        // Set click listeners for date buttons
        buttonStartDate?.setOnClickListener(View.OnClickListener { // Handle click for selecting start date
            selectDate("Start Date")
        })
        buttonEndDate?.setOnClickListener(View.OnClickListener { // Handle click for selecting end date
            selectDate("End Date")
        })

        // Set click listeners for new debit and credit buttons
        buttonNewDebit?.setOnClickListener(View.OnClickListener { // Handle click for adding new debit
            addNewDebit()
        })
        buttonNewCredit?.setOnClickListener(View.OnClickListener { // Handle click for adding new credit
            addNewCredit()
        })
        return view
    }

    // Method to open a calendar for selecting date
    private fun selectDate(dateType: String) {
        // You can implement your logic here to open a calendar and select a date
        // For example, you can use DatePickerDialog
        // This is just a placeholder method
        Toast.makeText(context, "Select $dateType from calendar", Toast.LENGTH_SHORT).show()
    }

    // Method to add new debit
    private fun addNewDebit() {
        // You can implement your logic here to add a new debit
        // This is just a placeholder method
        Toast.makeText(context, "Add new debit", Toast.LENGTH_SHORT).show()
    }

    // Method to add new credit
    private fun addNewCredit() {
        // You can implement your logic here to add a new credit
        // This is just a placeholder method
        Toast.makeText(context, "Add new credit", Toast.LENGTH_SHORT).show()
    }
}
