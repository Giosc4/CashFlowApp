package com.example.cashflow

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
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflow.dataClass.Account
import com.example.cashflow.dataClass.Transactions
import java.io.IOException

class AccountDetailsFragment(private val account: Account) : Fragment() {
    private val jsonReadWrite: JsonReadWrite

    // Views
    private var nameEditText: EditText? = null
    private var balanceTextView: TextView? = null
    private var transactionsRecyclerView: RecyclerView? = null
    private var deleteButton: Button? = null
    private var saveButton: Button? = null

    init {
        jsonReadWrite = JsonReadWrite()
    }

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


        // Set account details
        nameEditText?.setText(account.name)
        balanceTextView?.setText("Balance Account: " + account.getBalance().toString())


        // Set up RecyclerView with transactions
        val adapter = TransactionListAdapter(account.listTrans)
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
        val newName = nameEditText!!.getText().toString()
        val oldName = account.name
        try {
            val accounts = jsonReadWrite.readAccountsFromJson(requireContext())
            val index = findAccountIndex(accounts, oldName)
            if (index != -1 && !doesAccountExist(accounts, newName)) {
                account.name = newName
                accounts[index] = account
                jsonReadWrite.setList(accounts, requireContext())
                Toast.makeText(context, "Account aggiornato: $newName", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Errore aggiornamto account.", Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
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
        val accountToDelete = account.name
        try {
            val accounts = jsonReadWrite.readAccountsFromJson(requireContext())
            val index = findAccountIndex(accounts, accountToDelete)
            if (index != -1) {
                accounts.removeAt(index)
                jsonReadWrite.setList(accounts, requireContext())
                Toast.makeText(context, "Account eliminato: $accountToDelete", Toast.LENGTH_LONG)
                    .show()
                if (activity != null && isAdded) {
                    val fragmentManager = requireActivity().supportFragmentManager
                    fragmentManager.popBackStackImmediate(
                        null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            } else {
                Toast.makeText(context, "Errore per eliminare l'account.", Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
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
                transactionDetailTextView.setOnClickListener {
                    //                        EditTransactionFragment editTransactionFragment = new EditTransactionFragment(transactions.get(getAdapterPosition()), account);
//                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                        fragmentTransaction.replace(R.id.fragment_container, editTransactionFragment);
//                        fragmentTransaction.addToBackStack(null);
//                        fragmentTransaction.commit();
                }
                detailButton.setOnClickListener {
                    val editTransactionFragment =
                        EditTransactionFragment(transactions[getAdapterPosition()], account)
                    val fragmentManager = requireActivity().supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.linearContainer, editTransactionFragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
            }
        }
    }
}
