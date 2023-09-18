package com.example.cashflow;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
    private Button deleteButton;
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
        deleteButton = view.findViewById(R.id.deleteButton);


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
                HomeFragment homeFragment = new HomeFragment(jsonReadWrite.readAccountsFromJson(requireContext()));
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, homeFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });



        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        return view;
    }

    private void changeName() {
        String newName = nameEditText.getText().toString();
        String oldName = account.getName();

        try {
            ArrayList<Account> accounts = jsonReadWrite.readAccountsFromJson(requireContext());
            int index = findAccountIndex(accounts, oldName);

            if (index != -1 && !doesAccountExist(accounts, newName)) {
                account.setName(newName);
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

    private boolean doesAccountExist(ArrayList<Account> accounts, String name) {
        for (Account account : accounts) {
            if (account.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private int findAccountIndex(ArrayList<Account> accounts, String oldName) {
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getName().equals(oldName)) {
                return i;
            }
        }
        return -1;
    }

    // Method to show a delete confirmation dialog
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete this account?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteAccount();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    // Method to delete the account
    private void deleteAccount() {
        String accountToDelete = account.getName();

        try {
            ArrayList<Account> accounts = jsonReadWrite.readAccountsFromJson(requireContext());
            int index = findAccountIndex(accounts, accountToDelete);

            if (index != -1) {
                accounts.remove(index);
                jsonReadWrite.setList(accounts, requireContext());

                Toast.makeText(getContext(), "Account deleted: " + accountToDelete, Toast.LENGTH_LONG).show();
                if (getActivity() != null && isAdded()) {
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, new HomeFragment(accounts))
                            .commit();
                }
            } else {
                Toast.makeText(getContext(), "Failed to delete account.", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Custom RecyclerView.Adapter for displaying transactions with a "Detail" button
    private class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.ViewHolder> {
        private ArrayList<Transactions> transactions;

        TransactionListAdapter(ArrayList<Transactions> transactions) {
            this.transactions = transactions;
            notifyDataSetChanged();
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
                        EditTransactionFragment editTransactionFragment = new EditTransactionFragment(transactions.get(getAdapterPosition()), account);
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, editTransactionFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
            }
        }
    }
}
