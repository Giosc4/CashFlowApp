package com.example.cashflow.box;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.cashflow.R;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class box_budget_fragment extends Fragment {
    private HorizontalBarChart horizontalBarChart;

    public box_budget_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate il layout per questo fragment
        View view = inflater.inflate(R.layout.box_fragment_budget, container, false);
        horizontalBarChart = view.findViewById(R.id.barraOrizzontale);
        setupChart();
        setupChart();
        setupChart();
        setupChart();
        return view;
    }

    private void setupChart() {
        // Dati fittizi per dimostrazione
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 50f)); // Esempio: valore corrente è 50
        float maxVal = 48f; // Esempio: valore massimo raggiungibile è 100

        BarDataSet dataSet = new BarDataSet(entries, "Label");
        dataSet.setColor(Color.GREEN); // Colore iniziale delle barre
        dataSet.setValueTextColor(Color.BLACK); // Colore del testo dei valori

        // Controllo per cambiare il colore se il valore sfora il massimo
        if (entries.get(0).getY() > maxVal) {
            dataSet.setColor(Color.RED); // Cambia il colore in rosso se sforato
        }

        BarData barData = new BarData(dataSet);
        horizontalBarChart.setData(barData);
        horizontalBarChart.setFitBars(true); // rende le barre dell'istogramma adattarsi
        horizontalBarChart.getDescription().setEnabled(false); // Disabilita la descrizione del grafico
        horizontalBarChart.invalidate(); // refresh del grafico
    }
}
