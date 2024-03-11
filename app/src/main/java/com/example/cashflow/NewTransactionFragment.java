package com.example.cashflow;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
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
import androidx.fragment.app.Fragment;

import com.example.cashflow.dataClass.Account;
import com.example.cashflow.dataClass.Category;
import com.example.cashflow.dataClass.City;
import com.example.cashflow.dataClass.Transactions;
import com.example.cashflow.db.SQLiteDB;
import com.example.cashflow.db.readSQL;
import com.example.cashflow.db.writeSQL;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class NewTransactionFragment extends Fragment {

    private Button expenseButton;
    private Button incomeButton;
    private Button doneButton;
    private Button deleteButton;

    private Spinner categorySpinner;
    private EditText numberEditText;
    private Spinner accountSpinner;
    private Calendar selectedDate;

    private Uri cameraImageUri;
    private Button dateButton;
    private TextView locationEditText;
    private ArrayList<Account> accounts;
    private ArrayList<String> categories;
    private ImageView cameraButton;
    private TextView selectedTimeTextView;
    private static final int PERMISSION_CAMERA = 1;
    public static final int REQUEST_IMAGE_PICK = 123;
    private City cityPosition;
    private OCRManager ocrManager;
    private SQLiteDB sqLiteDB;
    private readSQL readSQL;
    private writeSQL writeSQL;

    public NewTransactionFragment(City cityPosition) {
        sqLiteDB = new SQLiteDB(requireContext());
        SQLiteDatabase db = sqLiteDB.getWritableDatabase();
        sqLiteDB.onCreate(db);

        readSQL = new readSQL(db);
        writeSQL = new writeSQL(db);
        this.accounts = readSQL.getAllAccounts();
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

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dataFormattata = dateFormat.format(selectedDate.getTime());
        selectedTimeTextView.setText(dataFormattata + "");

        if (cityPosition != null && cityPosition.getNameCity() != null) {
            locationEditText.setText(cityPosition.printOnApp());
        } else {
            locationEditText.setText("Nessun nome di città disponibile");
            Toast.makeText(getContext(), "Nessun nome di città disponibile", Toast.LENGTH_SHORT).show();
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
                        StringBuilder builder = new StringBuilder();
                        // Scansiona i caratteri inseriti dall'utente
                        for (int i = start; i < end; i++) {
                            char c = source.charAt(i);
                            // Se trova una virgola, la sostituisce con un punto
                            if (c == ',') {
                                builder.append('.');
                            } else {
                                builder.append(c);
                            }
                        }

                        // Il testo modificato, con le virgole convertite in punti
                        String modifiedSource = builder.toString();

                        // Verifica se il testo contiene un separatore decimale
                        boolean hasDecimalSeparator = dest.toString().contains(".");
                        // Conta i posti decimali
                        int decimalPlaces = 0;
                        if (hasDecimalSeparator) {
                            String[] split = dest.toString().split("\\.");
                            if (split.length > 1) {
                                decimalPlaces = split[1].length();
                            }
                        }

                        // Verifica la validità del numero decimale
                        for (int i = 0; i < modifiedSource.length(); i++) {
                            char inputChar = modifiedSource.charAt(i);
                            // Consente cifre e un punto decimale
                            if (!Character.isDigit(inputChar) && inputChar != '.') {
                                return ""; // Rifiuta il carattere
                            }

                            // Limita a due il numero di cifre decimali
                            if (hasDecimalSeparator && decimalPlaces >= 2) {
                                return ""; // Rifiuta ulteriori cifre decimali
                            }

                            // Aggiorna il conteggio delle cifre decimali
                            if (inputChar == '.') {
                                hasDecimalSeparator = true;
                            } else if (hasDecimalSeparator) {
                                decimalPlaces++;
                            }
                        }

                        return modifiedSource; // Ritorna il testo modificato
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
        ArrayList<String> categories = new ArrayList<>();
        ArrayList<Category> categoryObjects = readSQL.getAllCategories(); // Assumendo che getAllCategories() restituisca una lista di oggetti Categoria dal DB
        for (Category category : categoryObjects) {
            categories.add(category.getName());
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
            Uri imageUri = (data != null && data.getData() != null) ? data.getData() : cameraImageUri;
            if (imageUri != null) {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getContext(), this);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && result != null) {
                Uri croppedImageUri = result.getUri();
                ocrManager.processImage(croppedImageUri, new OCRManager.OCRListener() {
                    @Override
                    public void onTextRecognized(final String value) {
                        // Esegui sulla UI thread
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("OCRManager", "Updating UI with OCR result");
                                    numberEditText.setText(String.valueOf(value));
                                }
                            });
                        }
                        Log.d("OCRManager", "Text Recognized: " + value);
                    }

                    @Override
                    public void onTextNotRecognized(String error) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Errore OCR", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
    }

    private void saveTransaction() throws IOException {
        if (numberEditText != null && accountSpinner != null) {

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

            String location = "";
            if (cityPosition != null) {
                location = cityPosition.getNameCity() != null ? cityPosition.getNameCity().toString() : "";
            } else {
                location = "Nessuna città disponibile";
            }


            String selectedCategory = categorySpinner.getSelectedItem() != null ? categorySpinner.getSelectedItem().toString() : "";

            // Resto del codice per salvare la transazione
            Toast.makeText(getContext(), "Transazione salvata: " + amount + ", " + accountSelected + ", " + dataFormattata + ", " + location, Toast.LENGTH_LONG).show();


            int cityId = cityPosition != null ? cityPosition.getId() : -1; // Assumi -1 o un altro valore di default se cityPosition è null
            int categoryId = readSQL.getCategoryIdByName(selectedCategory);

            int accountId = readSQL.getIdByAccountName(accountSelected);

            Transactions newTrans = new Transactions(income, amount, selectedDate, cityId, categoryId, accountId);
            long transactionId = writeSQL.addTransaction(newTrans);

            if (transactionId != -1) {
                // La transazione è stata salvata con successo
                // Aggiorna il saldo dell'account selezionato, assumendo che sqLiteDB abbia un metodo per farlo
                writeSQL.updateAccountBalance(accountSelected, income ? amount : -amount);
                Toast.makeText(getContext(), "Transazione salvata", Toast.LENGTH_LONG).show();
            } else {
                // Errore nel salvataggio della transazione
                Toast.makeText(getContext(), "Errore nel salvare la transazione", Toast.LENGTH_SHORT).show();
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
            }
        }, year, month, dayOfMonth);

        // Mostra il dialog per la selezione della data
        datePickerDialog.show();
    }


}
