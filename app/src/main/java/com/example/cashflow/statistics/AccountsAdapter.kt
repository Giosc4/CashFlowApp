package com.example.cashflow.statistics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cashflow.R;

import java.util.List;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.ViewHolder> {
    private List<String> accountNames;
    private boolean[] selectedAccounts;
    private OnItemClickListener onItemClickListener;  // Aggiunto gestore di eventi personalizzato

    public AccountsAdapter(List<String> accountNames) {
        this.accountNames = accountNames;
        selectedAccounts = new boolean[accountNames.size()];
    }

    // Imposta il gestore di eventi personalizzato per gli eventi di clic
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox accountCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            accountCheckBox = itemView.findViewById(R.id.accountCheckBox);
        }
    }

    public boolean isSelected(int position) {
        return selectedAccounts[position];
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View accountView = inflater.inflate(R.layout.item_account, parent, false);
        return new ViewHolder(accountView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String accountName = accountNames.get(position);
        holder.accountCheckBox.setText(accountName);
        holder.accountCheckBox.setChecked(selectedAccounts[position]);

        // Aggiungi un ascoltatore di clic per gli elementi del RecyclerView
        holder.accountCheckBox.setOnClickListener(view -> {
            boolean isChecked = holder.accountCheckBox.isChecked();
            selectedAccounts[position] = isChecked;
            notifyDataSetChanged();

            // Chiamare il gestore di eventi personalizzato quando un elemento viene cliccato
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return accountNames.size();
    }

    public void setSelected(int position, boolean isSelected) {
        selectedAccounts[position] = isSelected;
        notifyDataSetChanged();
    }

    // Interfaccia per il gestore di eventi di clic personalizzato
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
