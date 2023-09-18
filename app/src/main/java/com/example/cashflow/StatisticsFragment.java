package com.example.cashflow;
import com.example.cashflow.statistics.chart_pie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class StatisticsFragment extends Fragment {

    private ArrayList<Account> accounts;

    public StatisticsFragment(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla il layout del fragment per la visualizzazione dei pulsanti
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        // Trova i tuoi pulsanti nella vista inflata
        Button btnPieChart = view.findViewById(R.id.btnPieChart);
        Button btnBarChart = view.findViewById(R.id.btnBarChart);
        btnPieChart.setText("Grafico sulle Categorie");

        // Gestisci il clic sui pulsanti per selezionare il tipo di grafico
        btnPieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGrafoATorta();
            }
        });

        btnBarChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                chart_bar barChartFragment = new chart_bar(accounts);
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, barChartFragment)
                        .commit(); */
            }
        });

        // Aggiungi altri pulsanti e gestisci i loro clic per altri tipi di grafico, se necessario

        return view;
    }

    private void openGrafoATorta() {
        chart_pie chart_pie = new chart_pie(accounts);
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, chart_pie);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
