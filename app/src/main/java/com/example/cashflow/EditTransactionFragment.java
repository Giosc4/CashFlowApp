package com.example.cashflow;

import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.ArrayList;

public class EditTransactionFragment extends Fragment {

    private Button expenseButton;
    private Button incomeButton;
    private Spinner categorySpinner;
    private EditText numberEditText;
    private Spinner accountSpinner;
    private EditText dateEditText;
    private EditText locationEditText;
    private Button doneButton;
    private Transactions transactionOriginal;
    private Account accountOriginal;
    private ArrayList<Account> accounts;
    private ArrayList<String> categories;
    private JsonReadWrite jsonReadWrite;
    private int originalTransactionIndex;
    private int originalAccountIndex;

    public EditTransactionFragment(Transactions transaction, Account account) {
        this.transactionOriginal = transaction;
        this.accountOriginal = account;
        jsonReadWrite = new JsonReadWrite("test12.json");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_transaction, container, false);

        expenseButton = view.findViewById(R.id.expenseButton);
        incomeButton = view.findViewById(R.id.incomeButton);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        numberEditText = view.findViewById(R.id.numberEditText);
        accountSpinner = view.findViewById(R.id.accountSpinner);
        dateEditText = view.findViewById(R.id.dateEditText);
        locationEditText = view.findViewById(R.id.locationEditText);
        doneButton = view.findViewById(R.id.doneButton);

        if (transactionOriginal.isIncome()) {
            setIncome();
        } else {
            setExpense();
        }

        numberEditText.setText(String.valueOf(transactionOriginal.getAmount()));
        dateEditText.setText(transactionOriginal.getDate());
        locationEditText.setText(transactionOriginal.getCity());

        numberEditText.setFilters(new InputFilter[]{
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


        expenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIncome();
            }
        });

        incomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambia il colore del pulsante e imposta la sua proprietà "selected" a true
                setExpense();
            }
        });


        // Spinner CATEGORIES
        categories = new ArrayList<>();
        for (CategoriesEnum category : CategoriesEnum.values()) {
            categories.add(category.name());
        }
        this.originalTransactionIndex = categories.indexOf(transactionOriginal.getCategory().name());


        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(originalTransactionIndex);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Selected Category: " + selectedCategory, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Codice da eseguire quando non viene selezionato nessun elemento
            }
        });

        accounts = jsonReadWrite.readAccountsFromJson(requireContext());

        //SPINNER ACCOUNTS
        ArrayList<String> accountNames = new ArrayList<>();
        for (Account account : accounts) {
            accountNames.add(account.getName());
        }
        this.originalAccountIndex = accountNames.indexOf(accountOriginal.getName() + "");


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, accountNames);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountSpinner.setAdapter(dataAdapter);
        accountSpinner.setSelection(originalAccountIndex);

        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedAccount = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Conto Selezionato: " + selectedAccount, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Codice da eseguire quando non viene selezionato nessun elemento
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updateTransaction();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return view;
    }

    private void setIncome() {
        // Cambia il colore del pulsante e imposta la sua proprietà "selected" a true
        expenseButton.setSelected(true);
        expenseButton.setBackgroundColor(Color.parseColor("#00cc44")); // Verde quando selezionato
        incomeButton.setSelected(false);
        incomeButton.setBackgroundColor(Color.parseColor("#e06666")); // rosso quando non selezionato
    }

    private void setExpense() {
        incomeButton.setSelected(true);
        incomeButton.setBackgroundColor(Color.parseColor("#00cc44")); // Verde quando selezionato
        expenseButton.setSelected(false);
        expenseButton.setBackgroundColor(Color.parseColor("#e06666")); // rosso quando non selezionato
    }

    private void updateTransaction() throws IOException {
        boolean income = incomeButton.isSelected();
        double amount = Double.parseDouble(numberEditText.getText().toString());
        String date = dateEditText.getText().toString();
        String city = locationEditText.getText().toString();
        CategoriesEnum category = CategoriesEnum.values()[categorySpinner.getSelectedItemPosition()];
        int selectedAccountIndex = accountSpinner.getSelectedItemPosition();

        Transactions newTrans = new Transactions(income, amount, date, city, category);

        accountOriginal.removeTransaction(transactionOriginal);
        accountOriginal.getListTrans().add(newTrans);
        System.out.println("Transaction Original: " + transactionOriginal.toString());


        Account selectedAccount = accounts.get(selectedAccountIndex);
        selectedAccount.getListTrans().add(newTrans);
        selectedAccount.updateBalance();

        accounts.set(selectedAccountIndex, selectedAccount);

        jsonReadWrite.setList(accounts, requireContext());

        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
            LinearLayout mainLayout = getActivity().findViewById(R.id.mainLayout);
            mainLayout.setVisibility(View.VISIBLE);
        }

        Toast.makeText(getContext(), "Transaction updated", Toast.LENGTH_LONG).show();
    }


}
