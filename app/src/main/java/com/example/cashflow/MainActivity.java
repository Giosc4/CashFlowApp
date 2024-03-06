package com.example.cashflow;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cashflow.box.*;
import com.example.cashflow.dataClass.Account;

import java.io.IOException;
import java.util.ArrayList;

import android.Manifest;

public class MainActivity extends AppCompatActivity {
    private Button btnHome;

    private ArrayList<Account> accounts;
    JsonReadWrite jsonReadWrite;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        loadFragment2(new box_template_fragment());


        // Inizializza il JsonReadWrite
        jsonReadWrite = new JsonReadWrite();
        accounts = jsonReadWrite.readAccountsFromJson(MainActivity.this);
        //accounts = null;
        System.out.println(accounts);
        if (accounts == null) {
            // Le righe di codice devono essere eseguite solo all'installazione dell'app.
            Test test = new Test();
            accounts = test.getList();
            jsonReadWrite = new JsonReadWrite(test.getList());
        }
        try {
            jsonReadWrite.setList(accounts, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }


        btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new HomeFragment(jsonReadWrite.readAccountsFromJson(MainActivity.this)));
            }
        });
        loadFragment(new HomeFragment(jsonReadWrite.readAccountsFromJson(MainActivity.this)));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadFragment(new HomeFragment(jsonReadWrite.readAccountsFromJson(MainActivity.this)));
            } else {
                System.out.println("Location permission is required to fetch the location");
            }
        }
    }

    private void loadFragment2(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.boxContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
