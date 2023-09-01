package com.example.cashflow.statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cashflow.Account;
import com.example.cashflow.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class chart_pie extends Fragment {

    private ArrayList<Account> accounts;

    public chart_pie(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla il layout del fragment per la visualizzazione del grafico a torta
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        // Trova il tuo PieChart nella vista inflata e configuralo
        PieChart pieChart = view.findViewById(R.id.pieChart);
        setupPieChart(pieChart, accounts);

        return view;
    }

    public void setupPieChart(PieChart pieChart, ArrayList<Account> accounts) {
        // Impostazioni per il grafico a torta
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        // Legenda
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        pieChart.setDrawEntryLabels(false);

        // Dati per il grafico a torta
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Account account : accounts) {
            entries.add(new PieEntry((float) account.getBalance(), account.getName()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Total Balance");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setData(pieData);
    }

}
