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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TransactionFragment extends Fragment {

    private Button expenseButton;
    private Button incomeButton;
    private EditText numberEditText;
    private Spinner accountSpinner;
//    private Account accountSelected;
        private String accountSelected;

    private EditText dateEditText;
    private EditText locationEditText;

    JsonReadWrite jsonReadWrite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        // Initialize the EditText, Spinner, and Button variables
        expenseButton = view.findViewById(R.id.expenseButton);
        incomeButton = view.findViewById(R.id.incomeButton);
        numberEditText = view.findViewById(R.id.numberEditText);
        accountSpinner = view.findViewById(R.id.accountSpinner);
        dateEditText = view.findViewById(R.id.dateEditText);
        locationEditText = view.findViewById(R.id.locationEditText);
        accountSpinner = view.findViewById(R.id.accountSpinner);




        // Set the input filter on numberEditText double for prices
        numberEditText.setFilters(new InputFilter[] {
                new InputFilter() {
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        for (int i = start; i < end; i++) {
                            if (!Character.isDigit(source.charAt(i)) && source.charAt(i) != '.') {
                                return "";
                            }
                        }

                        // Limit decimal digits to 2
                        if (source.toString().contains(".")) {
                            String[] split = dest.toString().split("\\.");
                            if (split.length > 1 && split[1].length() >= 2) {
                                return "";
                            }
                        }

                        return null;
                    }
                }
        });

        // Imposta OnClickListener per i pulsanti "EXPENSE" e "INCOME"
        expenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambia il colore del pulsante e imposta la sua proprietà "selected" a true
                expenseButton.setSelected(true);
                expenseButton.setBackgroundColor(Color.parseColor("#00FF00")); // Verde quando selezionato
                incomeButton.setSelected(false);
                incomeButton.setBackgroundColor(Color.parseColor("#800080")); // Viola quando non selezionato
            }
        });

        incomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambia il colore del pulsante e imposta la sua proprietà "selected" a true
                incomeButton.setSelected(true);
                incomeButton.setBackgroundColor(Color.parseColor("#00FF00")); // Verde quando selezionato
                expenseButton.setSelected(false);
                expenseButton.setBackgroundColor(Color.parseColor("#800080")); // Viola quando non selezionato
            }
        });

        jsonReadWrite = new JsonReadWrite();

        // da qui bisogna leggere la lista di account


        // Creazione di una lista di conti
        List<String> accounts = new ArrayList<>();
        accounts.add("Conto 1");
        accounts.add("Conto 2");
        accounts.add("Conto 3");
        // Aggiungi quanti conti desideri...

        // Creazione di un ArrayAdapter usando la lista di conti
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, accounts);

        // Impostazione del layout per quando lo Spinner viene visualizzato
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Impostazione dell'ArrayAdapter come adapter per lo Spinner
        accountSpinner.setAdapter(dataAdapter);

        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedAccount = parent.getItemAtPosition(position).toString();
                // Ora 'selectedAccount' è l'account selezionato
                Toast.makeText(parent.getContext(), "Conto Selezionato: " + selectedAccount, Toast.LENGTH_LONG).show();
                accountSelected = selectedAccount;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Codice da eseguire quando non viene selezionato nessun elemento
            }
        });



        // Add OnClickListener for the "DONE" button
        Button doneButton = view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveTransaction(); // Save the transaction
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return view;
    }

    private void saveTransaction() throws IOException {
            if (numberEditText != null && accountSpinner != null && dateEditText != null && locationEditText != null && accountSelected != null) {

                boolean income = incomeButton.isSelected();
                boolean expense = expenseButton.isSelected();

                // Verifica se almeno uno dei pulsanti è stato premuto
                if (!income && !expense) {
                    // Nessun pulsante selezionato, mostra un messaggio di avviso o gestisci l'errore
                    Toast.makeText(getContext(), "Please select either Income or Expense", Toast.LENGTH_SHORT).show();
                    return; // Esce dal metodo senza salvare la transazione
                }
            String number = numberEditText.getText() != null ? numberEditText.getText().toString() : "";
            String account = accountSpinner.getSelectedItem() != null ? accountSpinner.getSelectedItem().toString() : "";
            String date = dateEditText.getText() != null ? dateEditText.getText().toString() : "";
            String location = locationEditText.getText() != null ? locationEditText.getText().toString() : "";

            //Transactions transaction = new Transactions(number, date, location);
            // Resto del codice per salvare la transazione
            Toast.makeText(getContext(), "Transaction saved: " + number + ", " + account + ", " + date + ", " + location, Toast.LENGTH_LONG).show();

                //jsonReadWrite.addTransaction(requireContext(), income, number, date, location, accountSelected);

                System.out.println(jsonReadWrite.readJsonFromFile(requireContext(), "test2122.json"));

                // il file che legge è vuoto


                if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
                LinearLayout mainLayout = getActivity().findViewById(R.id.mainLayout);
                mainLayout.setVisibility(View.VISIBLE);
            }
        } else {
            // Gestisci il caso in cui uno dei componenti UI sia nullo
        }
    }
}
