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

import com.example.cashflow.dataClass.Account;
import com.example.cashflow.dataClass.City;

import java.math.BigDecimal;
import java.util.ArrayList;


public class HomeFragment extends Fragment {

    ArrayList<Account> accounts;
    private String subtotalText = "";
    TextView myTextView;

    Posizione posizione;
    City city;


    public HomeFragment(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        GridLayout gridLayout = view.findViewById(R.id.gridLayout);
        myTextView = view.findViewById(R.id.myTextView);


        this.posizione = new Posizione(requireContext());

        posizione.requestDeviceLocation(new Posizione.DeviceLocationCallback() {
            @Override
            public void onLocationFetched(City city) {
                HomeFragment.this.city = city;
            }

            @Override
            public void onLocationFetchFailed(Exception e) {
                // Gestisci l'errore in base alle tue esigenze
            }
        });

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

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AccountDetailsFragment accountDetailsFragment = new AccountDetailsFragment(account);
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.linearContainer, accountDetailsFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });

            gridLayout.addView(button);
        }

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
                openStatisticsFragment();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Recalculate subtotal and update TextView
        subtotalText = getSubtotal();
        myTextView.setText("Totale: " + subtotalText);
        System.out.println("Subtotal: " + subtotalText);
    }


    private String getSubtotal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (Account account : accounts) {
            sum = sum.add(BigDecimal.valueOf(account.getBalance()));
        }
        return sum.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }


    private void openNewAccountFragment() {
        NewAccountFragment newAccountFragment = new NewAccountFragment(accounts);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.linearContainer, newAccountFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openTransactionFragment() {
        NewTransactionFragment transactionFragment = new NewTransactionFragment(accounts, city);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.linearContainer, transactionFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openStatisticsFragment() {
        StatisticsFragment statisticsFragment = new StatisticsFragment(accounts);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.linearContainer, statisticsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
