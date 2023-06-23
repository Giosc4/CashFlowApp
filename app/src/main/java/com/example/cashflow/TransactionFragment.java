package com.example.cashflow;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TransactionFragment extends Fragment {

    private EditText numberEditText;
    private Spinner accountSpinner;
    private EditText dateEditText;
    private EditText locationEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        // Inizializza le variabili EditText e Spinner
        numberEditText = view.findViewById(R.id.numberEditText);
        accountSpinner = view.findViewById(R.id.accountSpinner);
        dateEditText = view.findViewById(R.id.dateEditText);
        locationEditText = view.findViewById(R.id.locationEditText);

        // Aggiungi OnClickListener per il pulsante "DONE"
        Button doneButton = view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTransaction(); // Salva la transazione
            }
        });

        return view;
    }
    private void saveTransaction() {
        if (numberEditText != null && accountSpinner != null && dateEditText != null && locationEditText != null) {
            String number = numberEditText.getText() != null ? numberEditText.getText().toString() : "";
            String account = accountSpinner.getSelectedItem() != null ? accountSpinner.getSelectedItem().toString() : "";
            String date = dateEditText.getText() != null ? dateEditText.getText().toString() : "";
            String location = locationEditText.getText() != null ? locationEditText.getText().toString() : "";

            // Resto del codice per salvare la transazione
            Toast.makeText(getContext(), "Transaction saved: " + number + ", " + account + ", " + date + ", " + location, Toast.LENGTH_LONG).show();

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
