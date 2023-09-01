package com.example.cashflow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
import java.util.ArrayList;

public class NewAccountFragment extends Fragment {

    private JsonReadWrite jsonReadWrite;
    private ArrayList<Account> accounts;

    public NewAccountFragment(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_account, container, false);

        jsonReadWrite = new JsonReadWrite("test12.json");

        final EditText edtName = view.findViewById(R.id.edtName);
        final EditText edtCurrency = view.findViewById(R.id.edtCurrency);
        final EditText edtColor = view.findViewById(R.id.edtColor);
        Button btnCreateAccount = view.findViewById(R.id.btnCreateAccount);

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String currency = edtCurrency.getText().toString();
                String color = edtColor.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(getActivity(), "Per favore, inserisci tutte le informazioni", Toast.LENGTH_SHORT).show();
                } else {
                    accounts.add(new Account(name));
                    try {
                        jsonReadWrite.setList(accounts, requireContext());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Toast.makeText(getActivity(), "Conto creato!", Toast.LENGTH_SHORT).show();
                    if (getActivity() != null) {
                        HomeFragment homeFragment = new HomeFragment(accounts);
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, homeFragment);
                        transaction.commit();
                    }
                }
            }
        });

        return view;
    }
}
