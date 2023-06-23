package com.example.cashflow;import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private Account[] accounts = new Account[4];
    private Button[] accountButtons = new Button[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the accounts
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account();
            // Set the account details here
        }

        // Initialize the account buttons
        accountButtons[0] = findViewById(R.id.btnAccount1);
        accountButtons[1] = findViewById(R.id.btnAccount2);
        accountButtons[2] = findViewById(R.id.btnAccount3);
        accountButtons[3] = findViewById(R.id.btnAccount4);

        // Set OnClickListener for account buttons
        for (int i = 0; i < accountButtons.length; i++) {
            final int index = i; // Must be final to be used in OnClickListener
            accountButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle click on account button
                    // Open a new activity that displays the account details
                    openAccountActivity(accounts[index]);
                }
            });
        }

        Button addTransactionButton = findViewById(R.id.btnAddTransaction);
        addTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click on add transaction button
                openTransactionFragment();
            }
        });

        Button statisticsButton = findViewById(R.id.btnStatistics);
        statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click on statistics button
                openStatisticsActivity();
            }
        });
    }

    private void openAccountActivity(Account account) {
        // Open a new activity that displays the account details
        // Intent intent = new Intent(MainActivity.this, AccountActivity.class);
        // Pass the account details to the new activity
        // intent.putExtra("account", account);
        // startActivity(intent);
    }

    private void openTransactionFragment() {
        // Load the "Add Transaction" fragment above the main activity
        TransactionFragment transactionFragment = new TransactionFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, transactionFragment)
                .addToBackStack(null)
                .commit();

        // Hide the main activity
        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setVisibility(View.GONE);
    }

    private void openStatisticsActivity() {
        // Open a new activity for statistics
        // Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
        // startActivity(intent);
    }

}
