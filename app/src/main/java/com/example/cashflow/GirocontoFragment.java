package com.example.cashflow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cashflow.dataClass.Account;

import java.util.ArrayList;
public class GirocontoFragment extends Fragment {

    private Spinner accountSpinnerProv;
    private Spinner accountSpinnerArrivo;
    private Button saveGirocontoButton;

    private ArrayList<Account> accounts;

    public GirocontoFragment(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_giroconto, container, false);

        accountSpinnerProv = view.findViewById(R.id.accountSpinnerProv);
        accountSpinnerArrivo = view.findViewById(R.id.accountSpinnerArrivo);
        saveGirocontoButton = view.findViewById(R.id.saveGirocontoButton);

        //SPINNER ACCOUNTS
        ArrayList<String> accountNames = new ArrayList<>();
        for (Account account : accounts) {
            accountNames.add(account.getName());
        }

        ArrayAdapter<String> dataAdapterProv = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, accountNames);
        dataAdapterProv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountSpinnerProv.setAdapter(dataAdapterProv);

        accountSpinnerProv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedAccountProv = parent.getItemAtPosition(position).toString();
                ArrayList<String> accountNamesArrivo = new ArrayList<>(accountNames);
                accountNamesArrivo.remove(selectedAccountProv);

                ArrayAdapter<String> dataAdapterArrivo = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, accountNamesArrivo);
                dataAdapterArrivo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                accountSpinnerArrivo.setAdapter(dataAdapterArrivo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle nothing selected
            }
        });

        saveGirocontoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for saving giroconto
                String selectedAccountProv = accountSpinnerProv.getSelectedItem().toString();
                String selectedAccountArrivo = accountSpinnerArrivo.getSelectedItem().toString();
                // Perform actions with selected accounts
                getFragmentManager().popBackStack();

            }
        });

        return view;
    }
}
