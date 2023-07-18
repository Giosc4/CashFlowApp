package com.example.cashflow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NewAccountFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_account, container, false);

        final EditText edtName = view.findViewById(R.id.edtName);
        final EditText edtCurrency = view.findViewById(R.id.edtCurrency);
        final EditText edtLiquidity = view.findViewById(R.id.edtLiquidity);
        final EditText edtColor = view.findViewById(R.id.edtColor);
        Button btnCreateAccount = view.findViewById(R.id.btnCreateAccount);

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String currency = edtCurrency.getText().toString();
                String liquidity = edtLiquidity.getText().toString();
                String color = edtColor.getText().toString();

                if(name.isEmpty() || currency.isEmpty() || liquidity.isEmpty() || color.isEmpty()) {
                    Toast.makeText(getActivity(), "Per favore, inserisci tutte le informazioni", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(getActivity(), "Conto creato!", Toast.LENGTH_SHORT).show();
                    // Qui potresti aggiungere la logica per creare il conto
                }
            }
        });

        return view;
    }
}
