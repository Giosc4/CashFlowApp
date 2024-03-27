package com.example.cashflow.fragments.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflow.DetailsActivity
import com.example.cashflow.R
import com.example.cashflow.dataClass.Transactions
import com.example.cashflow.db.*

class ViewTransactionsFragment(private val readSQL: ReadSQL, private val writeSQL: WriteSQL) :
    Fragment() {

    private var transactionsRecyclerView: RecyclerView? = null
    private lateinit var adapter: TransactionListAdapter
    private var transactions: ArrayList<Transactions>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_transactions, container, false)

        transactionsRecyclerView = view.findViewById(R.id.transactionsRecyclerView)

        transactions = readSQL.getAllTransactions()

        setupRecyclerView()

        return view
    }

    private fun setupRecyclerView() {
        adapter = TransactionListAdapter(transactions ?: arrayListOf())
        transactionsRecyclerView?.layoutManager = LinearLayoutManager(context)
        transactionsRecyclerView?.adapter = adapter
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
            holder.transactionDetailTextView.text = transaction.printOnApp()
            if (position % 2 == 0) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.green_light_background))
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT)
            }
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
                    // Creazione dell'intent per avviare DetailsActivity
                    val intent = Intent(context, DetailsActivity::class.java)
                    intent.putExtra("FRAGMENT_ID", 1)
                    intent.putExtra("TRANSACTION_ID", transactions[adapterPosition].id)
                    context?.startActivity(intent)
                }

            }
        }
    }
}
