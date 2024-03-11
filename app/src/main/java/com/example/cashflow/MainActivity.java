package com.example.cashflow;

import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cashflow.dataClass.*;
import com.example.cashflow.db.*;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;

import android.Manifest;

public class MainActivity extends AppCompatActivity {
    private Button btnHome;
    private ArrayList<Account> accounts;

    private SQLiteDB sqLiteDB;
    private readSQL readSQL;
    private writeSQL writeSQL;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sqLiteDB = new SQLiteDB(this);
        SQLiteDatabase db = sqLiteDB.getWritableDatabase();

        sqLiteDB.onCreate(db);

//        sqLiteDB.clearAllTableData(db);
        readSQL = new readSQL(db);
        writeSQL = new writeSQL(db);

        accounts = readSQL.getAllAccounts();

        //accounts = null;
        System.out.println(accounts);
        if (accounts == null || accounts.size() == 0) {
            System.out.println("accounts is null");
            // Le righe di codice devono essere eseguite solo all'installazione dell'app.
            writeSQL.createAccount("Cash", 0);
            writeSQL.createAccount("Bank", 0);

            writeSQL.createCategory("Salary", "stipendio");

            City city = new City();
            writeSQL.createCity(city.getNameCity(), city.getLatitude(), city.getLongitude());

            writeSQL.createTransaction(1, 100, "2021-01-01", 1, 1, 1);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(Calendar.getInstance().getTime());
            writeSQL.createTransaction(1, 100, date, null, 1, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }

        btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new HomeFragment(null));
            }
        });
        loadFragment(new HomeFragment(null));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadFragment(new HomeFragment(accounts));
            } else {
                System.out.println("Location permission is required to fetch the location");
            }
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
