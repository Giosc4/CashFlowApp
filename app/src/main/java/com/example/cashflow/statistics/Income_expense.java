package com.example.cashflow.statistics;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.cashflow.R;
import com.example.cashflow.SQLiteDB;
import com.example.cashflow.dataClass.Account;
import com.example.cashflow.dataClass.Category;
import com.example.cashflow.dataClass.Transactions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class Income_expense extends Fragment {
    private final Boolean isIncome;
    private ArrayList<Account> accounts;

    private TextView title;
    private CheckBox accountsCheckBox;
    private RecyclerView accountsRecyclerView;
    private AccountsAdapter accountsAdapter;

    private ArrayList<Account> selectedAccounts;
    private PieChart pieChart;
    private BarChart barChart;

    private SQLiteDB sqLiteDB;

    public Income_expense(Boolean isIncome, SQLiteDB sqLiteDB) {
        this.isIncome = isIncome;
        this.sqLiteDB = sqLiteDB;
        this.accounts = sqLiteDB.getAllAccounts();
        selectedAccounts = new ArrayList<>();
    }
/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income_expense, container, false);

        accountsCheckBox = view.findViewById(R.id.accountsCheckBox);
        accountsRecyclerView = view.findViewById(R.id.accountsRecyclerView);
        pieChart = view.findViewById(R.id.pieChart);
        barChart = view.findViewById(R.id.barChart);
        title = view.findViewById(R.id.title);

        if (isIncome) {
            title.setText("INCOME");
        } else {
            title.setText("EXPENSE");
        }

        accountsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Controlla lo stato del checkbox e aggiorna l'adapter di conseguenza
            for (int i = 0; i < accountsAdapter.getItemCount(); i++) {
                accountsAdapter.setSelected(i, isChecked);
            }

            // Aggiorna i grafici
            updateSelectedAccounts();
            initPieChart(selectedAccounts);
            initBarChart(selectedAccounts);
        });

        // Popola l'array di nomi degli account
        ArrayList<String> accountNames = new ArrayList<>();
        for (Account account : accounts) {
            accountNames.add(account.getName());
        }

        // Inizializza l'adapter personalizzato per il RecyclerView
        accountsAdapter = new AccountsAdapter(accountNames);
        accountsRecyclerView.setAdapter(accountsAdapter);

        // Imposta un layout manager per il RecyclerView
        accountsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        accountsAdapter.setOnItemClickListener(position -> {
            // Aggiorna i grafici quando viene selezionato un elemento nel RecyclerView
            updateSelectedAccounts();
            initPieChart(selectedAccounts);
            initBarChart(selectedAccounts);
        });

        if (selectedAccounts.isEmpty()) {
            accountsCheckBox.setChecked(true);
            initPieChart(accounts);
            initBarChart(accounts);
        }

        return view;
    }


    private void updateSelectedAccounts() {
        selectedAccounts.clear();
        for (int i = 0; i < accountsAdapter.getItemCount(); i++) {
            if (accountsAdapter.isSelected(i)) {
                selectedAccounts.add(accounts.get(i));
            }
        }

    }

    private void initPieChart(ArrayList<Account> selectedAccounts) {
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(android.R.color.transparent);
        pieChart.setTransparentCircleRadius(50f);
        pieChart.setRotationEnabled(true);
        pieChart.setExtraTopOffset(30f);
        pieChart.setUsePercentValues(true);

        PieDataSet pieDataSet = new PieDataSet(getIncomeOrExpensePieData(selectedAccounts), "");
        pieDataSet.setColors(getCategoryColors());
        pieDataSet.setDrawValues(false);

        // Configura la legenda
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setWordWrapEnabled(true);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
    }


    // Inizializza il grafico a barre
    private void initBarChart(ArrayList<Account> selectedAccounts) {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getLegend().setEnabled(false);

        BarDataSet barDataSet = new BarDataSet(getIncomeOrExpenseBarData(selectedAccounts), "");
        int[] colors = getCategoryColors();
        barDataSet.setColors(colors);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
    }

    public List<PieEntry> getIncomeOrExpensePieData(ArrayList<Account> accounts) {
        List<PieEntry> entries = new ArrayList<>();
        String[] categoriesList = sqLiteDB.getCategories();
        for (Category categor : categoriesList) {
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

        ArrayList<Category> categories = sqLiteDB.getCategories();

        for (int i = 0; i < categories.size(); i++) {
            float totalAmount = 0;

            for (Account account : accounts) {
                totalAmount += account.getListTrans().stream()
                        .filter(transaction -> transaction.isIncome() == isIncome && transaction.getCategory() == categories[i])
                        .mapToDouble(Transactions::getAmountValue)
                        .sum();
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
*/
}

