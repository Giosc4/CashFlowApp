package com.example.cashflow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private ArrayList<Account> accounts = new ArrayList<>();
    JsonReadWrite jsonReadWrite = new JsonReadWrite(this.accounts, "test12.json");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
//        try {
//            accounts = getAccounts();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        Test test = new Test();
        accounts = test.getList();

        GridLayout gridLayout = view.findViewById(R.id.gridLayout);

        if (accounts == null || accounts.isEmpty()) {
            return null;
        }

        // Add account buttons dynamically
        for (Account account : accounts) {
            Button button = new Button(requireContext());
            System.out.println(account.getName() + "");
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

        Button btnAddAccount = view.findViewById(R.id.btnAddAccount);
        btnAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click on "Nuovo Conto" button
                openAccountFragment();
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

    private ArrayList<Account> getAccounts() throws IOException {
        return jsonReadWrite.writeAccountsFromJson(requireContext());
    }


    private void openAccountFragment() {
        // Chiudi il fragment corrente
        requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();

        // Apri il nuovo fragment AccountFragment
        AccountFragment accountFragment = new AccountFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, accountFragment)
                .addToBackStack(null)
                .commit();
    }

    private void openTransactionFragment() {
        TransactionFragment transactionFragment = new TransactionFragment();

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
