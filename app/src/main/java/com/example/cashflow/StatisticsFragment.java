

package com.example.cashflow;
/*

import com.example.cashflow.dataClass.Account;
import com.example.cashflow.dataClass.Transactions;
import com.example.cashflow.statistics.Income_expense;
import com.example.cashflow.statistics.MapFragment;
import com.example.cashflow.statistics.Line_chart;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class StatisticsFragment extends Fragment {

    private ArrayList<Account> accounts;
    private Button btnLineChart;
    private Button google_maps;
    private Button incomeButton;
    private Button expenseButton;
    private Button btnCSVFileDownload;
    private SQLiteDB sqLiteDB;

/*

    public StatisticsFragment(SQLiteDB sqLiteDB) {
        this.sqLiteDB = sqLiteDB;
        this.accounts = sqLiteDB.getAllAccounts();
    }
/*
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        btnLineChart = view.findViewById(R.id.btnLineChart);
        google_maps = view.findViewById(R.id.google_maps);
        incomeButton = view.findViewById(R.id.incomeButton);
        expenseButton = view.findViewById(R.id.expenseButton);
        btnCSVFileDownload = view.findViewById(R.id.btnCSVFileDownload);


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
                        .replace(R.id.fragment_container, mapFragment)
                        .commit();
            }
        });

        incomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Income_expense incomeExpense = new Income_expense(true, sqLiteDB);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, incomeExpense)
                        .commit();
            }
        });

        expenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Income_expense incomeExpense = new Income_expense(false, sqLiteDB);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, incomeExpense)
                        .commit();
            }
        });

        btnCSVFileDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the function to save the CSV file

                try {
                    if (saveToCSV(accounts)) {
                        Toast.makeText(getActivity(), "File CSV Salvato", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Inserisci il nome dell'account", Toast.LENGTH_SHORT).show();

                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return view;
    }

    public boolean saveToCSV(ArrayList<Account> accounts) throws IOException {
        // Check if external storage is available and writable
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Get the directory path for the "Download" folder
            File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            if (!downloadDirectory.exists() && !downloadDirectory.mkdirs()) {
                throw new IOException("Cannot create 'Download' directory");
            }

            // Generate a unique file name using the current timestamp
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(new Date());
            String fileName = "CashFlowApp_" + timestamp + ".csv";

            // Specify the file path
            File file = new File(downloadDirectory, fileName);

            try (FileWriter writer = new FileWriter(file)) {
                // Write CSV header
                writer.write("Name, Balance, Transaction Type, Amount, Date, City, Category\n");

                // Write account data to the file
                for (Account account : accounts) {
                    String accountName = account.getName();
                    double accountBalance = account.getBalance();

                    for (Transactions transaction : sqLiteDB.getAllTransactions()) {
                        String transactionType = transaction.isIncome() ? "INCOME" : "EXPENSE";
                        double transactionAmount = transaction.getAmountValue();
                        String transactionDate = transaction.getDate().getTime().toString();
                        int cityId = transaction.getCityId();
                        int categoryId = transaction.getCategoryId();
                        String cityName = sqLiteDB.getCityNameById(cityId);
                        String categoryName = sqLiteDB.getCategoryNameById(categoryId);

                        String csvData = String.format("%s,%.2f,%s,%.2f,%s,%s,%s\n",
                                accountName, accountBalance, transactionType, transactionAmount, transactionDate, cityName, categoryName);
                        writer.write(csvData);
                    }
                }
            } catch (IOException e) {
                // Handle the exception
                e.printStackTrace();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            return true;
        } else {
            return false;
        }
    }
}
*/

