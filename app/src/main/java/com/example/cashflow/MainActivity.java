package com.example.cashflow;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button btnHome;
    private Test test;
    private ArrayList<Account> accounts;
    JsonReadWrite jsonReadWrite;

    private static final int PERMISSION_REQUEST_CODE = 1;
    FusedLocationProviderClient fusedLocationProviderClient;
    String nameCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inizializza fusedLocationProviderClient usando la variabile di classe
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Richiedi il permesso per l'accesso alla posizione
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Se il permesso è già concesso, ottieni la posizione
            getDeviceLocation();
        } else {
            // Se il permesso non è stato concesso, richiedilo all'utente
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }

        jsonReadWrite = new JsonReadWrite("test12.json");
        accounts = jsonReadWrite.readAccountsFromJson(MainActivity.this);
//        accounts = null;
        System.out.println(accounts);
        if (accounts == null ) {
            //        QUESTE RIGHE DI CODICE DEVONO ESSERE ESEGUITE SOLO ALL'INSTALLAZIONE DELL'APP.
            test = new Test();
            accounts = test.getList();
            jsonReadWrite = new JsonReadWrite(test.getList(), "test12.json");
        }
        try {
            jsonReadWrite.setList(accounts, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //per l'OCR -> riconoscimento del testo dalle immagini
        FirebaseApp.initializeApp(this);

        btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new HomeFragment(jsonReadWrite.readAccountsFromJson(MainActivity.this), nameCity));
            }
        });

        loadFragment(new HomeFragment(jsonReadWrite.readAccountsFromJson(MainActivity.this), nameCity));

    }

    private void getDeviceLocation() {
        // Verifica se il permesso di ACCESS_FINE_LOCATION è stato concesso
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Il permesso è già concesso, procedi con l'ottenimento della posizione
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && addresses.size() > 0) {
                                nameCity = addresses.get(0).getLocality();
                                System.out.println("City: " + nameCity);
                                System.out.println("");

                                // Ora che hai ottenuto la posizione, carica il fragment
                                loadFragment(new HomeFragment(jsonReadWrite.readAccountsFromJson(MainActivity.this), nameCity));

                            } else {
                                System.out.println("No address found.");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("Errore durante la richiesta di geocoding: " + e.getMessage());
                            // Gestisci l'errore qui (ad esempio, fornisci un messaggio all'utente o riprova la richiesta)
                        }
                    } else {
                        System.out.println("La posizione è nulla");
                    }
                }
            });
        } else {
            // Il permesso non è stato concesso, quindi dovresti gestire questo caso qui
            Toast.makeText(this, "Permesso di accesso alla posizione non concesso.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Il permesso è stato concesso, ottieni la posizione
                getDeviceLocation();
            } else {
                // Il permesso è stato negato dall'utente, puoi gestire questo caso qui
                Toast.makeText(this, "Il permesso per la posizione è stato negato.", Toast.LENGTH_SHORT).show();
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
