package com.example.cashflow.fragments.statistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflow.R
import com.example.cashflow.dataClass.Account

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

    // Seleziona o deseleziona tutti i checkbox
    fun selectAll(isSelected: Boolean) {
        for (i in selectedAccounts.indices) {
            selectedAccounts[i] = isSelected
        }
        notifyDataSetChanged()

    }

    // Ottiene gli account selezionati in base ai checkbox selezionati
    fun getSelectedAccounts(accounts: List<Account>): ArrayList<Account> {
        return ArrayList(accounts.filterIndexed { index, _ ->
            selectedAccounts[index]
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val accountView = inflater.inflate(R.layout.account_sel, parent, false)
        return ViewHolder(accountView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val accountName = accountNames[position]
        holder.accountCheckBox.text = accountName
        holder.accountCheckBox.isChecked = selectedAccounts[position]

        holder.accountCheckBox.setOnCheckedChangeListener { _, isChecked ->
            selectedAccounts[position] = isChecked
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