package com.example.cashflow;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cashflow.R;
import com.example.cashflow.dataClass.Account;

import java.util.ArrayList;
import java.util.Calendar;

public class NewDebitCreditFragment extends Fragment {

    private EditText editTextName;
    private EditText editTextAmount;
    private EditText editTextContact;
    private EditText editTextDescription;
    private Spinner accountSpinner;

    private Button buttonStartDate;
    private Button buttonEndDate;
    private Button buttonNewDebit;
    private Button buttonNewCredit;
    private ArrayList<Account> accounts;

    public NewDebitCreditFragment(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_debit_credit, container, false);

        // Initialize UI components
        editTextName = view.findViewById(R.id.editTextName);
        editTextAmount = view.findViewById(R.id.editTextAmount);
        editTextContact = view.findViewById(R.id.editTextContact);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        accountSpinner = view.findViewById(R.id.accountSpinner);

        buttonStartDate = view.findViewById(R.id.buttonStartDate);
        buttonEndDate = view.findViewById(R.id.buttonEndDate);
        buttonNewDebit = view.findViewById(R.id.buttonNewDebit);
        buttonNewCredit = view.findViewById(R.id.buttonNewCredit);

        editTextAmount.setFilters(new InputFilter[]{
                new InputFilter() {
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        // Check if the input contains a decimal point
                        boolean hasDecimalSeparator = dest.toString().contains(".");

                        // Get the current number of decimal places
                        int decimalPlaces = 0;
                        if (hasDecimalSeparator) {
                            String[] split = dest.toString().split("\\.");
                            if (split.length > 1) {
                                decimalPlaces = split[1].length();
                            }
                        }

                        // Check if the input is a valid decimal number
                        for (int i = start; i < end; i++) {
                            char inputChar = source.charAt(i);

                            // Allow digits and a decimal point
                            if (!Character.isDigit(inputChar) && inputChar != '.') {
                                return "";
                            }

                            // Allow only two decimal places
                            if (hasDecimalSeparator && decimalPlaces >= 2) {
                                return "";
                            }

                            // Increment the decimal places count if a decimal point is encountered
                            if (inputChar == '.') {
                                hasDecimalSeparator = true;
                            } else if (hasDecimalSeparator) {
                                decimalPlaces++;
                            }
                        }

                        return null;
                    }
                }
        });

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
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // SE NULLA è SELEZIONATO ALLORA VIENE PRESO IL PRIMO ACCOUNT
            }

        });

        // Set click listeners for date buttons
        buttonStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for selecting start date
                selectDate("Start Date");
            }
        });

        buttonEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for selecting end date
                selectDate("End Date");
            }
        });

        // Set click listeners for new debit and credit buttons
        buttonNewDebit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for adding new debit
                addNewDebit();
            }
        });

        buttonNewCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for adding new credit
                addNewCredit();
            }
        });

        return view;
    }

    // Method to open a calendar for selecting date
    private void selectDate(String dateType) {
        // You can implement your logic here to open a calendar and select a date
        // For example, you can use DatePickerDialog
        // This is just a placeholder method
        Toast.makeText(getContext(), "Select " + dateType + " from calendar", Toast.LENGTH_SHORT).show();
    }

    // Method to add new debit
    private void addNewDebit() {
        // You can implement your logic here to add a new debit
        // This is just a placeholder method
        Toast.makeText(getContext(), "Add new debit", Toast.LENGTH_SHORT).show();
    }

    // Method to add new credit
    private void addNewCredit() {
        // You can implement your logic here to add a new credit
        // This is just a placeholder method
        Toast.makeText(getContext(), "Add new credit", Toast.LENGTH_SHORT).show();
    }
}
