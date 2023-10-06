package com.example.cashflow;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EditTransactionFragment extends Fragment {

    private Button expenseButton;
    private Button incomeButton;
    private Spinner categorySpinner;
    private EditText numberEditText;

    // NON C'E' cameraButton

    private Spinner accountSpinner;

    private Button dateTimeButton;

    private TextView selectedTimeTextView;

    private Calendar calendar;

    private EditText locationEditText;
    private Button doneButton;
    private Button deleteButton;

    //CONSTRUCTOR
    private Transactions transactionOriginal;
    private Account accountOriginal;
    private ArrayList<String> categories;

    //GET FROM JSON

    private JsonReadWrite jsonReadWrite;
    private ArrayList<Account> accounts;

    //CHANGE ACCCOUNT TRANS
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
        selectedTimeTextView = view.findViewById(R.id.selectedTimeTextView);
        locationEditText = view.findViewById(R.id.locationEditText);
        doneButton = view.findViewById(R.id.doneButton);
        deleteButton = view.findViewById(R.id.deleteButton);
        dateTimeButton = view.findViewById(R.id.dateTimeButton);
        calendar = transactionOriginal.getDate();

        String str = "";
        if (transactionOriginal.getAmount() < 0) {
            str = String.valueOf(transactionOriginal.getAmount());
            str = str.replace("-", "");
            setExpense();
        } else {
            str = String.valueOf(transactionOriginal.getAmount());
            setIncome();
        }

        numberEditText.setText(str);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String selectedDateString = dateFormat.format(transactionOriginal.getDate().getTime());
        selectedTimeTextView.setText(selectedDateString);


        locationEditText.setText(transactionOriginal.getCity());

        dateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

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
                setExpense();
            }
        });

        incomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambia il colore del pulsante e imposta la sua proprietà "selected" a true
                setIncome();
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

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTransaction();
            }
        });


        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTransaction();
            }
        });

        return view;
    }

    private void setExpense() {
        // Cambia il colore del pulsante e imposta la sua proprietà "selected" a true
        expenseButton.setSelected(true);
        expenseButton.setBackgroundColor(Color.parseColor("#00cc44")); // Verde quando selezionato
        incomeButton.setSelected(false);
        incomeButton.setBackgroundColor(Color.parseColor("#e06666")); // rosso quando non selezionato
    }

    private void setIncome() {
        incomeButton.setSelected(true);
        incomeButton.setBackgroundColor(Color.parseColor("#00cc44")); // Verde quando selezionato
        expenseButton.setSelected(false);
        expenseButton.setBackgroundColor(Color.parseColor("#e06666")); // rosso quando non selezionato
    }

    private void deleteTransaction() {
        // Rimuovi la transazione originale dall'account originale
        accountOriginal.removeTransaction(transactionOriginal);
        accounts.set(originalAccountIndex, accountOriginal);

        try {
            // Esegui il salvataggio dell'account originale nel file JSON dopo la rimozione della transazione
            jsonReadWrite.setList(accounts, requireContext());

            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
                LinearLayout mainLayout = getActivity().findViewById(R.id.mainLayout);
                mainLayout.setVisibility(View.VISIBLE);
            }

            Toast.makeText(getContext(), "Transaction deleted", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Errore durante il salvataggio delle modifiche", Toast.LENGTH_LONG).show();
        }
    }

    private void updateTransaction() {
        boolean newIncome = incomeButton.isSelected();
        double newAmount = Double.parseDouble(numberEditText.getText().toString());
        String newCity = locationEditText.getText().toString();
        CategoriesEnum newCategory = CategoriesEnum.values()[categorySpinner.getSelectedItemPosition()];
        int newAccountIndex = accountSpinner.getSelectedItemPosition();
        Calendar newDate = calendar;
        
// crea la nuova transazione, verifica se è nello stesso account
        // SI: sostituisce la vecchia transazione con la nuova
        // NO: elimina la transazione dall'account originale, lo aggiunge all'account selezionato
        //sostituisce l'account originale con quello con la transazione aggiornata e poi salva tutto su JSON

        Transactions newTrans = new Transactions(newIncome, newAmount, newDate, newCity, newCategory);
        if (newAccountIndex != originalAccountIndex) {
            accountOriginal.removeTransaction(transactionOriginal);
            accounts.get(newAccountIndex).addTransaction(newTrans);
        } else {
            accountOriginal.editTransaction(transactionOriginal, newTrans);
        }
        try {
            // Esegui il salvataggio dei dati qui, dopo aver apportato tutte le modifiche
            accounts.set(originalAccountIndex, accountOriginal);
            jsonReadWrite.setList(accounts, requireContext());

            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
                LinearLayout mainLayout = getActivity().findViewById(R.id.mainLayout);
                mainLayout.setVisibility(View.VISIBLE);
            }
            Toast.makeText(getContext(), "Transaction updated", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Errore durante il salvataggio delle modifiche", Toast.LENGTH_LONG).show();
        }

    }


    private void showDatePickerDialog() {
        Calendar newCalendar = Calendar.getInstance();
        int year = newCalendar.get(Calendar.YEAR);
        int month = newCalendar.get(Calendar.MONTH);
        int dayOfMonth = newCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                newCalendar.set(Calendar.YEAR, year);
                newCalendar.set(Calendar.MONTH, monthOfYear);
                newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                calendar = newCalendar; // Imposta la variabile globale

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String selectedDateString = dateFormat.format(calendar.getTime());
                System.out.println(selectedDateString + " selectedDateString");
                selectedTimeTextView.setText(selectedDateString);
            }
        }, year, month, dayOfMonth);

        // Mostra il dialog per la selezione della data
        datePickerDialog.show();
    }


}
