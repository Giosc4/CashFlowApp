package com.example.cashflow.box;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashflow.R;
import com.example.cashflow.dataClass.Transactions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class box_list_debito_fragment extends Fragment {

    GridLayout gridLayout;
    TextView textViewTitle;
    private List<Transactions> debitTransactions;

    public box_list_debito_fragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.box_fragment_credito_debito, container, false);

        gridLayout = view.findViewById(R.id.gridLayout);
        textViewTitle = view.findViewById(R.id.textViewTitle);

        textViewTitle.setText("Debito (da dare)");

        // Popola la lista di transazioni debit
        debitTransactions = populateRecyclerViewWithExampleData();

        // Aggiungi le transazioni alla tabella GridLayout
        addTransactionList(view);

        return view;
    }

    private void addTransactionList(View view) {

        // Dati di esempio delle transazioni di debito
        String[][] transactions = {
                {"Transazione 1", "€50.0"},
                {"Transazione 2", "€30.0"},
                {"Transazione 3", "€20.0"}
        };

        for (int i = 0; i < transactions.length; i++) {
            for (int j = 0; j < transactions[i].length; j++) {
                TextView textView = new TextView(getContext());
                textView.setText(transactions[i][j]);
                textView.setPadding(10, 10, 10, 10);
                textView.setGravity(Gravity.CENTER);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j, 1f);
                textView.setLayoutParams(params);

                // Alternare il colore di sfondo per le righe
                if (i % 2 == 0) {
                    textView.setBackgroundColor(Color.parseColor("#7ad95f"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#e9F2ef"));
                }

                gridLayout.addView(textView);
            }
        }
    }

    private List<Transactions> populateRecyclerViewWithExampleData() {
        // Esempio di dati di transazione
        Calendar calendar = Calendar.getInstance();

        // Creazione di oggetti di transazione di debito di esempio
        Transactions transaction1 = new Transactions(false, 50.0, calendar, null, null);
        Transactions transaction2 = new Transactions(false, 30.0, calendar, null, null);
        Transactions transaction3 = new Transactions(false, 20.0, calendar, null, null);

        ArrayList<Transactions> debitTransactions = new ArrayList<>();
        // Aggiungi le transazioni alla lista
        debitTransactions.add(transaction1);
        debitTransactions.add(transaction2);
        debitTransactions.add(transaction3);

        return debitTransactions;
    }
}
