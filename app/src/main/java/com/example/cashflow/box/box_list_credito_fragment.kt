package com.example.cashflow.box;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashflow.R;
import com.example.cashflow.dataClass.Transactions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class box_list_credito_fragment extends Fragment {
    GridLayout gridLayout;
    TextView textViewTitle;
    private RecyclerView transactionsRecyclerView;

    // Lista di transazioni debit
    private List<Transactions> creditoTransactions;

    public box_list_credito_fragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.box_fragment_credito_debito, container, false);
        gridLayout = view.findViewById(R.id.gridLayout);
        textViewTitle = view.findViewById(R.id.textViewTitle);

        textViewTitle.setText("Credito (da ricevere)");

        addTransactionList(view);

        return view;
    }

    private void addTransactionList(View view) {
        ViewGroup gridLayout = view.findViewById(R.id.gridLayout);

        // Dati fittizi delle transazioni
        String[][] transactions = {
                {"Caffè", "€2"},
                {"Libro", "€15"},
                {"Biglietto cinema", "€8"},
                {"Abbonamento palestra", "€30"}
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
}