package com.example.cashflow;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.mikephil.charting.BuildConfig;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewTransactionFragment extends Fragment {

    private Button expenseButton;
    private Button incomeButton;
    private Button doneButton;
    private Button deleteButton;

    private Spinner categorySpinner;
    private EditText numberEditText;
    private Spinner accountSpinner;
    private Calendar selectedDate;

    private Button dateTimeButton;
    private EditText locationEditText;
    private JsonReadWrite jsonReadWrite;
    private ArrayList<Account> accounts;
    private ArrayList<String> categories;
    private ImageView cameraButton;
    private TextView cameraTextView;
    private TextView selectedTimeTextView;

    public static final int REQUEST_IMAGE_PICK = 123;
    private String nameCity;


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
        locationEditText = view.findViewById(R.id.locationEditText);
        accountSpinner = view.findViewById(R.id.accountSpinner);
        cameraButton = view.findViewById(R.id.cameraButton);
        cameraTextView = view.findViewById(R.id.cameraTextView);
        selectedTimeTextView = view.findViewById(R.id.selectedTimeTextView);

        dateTimeButton = view.findViewById(R.id.dateTimeButton);
        deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.INVISIBLE);
        deleteButton.setVisibility(View.GONE);

        selectedDate = Calendar.getInstance();
        dateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });


        locationEditText.setText(nameCity + "");


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
        // Imposta un listener per il pulsante della fotocamera
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Crea un dialog per scegliere tra Fotocamera e Galleria
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
                builder.setTitle("Scegli la fonte dell'immagine");
                builder.setItems(new CharSequence[]{"Fotocamera", "Galleria"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) { // Fotocamera
                            openCamera();
                        } else if (which == 1) { // Galleria
                            openGallery();
                        }
                    }
                });
                builder.show();
            }
        });
        return view;
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_PICK);
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            if (imageUri != null) {
                FirebaseVisionImage image;
                try {
                    image = FirebaseVisionImage.fromFilePath(requireContext(), imageUri);

                    FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                            .getCloudTextRecognizer();

                    textRecognizer.processImage(image)
                            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText result) {
                                    cameraTextView.setText(result.getText());
                                    Log.d("TextRecognition", result.getText());
                                    System.out.println("TextRecognition " +  result.getText());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Gestisci eventuali errori
                                    e.printStackTrace();
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveTransaction() throws IOException {
        if (numberEditText != null && accountSpinner != null && locationEditText != null && selectedDate != null) {

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
            System.out.println("DateTimeUtil.createDateTimeFromDateAndTimePickers " + selectedDate);
            String location = locationEditText.getText() != null ? locationEditText.getText().toString() : "";
            String selectedCategory = categorySpinner.getSelectedItem() != null ? categorySpinner.getSelectedItem().toString() : "";

            // Resto del codice per salvare la transazione
            Toast.makeText(getContext(), "Transaction saved: " + amount + ", " + accountSelected + ", " + selectedDate + ", " + location, Toast.LENGTH_LONG).show();

            Transactions newTrans = new Transactions(income, amount, selectedDate, location, CategoriesEnum.valueOf(selectedCategory));
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

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Qui puoi fare qualcosa con la data selezionata dall'utente
                // Ad esempio, puoi impostarla in un EditText o fare altro
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String selectedDateString = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                System.out.println(selectedDateString + " selectedDateString");
                selectedTimeTextView.setText(selectedDateString);
            }
        }, year, month, dayOfMonth);

        // Mostra il dialog per la selezione della data
        datePickerDialog.show();
    }


}
