package com.example.cashflow.statistics;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.cashflow.R;
import com.example.cashflow.dataClass.Account;
import com.example.cashflow.dataClass.Transactions;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
/*

public class Line_chart extends Fragment {

    private Button openStartDatePickerButton;
    private Button openEndDatePickerButton;
    private Button generateChartButton;

    private TextView startDateTextView;
    private TextView endDateTextView;

    private Calendar startDate;
    private Calendar endDate;


    private LineChart lineChart;

    private ArrayList<Account> accounts;
    public Line_chart(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_line_chart, container, false);
        generateChartButton = view.findViewById(R.id.generateChartButton);
        openStartDatePickerButton = view.findViewById(R.id.openStartDatePickerButton);
        openEndDatePickerButton = view.findViewById(R.id.openEndDatePickerButton);
        startDateTextView = view.findViewById(R.id.startDateTextView);
        endDateTextView = view.findViewById(R.id.endDateTextView);

        lineChart = view.findViewById(R.id.lineChart);

        // Calcola le date iniziali e finali
        startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, -6);
        endDate = Calendar.getInstance();
        endDate.add(Calendar.DAY_OF_MONTH, 1);

        startDateTextView.setText(formatDateKey(startDate));
        endDateTextView.setText(formatDateKey(endDate));

        openStartDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStartDatePicker();
            }
        });

        openEndDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEndDatePicker();
            }
        });

        generateChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createLineChart();
            }
        });

        return view;
    }

    private void openStartDatePicker() {
        int year = startDate.get(Calendar.YEAR);
        int month = startDate.get(Calendar.MONTH);
        int day = startDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);

                if (selectedDate.before(endDate)) {
                    startDate.set(year, month, dayOfMonth);
                    // Imposta la data selezionata nel TextView
                    startDateTextView.setText(formatDateKey(startDate));
                }
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void openEndDatePicker() {
        int year = endDate.get(Calendar.YEAR);
        int month = endDate.get(Calendar.MONTH);
        int day = endDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);

                if (selectedDate.after(startDate)) {
                    endDate.set(year, month, dayOfMonth);
                    // Imposta la data selezionata nel TextView
                    endDateTextView.setText(formatDateKey(endDate));
                }
            }
        }, year, month, day);

        datePickerDialog.show();
    }


    private void createLineChart() {
        ArrayList<Entry> entries = generateDataEntries(startDate, endDate);

        // Crea un elenco di etichette delle date per l'asse X
        ArrayList<String> dateLabels = new ArrayList<>();
        for (Calendar date = (Calendar) startDate.clone(); date.compareTo(endDate) <= 0; date.add(Calendar.DAY_OF_MONTH, 1)) {
            dateLabels.add(formatDateKeyWithoutYear(date));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Daily Total");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(Color.parseColor("#00796B"));
        dataSet.setValueTextSize(12f);
        dataSet.setLineWidth(2f);
        dataSet.setValueTypeface(Typeface.DEFAULT_BOLD);

        LineData lineData = new LineData(dataSet);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Configura il formatter per le etichette dell'asse X
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < dateLabels.size()) {
                    return dateLabels.get(index);
                } else {
                    return "";
                }
            }
        });

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setGranularity(1f);

        lineChart.setData(lineData);
        lineChart.getLegend().setEnabled(false);
        lineChart.invalidate();
    }

    private ArrayList<Entry> generateDataEntries(Calendar startDate, Calendar endDate) {
        ArrayList<Entry> entries = new ArrayList<>();

        // Inizializza il saldo iniziale con 0
        double initialBalance = 0;

        // Copia la data di inizio in una variabile temporanea per usarla durante l'iterazione
        Calendar currentDate = (Calendar) startDate.clone();

        // Itera su tutte le date da startDate a endDate
        while (currentDate.compareTo(endDate) <= 0) {
            // Inizializza la data corrente
            String dayKey = formatDateKeyWithoutYear(currentDate);

            // Calcola il totale delle transazioni per questa data
            double dailyTotal = 0;

            for (Account account : accounts) {
                for (Transactions transaction : account.getListTrans()) {
                    Calendar transactionDate = transaction.getDate();
                    if (isSameDay(transactionDate, currentDate)) {
                        dailyTotal += transaction.getAmount();
                    }
                }
            }

            // Calcola il saldo per questa data come saldo iniziale + totale delle transazioni
            double dailyBalance = initialBalance + dailyTotal;

            // Aggiungi il saldo al grafico
            entries.add(new Entry(entries.size() + 1, (float) dailyBalance));

            // Aggiorna il saldo iniziale per il prossimo giorno
            initialBalance = dailyBalance;

            // Vai alla data successiva
            currentDate.add(Calendar.DAY_OF_MONTH, 1);
        }

        return entries;
    }

    // Funzione per verificare se due date sono dello stesso giorno
    private boolean isSameDay(Calendar date1, Calendar date2) {
        return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
                date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH) &&
                date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH);
    }

    private String formatDateKey(Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        return dateFormat.format(date.getTime());
    }

    private String formatDateKeyWithoutYear(Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM", Locale.US);
        return dateFormat.format(date.getTime());
    }
}
*/
