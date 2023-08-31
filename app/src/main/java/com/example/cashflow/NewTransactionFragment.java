package com.example.cashflow;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewTransactionFragment extends Fragment {

    private Button expenseButton;
    private Button incomeButton;
    private Spinner categorySpinner;
    private EditText numberEditText;
    private Spinner accountSpinner;
    private EditText dateEditText;
    private EditText locationEditText;
    private JsonReadWrite jsonReadWrite;
    private ArrayList<Account> accounts;
    private ArrayList<String> categories;

//    private String cityName;
//    private FusedLocationProviderClient fusedLocationProviderClient;
//    private final static int REQUEST_CODE = 100;
//    private LocationManager locationManager;


    public NewTransactionFragment(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_transaction, container, false);

        // Initialize the EditText, Spinner, and Button variables
        expenseButton = view.findViewById(R.id.expenseButton);
        incomeButton = view.findViewById(R.id.incomeButton);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        numberEditText = view.findViewById(R.id.numberEditText);
        accountSpinner = view.findViewById(R.id.accountSpinner);
        dateEditText = view.findViewById(R.id.dateEditText);
        locationEditText = view.findViewById(R.id.locationEditText);
        accountSpinner = view.findViewById(R.id.accountSpinner);

//        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
//
//        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            // GPS disabilitato, richiedi all'utente di abilitarlo
//            Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(gpsIntent);
//        } else {
//            getLocation(); // Chiamata solo una volta per ottenere la posizione e il nome della città
//        }
//        locationEditText.setText(cityName);
//
//        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            // GPS disabilitato, richiedi all'utente di abilitarlo
//            Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(gpsIntent);
//        }

        LocalDateTime currentDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDateTime = LocalDateTime.now();
        }
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        }
        String formattedDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formattedDateTime = currentDateTime.format(formatter);
        }
        dateEditText.setText(formattedDateTime);


// Set the input filter on numberEditText for decimal numbers
        numberEditText.setFilters(new InputFilter[]{
                new InputFilter() {
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        // Check if the input contains a decimal point
                        boolean hasDecimalSeparator = dest.toString().contains(".");

                        // Get the current number of decimal places
                        int decimalPlaces = 0;
                        if (hasDecimalSeparator) {
                            String[] split = dest.toString().split("\\.");
                            if (split.length > 1) {
                                decimalPlaces = split[1].length();
                            }
                        }

                        // Check if the input is a valid decimal number
                        for (int i = start; i < end; i++) {
                            char inputChar = source.charAt(i);

                            // Allow digits and a decimal point
                            if (!Character.isDigit(inputChar) && inputChar != '.') {
                                return "";
                            }

                            // Allow only two decimal places
                            if (hasDecimalSeparator && decimalPlaces >= 2) {
                                return "";
                            }

                            // Increment the decimal places count if a decimal point is encountered
                            if (inputChar == '.') {
                                hasDecimalSeparator = true;
                            } else if (hasDecimalSeparator) {
                                decimalPlaces++;
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
                expenseButton.setBackgroundColor(Color.parseColor("#00cc44")); // Verde quando selezionato
                incomeButton.setSelected(false);
                incomeButton.setBackgroundColor(Color.parseColor("#a7c5f9")); // azzurro quando non selezionato
            }
        });

        incomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambia il colore del pulsante e imposta la sua proprietà "selected" a true
                incomeButton.setSelected(true);
                incomeButton.setBackgroundColor(Color.parseColor("#00cc44")); // Verde quando selezionato
                expenseButton.setSelected(false);
                expenseButton.setBackgroundColor(Color.parseColor("#a7c5f9")); // azzurro quando non selezionato
            }
        });

        // Spinner CATEGORIES
        categories = new ArrayList<>();
        for (CategoriesEnum category : CategoriesEnum.values()) {
            categories.add(category.name());
        }

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Selected Category: " + selectedCategory, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Codice da eseguire quando non viene selezionato nessun elemento
            }
        });


        //SPINNER ACCOUNTS
        ArrayList<String> accountNames = new ArrayList<>();
        for (Account account : accounts) {
            accountNames.add(account.getName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, accountNames);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountSpinner.setAdapter(dataAdapter);

        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedAccount = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Conto Selezionato: " + selectedAccount, Toast.LENGTH_LONG).show();
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

//
//    private void getLocation() {
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            // Hai ottenuto il permesso, puoi procedere con l'ottenimento della posizione
//            System.out.println("Permission granted. Getting location...");
//            fusedLocationProviderClient.getLastLocation()
//                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
//                @Override
//                public void onSuccess(Location location) {
//                    if (location != null) {
//                        Geocoder geocoder= new Geocoder(requireActivity(), Locale.getDefault());
//                        List<Address> addresses = null;
//                        try {
//                            addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
//                            locationEditText.setText("City: " + addresses.get(0).getLocality());
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//
//
//                        System.out.println("Got location: " + location.getLatitude() + ", " + location.getLongitude());
//                        updateLocationUI(location);
//                        getCityNameFromLocation(location);
//                        System.out.println("City name (getLocation): " + cityName);
//                    } else {
//                        System.out.println("Location is null."); //STOP HERE
//                    }
//                }
//
//            });
//        } else {
//            System.out.println("Permission not granted. Requesting permission...");
//            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
////        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode==REQUEST_CODE){
//            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                getLocation();
//            } else {
//                Toast.makeText(requireContext(), "Required Permission", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void updateLocationUI(Location location) {
//        // Aggiorna l'UI con la latitudine e longitudine
//        String coordinates = "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude();
//        System.out.println("Updating UI with location: " + coordinates);
//        locationEditText.setText(coordinates);
//    }
//
//    private void getCityNameFromLocation(Location location) {
//        // Utilizza la Geocoder API per ottenere il nome della città basato sulla posizione
//        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
//        try {
//            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//            if (!addresses.isEmpty()) {
//                cityName = addresses.get(0).getLocality(); // Ottieni il nome della città
//                System.out.println("City name (getCityNameFromLocation): " + cityName);
//                locationEditText.setText(cityName); // Aggiorna l'UI con il nome della città
//                System.out.println("No addresses found.");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void saveTransaction() throws IOException {
        if (numberEditText != null && accountSpinner != null && locationEditText != null && dateEditText != null) {

            boolean income = incomeButton.isSelected();
            boolean expense = expenseButton.isSelected();

            // Verifica se almeno uno dei pulsanti è stato premuto
            if (!income && !expense) {
                // Nessun pulsante selezionato, mostra un messaggio di avviso o gestisci l'errore
                Toast.makeText(getContext(), "Please select either Income or Expense", Toast.LENGTH_SHORT).show();
                return; // Esce dal metodo senza salvare la transazione
            }
            String number = numberEditText.getText() != null ? numberEditText.getText().toString() : "";
            double amount = Double.parseDouble(number);
            String accountSelected = accountSpinner.getSelectedItem() != null ? accountSpinner.getSelectedItem().toString() : "";
            String date = dateEditText.getText() != null ? dateEditText.getText().toString() : "";
            String location = locationEditText.getText() != null ? locationEditText.getText().toString() : "";
            String selectedCategory = categorySpinner.getSelectedItem() != null ? categorySpinner.getSelectedItem().toString() : "";

            System.out.println("new transaction fragment - amount 1: ");
            //check if is
            if (expense){
                amount = Double.parseDouble("-"+amount);
                System.out.println("new transaction fragment - amount 2: ");

            }
            System.out.println("new transaction fragment - amount 3: ");


            // Resto del codice per salvare la transazione
            Toast.makeText(getContext(), "Transaction saved: " + amount + ", " + accountSelected + ", " + date + ", " + location, Toast.LENGTH_LONG).show();

            Transactions newTrans = new Transactions(income, amount, date, location, CategoriesEnum.valueOf(selectedCategory));
            jsonReadWrite = new JsonReadWrite("test12.json");

            for (Account account : accounts) {
                if (account.getName().equals(accountSelected)) {
                    account.getListTrans().add(newTrans);
                    account.updateBalance();
                    System.out.println("New Transaction: " + newTrans.toString());
                    jsonReadWrite.setList(accounts, requireContext());
                    break;
                }
            }
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
