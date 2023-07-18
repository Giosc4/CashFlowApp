package com.example.cashflow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    ArrayList<Account> accounts;

    private String subtotalText = "";


    TextView myTextView;
    public HomeFragment(ArrayList<Account> accounts){
        this.accounts = accounts;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        GridLayout gridLayout = view.findViewById(R.id.gridLayout);

        if (accounts == null || accounts.isEmpty()) {
            return null;
        }

        // Add account buttons dynamically
        for (Account account : accounts) {
            Button button = new Button(requireContext());
            button.setText(account.getName() + "");
            button.setId(View.generateViewId());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = GridLayout.LayoutParams.WRAP_CONTENT;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f);
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f);
            params.setMargins(8, 8, 8, 8);
            button.setLayoutParams(params);
            gridLayout.addView(button);
        }

        myTextView = view.findViewById(R.id.myTextView);

        subtotalText = getSubtotal();
        myTextView.append(subtotalText);

        Button btnAddAccount = view.findViewById(R.id.btnAddAccount);
        btnAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click on "Nuovo Conto" button
                openNewAccountFragment();
            }
        });

        Button btnAddTransaction = view.findViewById(R.id.btnAddTransaction);
        btnAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click on "Aggiungi Transazione" button
                openTransactionFragment();
            }
        });

        Button btnStatistics = view.findViewById(R.id.btnStatistics);
        btnStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click on "Statistiche" button
                openStatisticsActivity();
            }
        });

        return view;
    }

    private String getSubtotal() {
        double sum = 0;
        for (Account account : accounts) {
            sum = sum + account.getBalance();
        }
        return sum + "";
    }

    private void openNewAccountFragment() {
        // Chiudi il fragment corrente
        requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();

        // Apri il nuovo fragment AccountFragment
        NewAccountFragment newAccountFragment = new NewAccountFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, newAccountFragment)
                .addToBackStack(null)
                .commit();
    }

    private void openTransactionFragment() {
        TransactionFragment transactionFragment = new TransactionFragment(accounts);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, transactionFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void openStatisticsActivity() {
        // Handle opening the statistics activity
    }
}
