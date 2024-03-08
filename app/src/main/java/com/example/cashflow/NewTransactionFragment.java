package com.example.cashflow;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import androidx.fragment.app.FragmentTransaction;

import com.example.cashflow.dataClass.Account;
import com.example.cashflow.dataClass.CategoriesEnum;
import com.example.cashflow.dataClass.City;
import com.example.cashflow.dataClass.Transactions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private Spinner timeSpinner;
    private Button dateEndRepButton;
    private LinearLayout accountLayout3;
    private TextView endTimeTextView;
    private CheckBox ripetizioneCheckBox;
    private CheckBox templateCheckBox;
    private EditText setNameTemplate;
    private Button girocontoButton;
    private FrameLayout fragment_container;
    private Uri cameraImageUri;
    private Button dateButton;
    private TextView locationEditText;
    private JsonReadWrite jsonReadWrite;
    private ArrayList<Account> accounts;
    private ArrayList<String> categories;
    private ImageView cameraButton;
    private TextView selectedTimeTextView;
    private static final int PERMISSION_CAMERA = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
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

        timeSpinner = view.findViewById(R.id.timeSpinner);
        dateEndRepButton = view.findViewById(R.id.dateEndRepButton);
        accountLayout3 = view.findViewById(R.id.accountLayout3);
        endTimeTextView = view.findViewById(R.id.endTimeTextView);
        ripetizioneCheckBox = view.findViewById(R.id.ripetizioneCheckBox);
        templateCheckBox = view.findViewById(R.id.templateCheckBox);
        setNameTemplate = view.findViewById(R.id.setNameTemplate);
        girocontoButton = view.findViewById(R.id.girocontoButton);
        fragment_container = view.findViewById(R.id.fragment_container);

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


        ripetizioneCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    accountLayout3.setVisibility(View.VISIBLE);
                    endTimeTextView.setVisibility(View.VISIBLE);
                    selectedTimeTextView.setVisibility(View.VISIBLE);
                } else {
                    accountLayout3.setVisibility(View.GONE);
                    endTimeTextView.setVisibility(View.GONE);
                    selectedTimeTextView.setVisibility(View.GONE);
                }
            }
        });

        String[] repeatOptions = {"Ogni giorno", "Ogni settimana", "Ogni mese", "Ogni anno"};
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, repeatOptions);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(timeAdapter);

        dateEndRepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(endTimeTextView);
            }
        });

        girocontoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verifica se il fragment del giroconto è già aperto
                Fragment existingFragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (existingFragment instanceof GirocontoFragment) {
                    // Chiudi il fragment del giroconto senza salvare e nascondi il fragment_container
                    requireActivity().getSupportFragmentManager().popBackStack();
                    fragment_container.setVisibility(View.GONE);
                } else {
                    // Apri il fragment del giroconto
                    GirocontoFragment girocontoFragment = new GirocontoFragment(accounts);
                    fragment_container.setVisibility(View.VISIBLE);
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, girocontoFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }

        });


        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(selectedTimeTextView);
            }
        });
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

        templateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setNameTemplate.setVisibility(View.VISIBLE);
                } else {
                    setNameTemplate.setVisibility(View.GONE);
                }
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
                        if (which == 0) {
                            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(requireActivity(),
                                        new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
                            } else if (isCameraAppAvailable()) {
                                // Il permesso è già stato concesso e l'app della fotocamera è disponibile
                                openCamera();
                            } else {
                                // Nessuna app della fotocamera disponibile
                                Toast.makeText(getContext(), "Nessuna app della fotocamera trovata", Toast.LENGTH_SHORT).show();
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

    private boolean isCameraAppAvailable() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager packageManager = getActivity().getPackageManager();
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Camera app exists
            Log.d("CameraCheck", "Camera app is available.");
            return true;
        } else {
            // No camera app
            Log.e("CameraCheck", "No camera app found.");
            return false;
        }
    }

    private void openCamera() {
        isCameraAppAvailable();
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        Log.d("takePictureIntent", takePictureIntent.toString());
        // Ensure there is a camera app to handle the intent
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Create a file to save the photo
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "cashflow_image");
            values.put(MediaStore.Images.Media.DESCRIPTION, "date: " + Calendar.getInstance().getTime());
            cameraImageUri = requireActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);

            Log.d("NewTransactionFragment", "openCamera() cameraImageUri: " + cameraImageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Log.e("NewTransactionFragment", "No camera app found");
            Toast.makeText(getContext(), "Please install a camera app to take photos", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri imageUri;

            // Gestione del risultato della selezione dell'immagine dalla galleria
            if (requestCode == REQUEST_IMAGE_PICK && data != null && data.getData() != null) {
                imageUri = data.getData();
                startCrop(imageUri);
                Log.d("REQUEST_IMAGE_PICK NewTransactionFragment", "onActivityResult() imageUri: " + imageUri);
            }

            // Gestione del risultato della cattura dell'immagine con la fotocamera
            else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Assicurati che l'URI dell'immagine non sia null
                if (cameraImageUri != null) {
                    imageUri = cameraImageUri;
                    startCrop(imageUri);
                    Log.d("REQUEST_IMAGE_CAPTURE NewTransactionFragment", "onActivityResult() cameraImageUri: " + cameraImageUri);
                }
            }

            // Gestione del risultato del cropping dell'immagine
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (result != null) {
                    Uri croppedImageUri = result.getUri();
                    // Processa l'immagine croppata come necessario, es. OCR
                    processCroppedImage(croppedImageUri);
                    Log.d("CropImage CROP_IMAGE_ACTIVITY_REQUEST_CODE", "onActivityResult() croppedImageUri: " + croppedImageUri);
                }
            }
        }
    }

    private void startCrop(Uri imageUri) {
        Log.d("startCrop()", "imageUri: " + imageUri);
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(getContext(), this);
    }

    private void processCroppedImage(Uri croppedImageUri) {
        ocrManager.processImage(croppedImageUri, new OCRManager.OCRListener() {
            @Override
            public void onTextRecognized(final String value) {
                // Esegui sulla UI thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Log.d("OCRManager", "Updating UI with OCR result");
                        String processedText = value;
                        if (value.contains(",")) {
                            processedText = value.replace(',', '.');
                        }
                        numberEditText.setText(processedText);
                    });
                }
                Log.d("OCRManager", "Text Recognized: " + value);
            }

            @Override
            public void onTextNotRecognized(String error) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show());
                Log.d("OCRManager", "No text recognized");
                Toast.makeText(getContext(), "ERRORE NELLA LETTURA DELL'IMPORTO\nRiprovare", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                getActivity().runOnUiThread(() -> {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Errore OCR", Toast.LENGTH_SHORT).show();
                    Log.e("OCRManager", "Error processing image", e);
                });
            }
        });
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

            Transactions newTrans = new Transactions(income, amount, selectedDate, cityPosition, CategoriesEnum.valueOf(selectedCategory));
            jsonReadWrite = new JsonReadWrite();

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

    private void showDatePickerDialog(TextView textView) {
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
                textView.setText(selectedDateString);
            }
        }, year, month, dayOfMonth);

        // Mostra il dialog per la selezione della data
        datePickerDialog.show();
    }
}
