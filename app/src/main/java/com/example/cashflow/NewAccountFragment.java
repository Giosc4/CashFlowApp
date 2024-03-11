package com.example.cashflow;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cashflow.dataClass.Account;
import com.example.cashflow.db.SQLiteDB;
import com.example.cashflow.db.readSQL;
import com.example.cashflow.db.writeSQL;

import java.util.ArrayList;

public class NewAccountFragment extends Fragment {

    private EditText edtName;
    private ArrayList<Account> accounts;

    private SQLiteDB sqLiteDB;
    private readSQL readSQL;
    private writeSQL writeSQL;

    public NewAccountFragment(ArrayList<Account> accounts) {
        this.accounts = accounts;
        sqLiteDB = new SQLiteDB(requireContext());
        SQLiteDatabase db = sqLiteDB.getWritableDatabase();
        sqLiteDB.onCreate(db);

        readSQL = new readSQL(db);
        writeSQL = new writeSQL(db);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_account, container, false);


        edtName = view.findViewById(R.id.edtName);
        Button btnCreateAccount = view.findViewById(R.id.btnCreateAccount);

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(getActivity(), "Inserisci il nome dell'account", Toast.LENGTH_SHORT).show();
                } else {
                    // Utilizza SQLiteDB per salvare il nuovo account.
                    boolean success = writeSQL.createAccount(name, 0); // Assumendo che l'equilibrio iniziale sia 0.

                    if (success) {
                        Toast.makeText(getActivity(), "Conto creato!", Toast.LENGTH_SHORT).show();
                        // Aggiorna l'UI o effettua il passaggio al Fragment desiderato qui.
                        if (getActivity() != null) {
                            HomeFragment homeFragment = new HomeFragment(accounts);
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, homeFragment);
                            transaction.commit();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Errore nella creazione del conto", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }
}
