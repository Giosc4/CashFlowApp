package com.example.cashflow;

import com.example.cashflow.dataClass.Account;
import com.example.cashflow.statistics.Income_expense;
import com.example.cashflow.statistics.MapFragment;
import com.example.cashflow.statistics.Line_chart;


import android.annotation.SuppressLint;
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
    private Button google_maps;
    private Button incomeButton;
    private Button expenseButton;

    public StatisticsFragment(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        btnLineChart = view.findViewById(R.id.btnLineChart);
        google_maps = view.findViewById(R.id.google_maps);
        incomeButton = view.findViewById(R.id.incomeButton);
        expenseButton = view.findViewById(R.id.expenseButton);


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
                Income_expense incomeExpense = new Income_expense(true, accounts);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, incomeExpense)
                        .commit();
            }
        });

        expenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Income_expense incomeExpense = new Income_expense(false, accounts);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, incomeExpense)
                        .commit();
            }
        });
        return view;
    }

}
