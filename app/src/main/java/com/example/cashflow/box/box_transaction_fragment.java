package com.example.cashflow.box;

import androidx.fragment.app.Fragment;
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

import com.example.cashflow.R;

public class box_transaction_fragment extends Fragment {

    public box_transaction_fragment() {
    }

    private void addTransactionList(View view) {
        GridLayout gridLayout = view.findViewById(R.id.gridLayout);
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.box_fragment_template, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addTransactionList(view);
    }



}
