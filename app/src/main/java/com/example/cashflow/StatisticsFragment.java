package com.example.cashflow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cashflow.statistics.chart_pie;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private ArrayList<Account> accounts;

    public StatisticsFragment(ArrayList<Account> accounts){
        this.accounts = accounts;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla il layout del fragment per la visualizzazione dei grafici
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        // Trova i tuoi grafici nella vista inflata e configurali
        PieChart pieChart = view.findViewById(R.id.pieChart);
        BarChart barChart = view.findViewById(R.id.barChart);

        // Chiamate le funzioni setupPieChart, setupBarChart e setupLineChart per configurare i grafici
        // Passa gli oggetti grafico e i dati appropriati come argomenti a queste funzioni.

        // Esempio:
//         setupPieChart(pieChart, accounts);
         setupBarChart(barChart, accounts);
//         setupLineChart(lineChart, accounts, null   , null);

        chart_pie pieChartFragment = new chart_pie(accounts);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, pieChartFragment)
                .commit();


        return view;
    }


    public  void setupBarChart(BarChart barChart, ArrayList<Account> accounts) {
        // Impostazioni per il grafico a barre
        barChart.getDescription().setEnabled(false);

        // Dati per il grafico a barre
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> accountNames = new ArrayList<>();

        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            entries.add(new BarEntry(i, (float) account.getBalance()));
            accountNames.add(account.getName());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Account Balances");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData = new BarData(dataSet);

        // Imposta etichette sull'asse X
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int index = (int) value;
                if (index >= 0 && index < accountNames.size()) {
                    return accountNames.get(index);
                } else {
                    return "";
                }
            }
        });

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        barChart.setData(barData);
        barChart.invalidate();
    }


}
