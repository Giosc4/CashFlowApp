package com.example.cashflow;
import com.example.cashflow.dataClass.Account;
import com.example.cashflow.statistics.MapFragment;
import com.example.cashflow.statistics.chart_pie;
import com.example.cashflow.statistics.Line_chart;


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
    private Button btnLineChart;
    private Button btnPieChart;
    private Button google_maps;

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
        Button btnLineChart = view.findViewById(R.id.btnLineChart);
        Button google_maps = view.findViewById(R.id.google_maps);
        btnPieChart.setText("Grafico sulle Categorie");

        // Gestisci il clic sui pulsanti per selezionare il tipo di grafico
        btnPieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGrafoATorta();
            }
        });

        btnLineChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Line_chart barChartFragment = new Line_chart(accounts);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, barChartFragment)
                        .commit();
            }
        });

        google_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapFragment mapFragment = new MapFragment(accounts);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,mapFragment)
                        .commit();
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
