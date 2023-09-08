package com.example.cashflow;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.BuildConfig;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewTransactionFragment extends Fragment {

    private Button expenseButton;
    private Button incomeButton;
    private Button doneButton;
    private Spinner categorySpinner;
    private EditText numberEditText;
    private Spinner accountSpinner;
    private EditText dateEditText;
    private EditText locationEditText;
    private JsonReadWrite jsonReadWrite;
    private ArrayList<Account> accounts;
    private ArrayList<String> categories;
    private ImageView cameraButton;
    private TextView cameraTextView;

    private static final int REQUEST_CAMERA_CODE = 100;
    private static final int YOUR_IMAGE_SELECTION_REQUEST_CODE = 123;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/com.example.cashflow/";

    public Bitmap bitmap;
    private String nameCity;
    public TextRecognition textRecognition;

    public static final String TESS_DATA = "/tessdata";
    private String mCurrentPhotoPath;

    public NewTransactionFragment(ArrayList<Account> accounts, String nameCity) {
        this.accounts = accounts;
        this.nameCity = nameCity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_transaction, container, false);

        // Initialize the EditText, Spinner, and Button variables
        expenseButton = view.findViewById(R.id.expenseButton);
        incomeButton = view.findViewById(R.id.incomeButton);
        doneButton = view.findViewById(R.id.doneButton);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        numberEditText = view.findViewById(R.id.numberEditText);
        accountSpinner = view.findViewById(R.id.accountSpinner);
        dateEditText = view.findViewById(R.id.dateEditText);
        locationEditText = view.findViewById(R.id.locationEditText);
        accountSpinner = view.findViewById(R.id.accountSpinner);
        cameraButton = view.findViewById(R.id.cameraButton);
        cameraTextView = view.findViewById(R.id.cameraTextView);


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
        locationEditText.setText(nameCity + ""); //errore, compare soltanto dopo aver premuto home


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

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verifica i permessi della fotocamera
                if (checkCameraPermission()) {

                    // Permessi già concessi, avvia l'attività di selezione dell'immagine
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, YOUR_IMAGE_SELECTION_REQUEST_CODE);
                } else {
                    // Richiedi i permessi della fotocamera all'utente
                    requestCameraPermission();
                }
            }
        });
        return view;
    }

    private boolean checkCameraPermission() {
        int cameraPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
    }


    // Gestisci il risultato della selezione dell'immagine
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == YOUR_IMAGE_SELECTION_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            // Ottieni l'URI dell'immagine selezionata dall'utente
            Uri selectedImageUri = data.getData();

            // Verifica se l'URI dell'immagine è valido
            if (selectedImageUri != null) {
                    textRecognition = new TextRecognition(requireContext() ,selectedImageUri);

                // Avvia il riconoscimento del testo sull'immagine selezionata
                String recognizedText = textRecognition.getRecognizedText();

                // Mostra il testo riconosciuto in un TextView (cameraTextView)
                if (cameraTextView != null) {
                    cameraTextView.setText(recognizedText);
                    System.out.println("recognizedText " + recognizedText);
                } else {
                    // Aggiungi un messaggio di errore se cameraTextView non è inizializzato correttamente
                    Toast.makeText(requireContext(), "Errore: TextView non inizializzato correttamente", Toast.LENGTH_SHORT).show();
                }

                System.out.println("Testo riconosciuto: " + recognizedText);
            } else {
                // Gestisci il caso in cui l'URI dell'immagine non sia valido
                Toast.makeText(requireContext(), "Errore: URI dell'immagine non valido", Toast.LENGTH_SHORT).show();
            }
        }
    }

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
