package com.example.cashflow.statistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflow.R

class AccountsAdapter(private val accountNames: List<String>) :
    RecyclerView.Adapter<AccountsAdapter.ViewHolder>() {
    private val selectedAccounts: BooleanArray
    private var onItemClickListener: ((Int) -> Unit)? = null

    init {
        selectedAccounts = BooleanArray(accountNames.size)
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var accountCheckBox: CheckBox

        init {
            accountCheckBox = itemView.findViewById(R.id.accountCheckBox)
        }
    }

    fun isSelected(position: Int): Boolean {
        return selectedAccounts[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val accountView = inflater.inflate(R.layout.item_account, parent, false)
        return ViewHolder(accountView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val accountName = accountNames[position]
        holder.accountCheckBox.text = accountName
        holder.accountCheckBox.setChecked(selectedAccounts[position])

        holder.accountCheckBox.setOnClickListener { view: View? ->
            val isChecked = holder.accountCheckBox.isChecked
            selectedAccounts[position] = isChecked
            notifyDataSetChanged()

            onItemClickListener?.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return accountNames.size
    }

    fun setSelected(position: Int, isSelected: Boolean) {
        selectedAccounts[position] = isSelected
        notifyDataSetChanged()
    }
}

    // Interfaccia per il gestore di eventi di clic personalizzato
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

