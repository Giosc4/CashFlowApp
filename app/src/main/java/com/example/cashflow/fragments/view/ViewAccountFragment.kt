package com.example.cashflow.fragments.view

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
import com.example.cashflow.DetailsActivity
import com.example.cashflow.MainActivity
import com.example.cashflow.R
import com.example.cashflow.dataClass.*
import com.example.cashflow.db.SQLiteDB
import com.example.cashflow.db.ReadSQL
import com.example.cashflow.db.WriteSQL

class ViewAccountFragment : Fragment() {
    private var accountId: Int = -1
    private lateinit var db: SQLiteDB
    private lateinit var readSql: ReadSQL
    private lateinit var writeSql: WriteSQL

    // Views
    private var nameEditText: EditText? = null
    private var balanceTextView: TextView? = null
    private var transactionsRecyclerView: RecyclerView? = null
    private var deleteButton: Button? = null
    private var saveButton: Button? = null

    private var account: Account? = null
    private var transactions: List<Transactions>? = null

    companion object {
        private const val ARG_ACCOUNT_ID = "account_id"

        fun newInstance(accountId: Int): ViewAccountFragment {
            val fragment = ViewAccountFragment()
            val args = Bundle().apply {
                putInt(ARG_ACCOUNT_ID, accountId)
            }
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            accountId = it.getInt(ARG_ACCOUNT_ID)
        }
        // Rest of your code...
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_account, container, false)
        nameEditText = view.findViewById(R.id.nameEditText)
        balanceTextView = view.findViewById(R.id.balanceTextView)
        transactionsRecyclerView = view.findViewById(R.id.transactionsRecyclerView)
        saveButton = view.findViewById(R.id.saveButton)
        deleteButton = view.findViewById(R.id.deleteButton)

        db = SQLiteDB(context)
        readSql = ReadSQL(db.writableDatabase)
        writeSql = WriteSQL(db.writableDatabase)

        account = readSql.getAccountById(accountId)
        transactions = readSql.getTransactionsByAccountId(accountId)
        arguments?.let {
            accountId = it.getInt(ARG_ACCOUNT_ID)
        }

        if (account != null && account!!.isNotEmpty()) {
            nameEditText?.setText(account!!.name)
            balanceTextView?.setText("Totale Conto: €" + account!!.balance.toString())
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

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete this account?")
            .setPositiveButton(android.R.string.yes) { dialog, whichButton -> deleteAccount() }
            .setNegativeButton(android.R.string.no, null).show()
    }

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


    private inner class TransactionListAdapter internal constructor(private val transactions: ArrayList<Transactions>) :
        RecyclerView.Adapter<TransactionListAdapter.ViewHolder>() {
        init {
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_view_accounts, parent, false)
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
                transactionDetailTextView = itemView.findViewById(R.id.detailTextView)
                detailButton = itemView.findViewById(R.id.detailButton)
                detailButton.setOnClickListener {
                    // Ottieni l'ID della transazione.
                    val transactionId = transactions[adapterPosition].id

                    val intent = Intent(context, DetailsActivity::class.java)
                    intent.putExtra("FRAGMENT_ID", 2)
                    intent.putExtra("TRANSACTION_ID", transactionId)
                    context?.startActivity(intent)
                }
            }
        }
    }
}
