package com.example.cashflow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class AccountDetailsFragment extends Fragment {

    private Account account;
    private JsonReadWrite jsonReadWrite;

    // Views
    private EditText nameEditText;
    private TextView balanceTextView;
    private RecyclerView transactionsRecyclerView;
    private Button saveButton;

    public AccountDetailsFragment(Account account) {
        this.account = account;
        jsonReadWrite = new JsonReadWrite("test12.json");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_details, container, false);

        nameEditText = view.findViewById(R.id.nameEditText);
        balanceTextView = view.findViewById(R.id.balanceTextView);
        transactionsRecyclerView = view.findViewById(R.id.transactionsRecyclerView);
        saveButton = view.findViewById(R.id.saveButton);

        // Set account details
        nameEditText.setText(account.getName());
        balanceTextView.setText("Balance Account: " + String.valueOf(account.getBalance()));

        // Set up RecyclerView with transactions
        TransactionListAdapter adapter = new TransactionListAdapter(account.getListTrans());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        transactionsRecyclerView.setLayoutManager(layoutManager);
        transactionsRecyclerView.setAdapter(adapter);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeName();
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    LinearLayout mainLayout = getActivity().findViewById(R.id.mainLayout);
                    mainLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }

    private void changeName() {
        String newName = nameEditText.getText().toString();
        String oldName = account.getName();
        account.setName(newName);

        try {
            ArrayList<Account> accounts = jsonReadWrite.readAccountsFromJson(requireContext());
            int index = findAccountIndex(accounts, oldName);

            if (index != -1) {
                accounts.set(index, account);
                jsonReadWrite.setList(accounts, requireContext());

                Toast.makeText(getContext(), "Account updated: " + newName, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Failed to update account.", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int findAccountIndex(ArrayList<Account> accounts, String oldName) {
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getName().equals(oldName)) {
                return i;
            }
        }
        return -1;
    }

    // Custom RecyclerView.Adapter for displaying transactions with a "Detail" button
    private class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.ViewHolder> {
        private ArrayList<Transactions> transactions;

        TransactionListAdapter(ArrayList<Transactions> transactions) {
            this.transactions = transactions;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_transaction, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Transactions transaction = transactions.get(position);
            System.out.println("Transaction category: " + transaction.getCategory());
            holder.transactionDetailTextView.setText(transaction.printOnApp());
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView transactionDetailTextView;
            Button detailButton;

            ViewHolder(View itemView) {
                super(itemView);
                transactionDetailTextView = itemView.findViewById(R.id.transactionDetailTextView);
                detailButton = itemView.findViewById(R.id.detailButton);

                detailButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // This is where you handle the button click.
                        // You might, for example, start a new Activity or Fragment that displays the details of the transaction.
                    }
                });
            }
        }
    }
}