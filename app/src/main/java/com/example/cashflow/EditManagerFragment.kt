package com.example.cashflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.cashflow.db.ReadSQL
import com.example.cashflow.db.SQLiteDB
import com.example.cashflow.db.WriteSQL
import com.example.cashflow.fragments.modify.ViewAccountFragment
import com.example.cashflow.fragments.modify.EditTransactionFragment

class EditManagerFragment : Fragment() {
    private lateinit var db: SQLiteDB
    private lateinit var readSQL: ReadSQL
    private lateinit var writeSQL: WriteSQL
    private var accountId: Int = -1
    private var transactionId: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            accountId = it.getInt(ARG_ACCOUNT_ID, -1)
            transactionId = it.getInt(ARG_TRANSACTION_ID, -1)

        }

        context?.let {
            db = SQLiteDB(it)
            readSQL = ReadSQL(db.writableDatabase)
            writeSQL = WriteSQL(db.writableDatabase)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Open AccountDetailsFragment once the view has been created and is ready
        if (accountId != -1) {
            openAccountDetails(accountId)
        } else if (transactionId != -1) {
            openEditTransaction(transactionId)
        }
    }

    fun openAccountDetails(accountId: Int) {
        // Clear all previous fragments/boxes
        clearFragments()

        // Load AccountDetailsFragment passing the account ID
        val accountDetailsFragment = ViewAccountFragment.newInstance(accountId)
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.linearContainer, accountDetailsFragment)
            addToBackStack(null) // Add to back stack for back navigation
            commit()
        }
    }

    fun openEditTransaction(transactionId: Int?) {
        // Clear all previous fragments/boxes
        clearFragments()

        // Load AccountDetailsFragment passing the account ID
        val accountDetailsFragment = transactionId?.let { EditTransactionFragment.newInstance(it) }
        requireActivity().supportFragmentManager.beginTransaction().apply {
            if (accountDetailsFragment != null) {
                replace(R.id.linearContainer, accountDetailsFragment)
            }
            addToBackStack(null) // Add to back stack for back navigation
            commit()
        }
    }

    private fun clearFragments() {
        requireActivity().supportFragmentManager.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    companion object {
        private const val ARG_ACCOUNT_ID = "account_id"
        private const val ARG_TRANSACTION_ID = "transaction_id"

        fun newInstance(accountId: Int? = null, transactionId: Int? = null): EditManagerFragment {
            return EditManagerFragment().apply {
                arguments = Bundle().apply {
                    accountId?.let { putInt(ARG_ACCOUNT_ID, it) }
                    transactionId?.let { putInt(ARG_TRANSACTION_ID, it) }
                }
            }
        }
    }
}