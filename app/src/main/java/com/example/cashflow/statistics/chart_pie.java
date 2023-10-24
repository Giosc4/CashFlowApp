package com.example.cashflow.statistics;

import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cashflow.dataClass.Account;
import com.example.cashflow.dataClass.CategoriesEnum;
import com.example.cashflow.R;
import com.example.cashflow.dataClass.Transactions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class chart_pie extends Fragment {

    private ArrayList<Account> accounts;
    private ListView categoryListView;
    private PieChart pieChart;
    private BarChart barChart;

    private Button clearSelectionButton;
    private Button selectCategoriesButton;

    public chart_pie(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla il layout del fragment per il grafico a torta
        View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);

        // Trova il tuo grafico a torta nella vista inflata
        pieChart = view.findViewById(R.id.pieChart);
        barChart = view.findViewById(R.id.barChart);

        // Trova la ListView delle categorie nella vista inflata
        categoryListView = view.findViewById(R.id.categoryListView);

        // Trova i pulsanti nella vista inflata
        clearSelectionButton = view.findViewById(R.id.clearSelectionButton);
        selectCategoriesButton = view.findViewById(R.id.selectCategoriesButton);

        // Crea un adattatore per la ListView delle categorie
        CategoriesEnum[] categories = CategoriesEnum.values();
        ArrayAdapter<CategoriesEnum> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_multiple_choice, categories);
        categoryListView.setAdapter(adapter);

        // Imposta un listener per il pulsante "Cancella Selezione"
        clearSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Deseleziona tutte le categorie nella ListView
                for (int i = 0; i < categoryListView.getCount(); i++) {
                    categoryListView.setItemChecked(i, false);
                }
                // Richiama la funzione per aggiornare il grafico a torta con le categorie deselezionate
                updatePieChart();
            }
        });

        // Imposta un listener per il pulsante "Seleziona Categorie"
        selectCategoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementa qui la logica per selezionare categorie specifiche, se necessario
                // Ad esempio, puoi mostrare un dialogo per selezionare le categorie.
                updatePieChart();
            }
        });

        // Chiama la funzione per inizializzare il grafico a torta con tutte le categorie
        popolaTorta(null);
        popolaBarre(null);


        return view;
    }

    private void updatePieChart() {
        // Ottieni le categorie selezionate dall'utente
        SparseBooleanArray checkedItems = categoryListView.getCheckedItemPositions();
        List<CategoriesEnum> selectedCategories = new ArrayList<>();

        for (int i = 0; i < checkedItems.size(); i++) {
            int key = checkedItems.keyAt(i);
            if (checkedItems.get(key)) {
                selectedCategories.add(CategoriesEnum.values()[key]);
            }
        }

        // Richiama la funzione per popolare il grafico a torta con le categorie selezionate
        popolaTorta(selectedCategories);
        popolaBarre(selectedCategories);
    }
    private void popolaBarre(List<CategoriesEnum> selectedCategories) {
        // Crea una lista di BarEntry con i dati desiderati
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        float[] listForCategory = new float[CategoriesEnum.values().length];

        // Scorrere gli account e le transazioni per calcolare le somme
        for (Account account : accounts) {
            for (Transactions transaction : account.getListTrans()) {
                CategoriesEnum categoria = transaction.getCategory();
                float importo = (float) transaction.getAmount();

                if (selectedCategories == null || selectedCategories.isEmpty() || selectedCategories.contains(categoria)) {
                    // Aggiungi l'importo alla lista corretta in base al segno
                    int index = categoria.ordinal();
                    listForCategory[index] += importo;
                }
            }
        }

        // Aggiungi le voci al grafico a barre
        for (int i = 0; i < listForCategory.length; i++) {
            barEntries.add(new BarEntry(i, listForCategory[i]));
        }

        // Imposta i dati del grafico a barre
        BarDataSet dataSet = new BarDataSet(barEntries, "Category");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Utilizza colori casuali

        BarData barData = new BarData(dataSet);

        // Imposta le etichette sull'asse X con il nome delle categorie
        String[] labels = new String[barEntries.size()];
        for (int i = 0; i < barEntries.size(); i++) {
            labels[i] = CategoriesEnum.values()[(int) barEntries.get(i).getX()].toString();
        }
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        // Imposta il grafico a barre
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.invalidate();
    }


    private void popolaTorta(List<CategoriesEnum> selectedCategories) {
        // Crea una lista di PieEntry con i dati desiderati
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        float[] listForCategory = new float[CategoriesEnum.values().length];

        // Scorrere gli account e le transazioni per calcolare le somme
        for (Account account : accounts) {
            for (Transactions transaction : account.getListTrans()) {
                CategoriesEnum categoria = transaction.getCategory();
                float importo = (float) transaction.getAmount();

                if (selectedCategories == null || selectedCategories.isEmpty() || selectedCategories.contains(categoria)) {
                    // Se non ci sono categorie selezionate o la categoria è selezionata, aggiungi l'importo
                    int index = categoria.ordinal();
                    listForCategory[index] += importo;
                }
            }
        }

        // Aggiungi le voci del grafico a torta solo se il totale è diverso da zero
        for (int i = 0; i < listForCategory.length; i++) {
            if (listForCategory[i] > 0f) {
                String nomeCategoria = CategoriesEnum.values()[i].toString() + "\n €" + listForCategory[i];
                pieEntries.add(new PieEntry(listForCategory[i], nomeCategoria));
            }
        }

        // Imposta i dati del grafico a torta
        PieDataSet dataSet = new PieDataSet(pieEntries, "Category");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData(dataSet);

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }


}
