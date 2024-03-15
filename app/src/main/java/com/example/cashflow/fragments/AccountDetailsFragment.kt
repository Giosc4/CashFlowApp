package com.example.cashflow.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflow.MainActivity
import com.example.cashflow.R
import com.example.cashflow.dataClass.*
import com.example.cashflow.db.SQLiteDB
import com.example.cashflow.db.readSQL
import com.example.cashflow.db.writeSQL

class AccountDetailsFragment(id: Int) : Fragment() {
    private var accountId: Int = -1
    private lateinit var db: SQLiteDB
    private lateinit var readSql: readSQL
    private lateinit var writeSql: writeSQL

    // Views
    private var nameEditText: EditText? = null
    private var balanceTextView: TextView? = null
    private var transactionsRecyclerView: RecyclerView? = null
    private var deleteButton: Button? = null
    private var saveButton: Button? = null

    private var account: Account? = null
    private var transactions: List<Transactions>? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account_details, container, false)
        nameEditText = view.findViewById(R.id.nameEditText)
        balanceTextView = view.findViewById(R.id.balanceTextView)
        transactionsRecyclerView = view.findViewById(R.id.transactionsRecyclerView)
        saveButton = view.findViewById(R.id.saveButton)
        deleteButton = view.findViewById(R.id.deleteButton)

        db = SQLiteDB(context)
        readSql = readSQL(db.writableDatabase)
        writeSql = writeSQL(db.writableDatabase)

        arguments?.let {
            accountId = it.getInt(ARG_ACCOUNT_ID)
        }
        account = readSql.getAccountById(accountId)
        transactions = readSql.getTransactionsByAccountId(accountId)


        if (account != null && account!!.isNotEmpty()) {
            nameEditText?.setText(account!!.name)
            balanceTextView?.setText("Balance Account: " + account!!.balance.toString())
        }

        // Set up RecyclerView with transactions
        val adapter = TransactionListAdapter(transactions as ArrayList<Transactions>)
        val layoutManager = LinearLayoutManager(context)
        transactionsRecyclerView?.setLayoutManager(layoutManager)
        transactionsRecyclerView?.setAdapter(adapter)
        saveButton?.setOnClickListener(View.OnClickListener {
            changeName()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        })
        deleteButton?.setOnClickListener(View.OnClickListener { showDeleteConfirmationDialog() })
        return view
    }
    companion object {
        private const val ARG_ACCOUNT_ID = "account_id"

        fun newInstance(accountId: Int): AccountDetailsFragment {
            val fragment = AccountDetailsFragment(accountId)
            val args = Bundle().apply {
                putInt(ARG_ACCOUNT_ID, accountId)
            }
            fragment.arguments = args
            return fragment
        }
    }
    private fun changeName() {
        val newName = nameEditText!!.text.toString()
        if (newName.isNotEmpty()) {
            try {
                if (!readSql.doesAccountExist(newName)) {
                    writeSql.updateAccountName(accountId, newName)
                    Toast.makeText(context, "Account aggiornato: $newName", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(
                        context,
                        "Account con questo nome già esiste.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    context,
                    "Errore nell'aggiornamento del nome dell'account.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun doesAccountExist(accounts: ArrayList<Account>, name: String): Boolean {
        for (account in accounts) {
            if (account.name == name) {
                return true
            }
        }
        return false
    }

    private fun findAccountIndex(accounts: ArrayList<Account>, oldName: String): Int {
        for (i in accounts.indices) {
            if (accounts[i].name == oldName) {
                return i
            }
        }
        return -1
    }

    // Method to show a delete confirmation dialog
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete this account?")
            .setPositiveButton(android.R.string.yes) { dialog, whichButton -> deleteAccount() }
            .setNegativeButton(android.R.string.no, null).show()
    }

    // Method to delete the account
    private fun deleteAccount() {
        try {
            writeSql.deleteAccount(accountId)
            Toast.makeText(context, "Account eliminato", Toast.LENGTH_LONG).show()
            if (activity != null && isAdded) {
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Errore nell'eliminazione dell'account.", Toast.LENGTH_LONG)
                .show()
        }
    }


    // Custom RecyclerView.Adapter for displaying transactions with a "Detail" button
    private inner class TransactionListAdapter internal constructor(private val transactions: ArrayList<Transactions>) :
        RecyclerView.Adapter<TransactionListAdapter.ViewHolder>() {
        init {
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_transaction, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val transaction = transactions[position]
            println(transaction.printOnApp())
            holder.transactionDetailTextView.text = transaction.printOnApp()
        }

        override fun getItemCount(): Int {
            return transactions.size
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var transactionDetailTextView: TextView
            var detailButton: Button


            init {
                transactionDetailTextView = itemView.findViewById(R.id.transactionDetailTextView)
                detailButton = itemView.findViewById(R.id.detailButton)
                detailButton.setOnClickListener {
                    val editTransactionFragment =
                        account?.let { it1 ->
                            EditTransactionFragment(transactions[getAdapterPosition()],
                                it1
                            )
                        }
                    val fragmentManager = requireActivity().supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    if (editTransactionFragment != null) {
                        fragmentTransaction.replace(R.id.linearContainer, editTransactionFragment)
                    }
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
            }
        }
    }
}
