package com.example.cashflow;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
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
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.cashflow.dataClass.Account;
import com.example.cashflow.dataClass.CategoriesEnum;
import com.example.cashflow.dataClass.City;
import com.example.cashflow.dataClass.Transactions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NewTransactionFragment extends Fragment {

    private Button expenseButton;
    private Button incomeButton;
    private Button doneButton;
    private Button deleteButton;

    private Spinner categorySpinner;
    private EditText numberEditText;
    private Spinner accountSpinner;
    private Calendar selectedDate;
    private boolean isDateSelected = false;

    private Uri cameraImageUri;
    private Button dateButton;
    private EditText locationEditText;
    private JsonReadWrite jsonReadWrite;
    private ArrayList<Account> accounts;
    private ArrayList<String> categories;
    private ImageView cameraButton;
    private TextView selectedTimeTextView;
    private static final int PERMISSION_CAMERA = 1;
    public static final int REQUEST_IMAGE_PICK = 123;
    private City cityPosition;
    private OCRManager ocrManager;


    public NewTransactionFragment(ArrayList<Account> accounts, City cityPosition) {
        this.accounts = accounts;
        this.cityPosition = cityPosition;

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
        cameraButton = view.findViewById(R.id.cameraButton);
        ocrManager = new OCRManager(requireContext());

        selectedTimeTextView = view.findViewById(R.id.selectedTimeTextView);

        dateButton = view.findViewById(R.id.dateButton);
        deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.INVISIBLE);
        deleteButton.setVisibility(View.GONE);

        selectedDate = Calendar.getInstance();

        if (cityPosition != null) {
            if (cityPosition.getNameCity() != null) {
                locationEditText.setText(cityPosition.getNameCity());
            } else {
                locationEditText.setText("Nessun nome di città disponibile");
            }
        } else {
            Toast.makeText(requireContext(), "Impossibile ottenere la posizione attuale", Toast.LENGTH_SHORT).show();
            locationEditText.setText("Posizione non disponibile");
        }

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

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
                /*
                NON SUCCEDE NULLA PERCHè NEL METODO saveTransaction() VIENE SEELEZIONATO CON accountSpinner.getSelectedItem
               String accountSelected = accountSpinner.getSelectedItem() != null ? accountSpinner.getSelectedItem().toString() : "";
                 */
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // SE NULLA è SELEZIONATO ALLORA VIENE PRESO IL PRIMO ACCOUNT
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
                            // Controlla se il permesso della fotocamera è già stato concesso
                            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                // Se il permesso non è stato concesso, richiedilo
                                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
                            } else {
                                // Il permesso è già stato concesso, procedi con l'apertura della fotocamera
                                openCamera();
                            }
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
        // Creare un file temporaneo per salvare l'immagine catturata dalla fotocamera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Cashflow");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Cashflow image");
        cameraImageUri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri); // Salva l'immagine nella galleria
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

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            if (cameraImageUri != null) {
                CropImage.activity(cameraImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(requireContext(), this);
            } else {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    CropImage.activity(imageUri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(requireContext(), this);
                }
            }
        }

        // Gestire l'output del cropping
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri croppedImageUri = result.getUri();
                if (croppedImageUri != null) {
                    ocrManager.processImage(croppedImageUri, new OCRManager.OCRListener() {
                        @Override
                        public void onTextRecognized(double value) {
                            numberEditText.setText(String.valueOf(value));
                        }

                        @Override
                        public void onTextNotRecognized(String error) {
                            // Notifica l'errore all'utente
                            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                            // Torna al CropImage per permettere all'utente di ritagliare nuovamente l'immagine
                            if (cameraImageUri != null) {
                                CropImage.activity(cameraImageUri)
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .start(requireContext(), NewTransactionFragment.this);
                            } else {
                                Uri imageUri = data.getData();
                                if (imageUri != null) {
                                    CropImage.activity(imageUri)
                                            .setGuidelines(CropImageView.Guidelines.ON)
                                            .start(requireContext(), NewTransactionFragment.this);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            e.printStackTrace();
                            // Gestisci eventuali errori
                        }
                    });
                }
            }
        }
    }

    private void saveTransaction() throws IOException {
        if (numberEditText != null && accountSpinner != null && cityPosition != null && selectedDate != null) {

            boolean income = incomeButton.isSelected();
            boolean expense = expenseButton.isSelected();
            String numberText = numberEditText.getText() != null ? numberEditText.getText().toString() : "";


            // Verifica se almeno uno dei pulsanti della spesa o entrata è stato premuto e quello della data
            if (!income && !expense) {
                // Nessun pulsante selezionato, mostra un messaggio di avviso o gestisci l'errore
                Toast.makeText(getContext(), "Inserisci Spesa o Entrata", Toast.LENGTH_SHORT).show();
                return; // Esce dal metodo senza salvare la transazione
            }

            if (numberText.isEmpty() || numberText.equals("0") || numberText.equals("0.0")) {
                // L'utente non ha inserito un valore numerico, mostra un messaggio di errore
                Toast.makeText(getContext(), "Aggiungi un prezzo", Toast.LENGTH_SHORT).show();
                return; // Esce dal metodo senza salvare la transazione
            }
            if (!isDateSelected) {
                // La data non è stata selezionata, mostra un messaggio di errore
                Toast.makeText(getContext(), "Seleziona la data", Toast.LENGTH_SHORT).show();
                return; // Esce dal metodo senza salvare la transazione
            }

            double amount;
            try {
                amount = Double.parseDouble(numberText);
            } catch (NumberFormatException e) {
                // Il valore inserito non è un numero valido, mostra un messaggio di errore
                Toast.makeText(getContext(), "Numero numerico non valido", Toast.LENGTH_SHORT).show();
                return; // Esce dal metodo senza salvare la transazione
            }

            String accountSelected = accountSpinner.getSelectedItem() != null ? accountSpinner.getSelectedItem().toString() : "";

            //formatting date for Toast
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dataFormattata = dateFormat.format(selectedDate.getTime());

            System.out.println("dataFormattata " + dataFormattata);
            String location = cityPosition.getNameCity() != null ? cityPosition.getNameCity().toString() : "";
            String selectedCategory = categorySpinner.getSelectedItem() != null ? categorySpinner.getSelectedItem().toString() : "";

            // Resto del codice per salvare la transazione
            Toast.makeText(getContext(), "Transazione salvata: " + amount + ", " + accountSelected + ", " + dataFormattata + ", " + location, Toast.LENGTH_LONG).show();

            Transactions newTrans = new Transactions(income, amount, selectedDate, cityPosition, CategoriesEnum.valueOf(selectedCategory));
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
                // Aggiorna la variabile selectedDate con la data selezionata dall'utente
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, monthOfYear);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String selectedDateString = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                selectedTimeTextView.setText(selectedDateString);
                isDateSelected = true;
            }
        }, year, month, dayOfMonth);

        // Mostra il dialog per la selezione della data
        datePickerDialog.show();
    }


}
