package com.example.cashflow.fragments.create

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cashflow.MainActivity
import com.example.cashflow.R
import com.example.cashflow.dataClass.*
import com.example.cashflow.db.DataViewModel
import com.example.cashflow.db.ReadSQL
import com.example.cashflow.db.WriteSQL
import java.text.SimpleDateFormat
import java.util.Calendar

class NewDebitCreditFragment() :
    Fragment() {
    private var editTextName: EditText? = null
    private var editTextAmount: EditText? = null
    private var editTextContact: EditText? = null
    private var editTextDescription: EditText? = null
    private var accountSpinner: Spinner? = null
    private var buttonStartDate: Button? = null
    private var textViewStartDate: TextView? = null
    private var buttonEndDate: Button? = null
    private var textViewEndDate: TextView? = null
    private var buttonNewDebit: Button? = null
    private var buttonNewCredit: Button? = null
    private var doneButton: Button? = null
    private var accounts: ArrayList<Account>? = null
    private val viewModel: DataViewModel by viewModels()
    private var readSQL: ReadSQL? = null
    private var writeSQL: WriteSQL? = null


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
        textViewStartDate = view.findViewById(R.id.textViewStartDate)
        buttonEndDate = view.findViewById(R.id.buttonEndDate)
        textViewEndDate = view.findViewById(R.id.textViewEndDate)
        buttonNewDebit = view.findViewById(R.id.buttonNewDebit)
        buttonNewCredit = view.findViewById(R.id.buttonNewCredit)
        doneButton = view.findViewById(R.id.doneButton)

        val calendar = Calendar.getInstance()
        val today = String.format(
            "%d-%02d-%02d",
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        textViewStartDate?.text = today

        readSQL = viewModel.getReadSQL()
        writeSQL = viewModel.getWriteSQL()

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

        accounts = readSQL!!.getAccounts()

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


        // Imposta OnClickListener per i pulsanti "EXPENSE" e "INCOME"
        buttonNewDebit?.setOnClickListener(View.OnClickListener {
            // Cambia il colore del pulsante e imposta la sua proprietà "selected" a true
            buttonNewDebit?.setSelected(true)
            buttonNewDebit?.setBackgroundColor(Color.parseColor("#00cc44")) // Verde quando selezionato
            buttonNewCredit?.setSelected(false)
            buttonNewCredit?.setBackgroundColor(Color.parseColor("#FF6464")) // rosso quando non selezionato
            Log.d("saveDebitCredit", "Debito Selezionato")
        })
        buttonNewCredit?.setOnClickListener(View.OnClickListener {
            // Cambia il colore del pulsante e imposta la sua proprietà "selected" a true
            buttonNewCredit?.setSelected(true)
            buttonNewCredit?.setBackgroundColor(Color.parseColor("#00cc44")) // Verde quando selezionato
            buttonNewDebit?.setSelected(false)
            buttonNewDebit?.setBackgroundColor(Color.parseColor("#FF6464")) // rosso quando non selezionato
            Log.d("saveDebitCredit", "Credito Selezionato")
        })

        buttonStartDate?.setOnClickListener {
            selectDate("Start Date")
        }

        buttonEndDate?.setOnClickListener {
            selectDate("End Date")
        }


        doneButton?.setOnClickListener(View.OnClickListener {
            saveDebitCredit()
        })
        return view
    }


    // Method to open a calendar for selecting date
    private fun selectDate(dateType: String) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(requireContext(), { view, year, monthOfYear, dayOfMonth ->
            // Use the selected date. You can format it as needed.
            val selectedDate = String.format("%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth)

            // Set the selected date to the corresponding TextView based on the button clicked
            when (dateType) {
                "Start Date" -> {
                    textViewStartDate?.text = selectedDate
                }

                "End Date" -> {
                    val startDateString = textViewStartDate?.text.toString()
                    val startDate = SimpleDateFormat("yyyy-MM-dd").parse(startDateString)
                    val endDate = SimpleDateFormat("yyyy-MM-dd").parse(selectedDate)

                    if (endDate.before(startDate)) {
                        Toast.makeText(
                            context,
                            "La data di fine non può essere prima della data di inizio",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        textViewEndDate?.text = selectedDate
                    }
                }
            }
        }, year, month, day)

        dpd.show()
    }

    private fun saveDebitCredit() {
        val name = editTextName?.text.toString()
        val amount = editTextAmount?.text.toString().toDoubleOrNull()
        val contact = editTextContact?.text.toString()
        val description = editTextDescription?.text.toString()
        val accountId = accounts?.get(accountSpinner?.selectedItemPosition ?: 0)?.id ?: 0
        val concessionDate = textViewStartDate?.text.toString()
        val extinctionDate = textViewEndDate?.text.toString()

        if (name.isEmpty() || amount == null) {
            Toast.makeText(context, "Nome e importo sono campi obbligatori", Toast.LENGTH_SHORT)
                .show()
            return
        }

        // Supponendo che tu abbia un metodo nel tuo WriteSQL per inserire un debito o credito
        if (buttonNewDebit?.isSelected == true) {
            val debito = Debito(
                id = 0, // Suppongo che l'ID venga generato dal database
                amount = amount,
                name = name,
                concessionDate = concessionDate,
                extinctionDate = extinctionDate,
                accountId = accountId
            )
            writeSQL?.insertDebito(debito)
            Log.d("saveDebitCredit", "Debito inserito: $debito")
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        } else if (buttonNewCredit?.isSelected == true) {
            val credito = Credito(
                id = 0, // Suppongo che l'ID venga generato dal database
                amount = amount,
                name = name,
                concessionDate = concessionDate,
                extinctionDate = extinctionDate,
                accountId = accountId
            )
            writeSQL?.insertCredito(credito)
            Log.d("saveDebitCredit", "Credito inserito: $credito")
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        } else {
            Toast.makeText(context, "Selezionare se è un debito o un credito", Toast.LENGTH_SHORT)
                .show()
        }

        Toast.makeText(context, "Transazione salvata", Toast.LENGTH_SHORT).show()
    }


}
