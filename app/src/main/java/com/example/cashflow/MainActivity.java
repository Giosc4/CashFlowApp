package com.example.cashflow;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnHome;
    private Test test;
    private ArrayList<Account> accounts ;
    JsonReadWrite jsonReadWrite ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test = new Test();
        accounts = test.getList();
        jsonReadWrite = new JsonReadWrite(test.getList(), "test12.json");

        try {
            jsonReadWrite.setList(accounts, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new HomeFragment(jsonReadWrite.readAccountsFromJson(MainActivity.this)));
            }
        });

        loadFragment(new HomeFragment(jsonReadWrite.readAccountsFromJson( this)));
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }



}
