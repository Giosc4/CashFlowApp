package com.example.cashflow.statistics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.cashflow.R;
import com.example.cashflow.dataClass.Account;
import com.example.cashflow.dataClass.Transactions;
import com.example.cashflow.dataClass.CategoriesEnum;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Income_expense extends Fragment {
    private final Boolean isIncome;
    private ArrayList<Account> accounts;

    private TextView title;
    private Spinner accountSpinner;
    private Button allAccountsButton;
    private TextView selectedAccounts;

    private PieChart pieChart;
    private BarChart barChart;

    public Income_expense(Boolean isIncome, ArrayList<Account> accounts) {
        this.isIncome = isIncome;
        this.accounts = accounts;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income_expense, container, false);

        accountSpinner = view.findViewById(R.id.accountSpinner);
        allAccountsButton = view.findViewById(R.id.allAccountsButton);
        selectedAccounts = view.findViewById(R.id.selectedAccounts);
        pieChart = view.findViewById(R.id.pieChart);
        barChart = view.findViewById(R.id.barChart);
        title = view.findViewById(R.id.title);

        if (isIncome){
            title.setText("INCOME");
        } else {
            title.setText("EXPENSE");
        }

        //SPINNER ACCOUNTS
        ArrayList<String> accountNames = new ArrayList<>();
        for (Account account : accounts) {
            accountNames.add(account.getName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, accountNames);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountSpinner.setAdapter(dataAdapter);

        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedAccount = parent.getItemAtPosition(position).toString();
                /*
                NON SUCCEDE NULLA PERCHè NEL METODO saveTransaction() VIENE SEELEZIONATO CON accountSpinner.getSelectedItem
               String accountSelected = accountSpinner.getSelectedItem() != null ? accountSpinner.getSelectedItem().toString() : "";
                 */
                System.out.println(selectedAccount);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // SE NULLA è SELEZIONATO ALLORA VIENE PRESO IL PRIMO ACCOUNT
            }

        });


        allAccountsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // vengono selezionati tutti cli account e deselezionato l'account dallo spinner
            }
        });


        initPieChart();
        initBarChart();


        return view;
    }





    /*

    ERRORE MANCA LA LEGENDA E BISOGNA SISTEMARE I CONTI

     */



















    private void initPieChart() {
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(android.R.color.transparent);
        pieChart.setTransparentCircleRadius(50f);
        pieChart.setRotationEnabled(true);
        pieChart.setExtraTopOffset(30f);
        pieChart.setUsePercentValues(true);

        PieDataSet pieDataSet = new PieDataSet(getIncomeOrExpensePieData(accounts), "Legenda Dati");
        pieDataSet.setColors(getCategoryColors());
        pieDataSet.setDrawValues(false);

        // Configura la legenda
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(10f);

        // Imposta l'YOffset per posizionare la legenda sopra il grafico
        legend.setYOffset(-30f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
    }





    // Inizializza il grafico a barre
    private void initBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getLegend().setEnabled(false);

        BarDataSet barDataSet = new BarDataSet(getIncomeOrExpenseBarData(accounts), "");
        int[] colors = getCategoryColors();
        barDataSet.setColors(colors);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
    }

    public List<PieEntry> getIncomeOrExpensePieData(ArrayList<Account> accounts) {
        List<PieEntry> entries = new ArrayList<>();
        CategoriesEnum[] categories = CategoriesEnum.values();

        for (CategoriesEnum category : categories) {
            float totalAmount = 0;

            for (Account account : accounts) {
                for (Transactions transaction : account.getListTrans()) {
                    if (transaction.isIncome() == isIncome && transaction.getCategory() == category) {
                        totalAmount += (float) transaction.getAmountValue();
                    }
                }
            }

            if (totalAmount > 0) {
                entries.add(new PieEntry(totalAmount, category.name()));
            }
        }

        return entries;
    }

    public List<BarEntry> getIncomeOrExpenseBarData(ArrayList<Account> accounts) {
        List<BarEntry> entries = new ArrayList<>();
        CategoriesEnum[] categories = CategoriesEnum.values();

        for (int i = 0; i < categories.length; i++) {
            float totalAmount = 0;

            for (Account account : accounts) {
                for (Transactions transaction : account.getListTrans()) {
                    if (transaction.isIncome() == isIncome && transaction.getCategory() == categories[i]) {
                        totalAmount += (float) transaction.getAmountValue();
                    }
                }
            }

            if (totalAmount > 0) {
                entries.add(new BarEntry(i, totalAmount, categories[i].name()));
            }
        }

        return entries;
    }


    private int[] getCategoryColors() {
        CategoriesEnum[] categories = CategoriesEnum.values();
        int[] colors = new int[categories.length];

        // Assegna un colore univoco a ciascuna categoria
        for (int i = 0; i < categories.length; i++) {
            switch (categories[i]) {
                case FoodAndDrinks:
                    colors[i] = Color.BLUE;
                    break;
                case Shopping:
                    colors[i] = Color.GREEN;
                    break;
                case House:
                    colors[i] = Color.RED;
                    break;
                case Transport:
                    colors[i] = Color.YELLOW;
                    break;
                case LifeAndEntertainment:
                    colors[i] = Color.MAGENTA;
                    break;
                case CommunicationAndPC:
                    colors[i] = Color.CYAN;
                    break;
                case Salary:
                    colors[i] = Color.LTGRAY;
                    break;
                case Gifts:
                    colors[i] = Color.DKGRAY;
                    break;
                case Other:
                    colors[i] = Color.BLACK;
                    break;
            }
        }

        return colors;
    }

}

