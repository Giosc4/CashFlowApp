package com.example.cashflow;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
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

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

public class EditTransactionFragment extends Fragment {

    private Button expenseButton;
    private Button incomeButton;
    private Spinner categorySpinner;
    private EditText numberEditText;

    private ImageView cameraButton;

    private OCRManager ocrManager;
    private Uri cameraImageUri;
    private static final int PERMISSION_CAMERA = 1;
    public static final int REQUEST_IMAGE_PICK = 123;

    private Spinner accountSpinner;

    private Button dateButton;

    private TextView selectedTimeTextView;

    private Calendar calendar;

    private TextView locationEditText;
    private Button doneButton;
    private Button deleteButton;

    //CONSTRUCTOR
    private Transactions transactionOriginal;
    private Account accountOriginal;
    private ArrayList<String> categories;

    //GET FROM JSON

    private JsonReadWrite jsonReadWrite;
    private ArrayList<Account> accounts;

    //CHANGE ACCCOUNT TRANS
    private int originalTransactionIndex;
    private int originalAccountIndex;

    public EditTransactionFragment(Transactions transaction, Account account) {
        this.transactionOriginal = transaction;
        this.accountOriginal = account;
        jsonReadWrite = new JsonReadWrite("test12.json");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_transaction, container, false);

        expenseButton = view.findViewById(R.id.expenseButton);
        incomeButton = view.findViewById(R.id.incomeButton);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        numberEditText = view.findViewById(R.id.numberEditText);
        accountSpinner = view.findViewById(R.id.accountSpinner);
        selectedTimeTextView = view.findViewById(R.id.selectedTimeTextView);
        locationEditText = view.findViewById(R.id.locationEditText);
        doneButton = view.findViewById(R.id.doneButton);
        deleteButton = view.findViewById(R.id.deleteButton);
        dateButton = view.findViewById(R.id.dateButton);
        calendar = transactionOriginal.getDate();
        cameraButton = view.findViewById(R.id.cameraButton);

        ocrManager = new OCRManager(requireContext());

        String str = "";
        if (transactionOriginal.getAmount() < 0) {
            str = String.valueOf(transactionOriginal.getAmount());
            str = str.replace("-", "");
            setExpense();
        } else {
            str = String.valueOf(transactionOriginal.getAmount());
            setIncome();
        }

        numberEditText.setText(str);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String selectedDateString = dateFormat.format(transactionOriginal.getDate().getTime());
        selectedTimeTextView.setText(selectedDateString);


        locationEditText.setText(transactionOriginal.getCity().printOnApp());

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
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

        expenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExpense();
            }
        });

        incomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambia il colore del pulsante e imposta la sua proprietà "selected" a true
                setIncome();
            }
        });

        // Spinner CATEGORIES
        categories = new ArrayList<>();
        for (CategoriesEnum category : CategoriesEnum.values()) {
            categories.add(category.name());
        }
        this.originalTransactionIndex = categories.indexOf(transactionOriginal.getCategory().name());


        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(originalTransactionIndex);

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

        accounts = jsonReadWrite.readAccountsFromJson(requireContext());

        //SPINNER ACCOUNTS
        ArrayList<String> accountNames = new ArrayList<>();
        for (Account account : accounts) {
            accountNames.add(account.getName());
        }
        this.originalAccountIndex = accountNames.indexOf(accountOriginal.getName() + "");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, accountNames);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountSpinner.setAdapter(dataAdapter);
        accountSpinner.setSelection(originalAccountIndex);

        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedAccount = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Codice da eseguire quando non viene selezionato nessun elemento
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTransaction();
            }
        });


        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTransaction();
            }
        });

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
                            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
                                        .start(requireContext(), EditTransactionFragment.this);
                            } else {
                                Uri imageUri = data.getData();
                                if (imageUri != null) {
                                    CropImage.activity(imageUri)
                                            .setGuidelines(CropImageView.Guidelines.ON)
                                            .start(requireContext(), EditTransactionFragment.this);
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

    private void setExpense() {
        // Cambia il colore del pulsante e imposta la sua proprietà "selected" a true
        expenseButton.setSelected(true);
        expenseButton.setBackgroundColor(Color.parseColor("#00cc44")); // Verde quando selezionato
        incomeButton.setSelected(false);
        incomeButton.setBackgroundColor(Color.parseColor("#e06666")); // rosso quando non selezionato
    }

    private void setIncome() {
        incomeButton.setSelected(true);
        incomeButton.setBackgroundColor(Color.parseColor("#00cc44")); // Verde quando selezionato
        expenseButton.setSelected(false);
        expenseButton.setBackgroundColor(Color.parseColor("#e06666")); // rosso quando non selezionato
    }

    private void deleteTransaction() {
        // Rimuovi la transazione originale dall'account originale
        accountOriginal.removeTransaction(transactionOriginal);
        accounts.set(originalAccountIndex, accountOriginal);

        try {
            // Esegui il salvataggio dell'account originale nel file JSON dopo la rimozione della transazione
            jsonReadWrite.setList(accounts, requireContext());

            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
                LinearLayout mainLayout = getActivity().findViewById(R.id.mainLayout);
                mainLayout.setVisibility(View.VISIBLE);
            }

            Toast.makeText(getContext(), "Transazione Eliminata", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Errore durante il salvataggio delle modifiche", Toast.LENGTH_LONG).show();
        }
    }

    private void updateTransaction() {
        boolean newIncome = incomeButton.isSelected();
        double newAmount = Double.parseDouble(numberEditText.getText().toString());
        String nuovacitta = locationEditText.getText().toString();
        //transactionOriginal.getCity().getNameCity())
        City newCity = new City(locationEditText.getText().toString(), 0, 0);
        CategoriesEnum newCategory = CategoriesEnum.values()[categorySpinner.getSelectedItemPosition()];
        int newAccountIndex = accountSpinner.getSelectedItemPosition();
        Calendar newDate = calendar;

        Transactions newTrans = new Transactions(newIncome, newAmount, newDate, newCity, newCategory);
        if (newAccountIndex != originalAccountIndex) {
            accountOriginal.removeTransaction(transactionOriginal);
            accounts.get(newAccountIndex).addTransaction(newTrans);
        } else {
            accountOriginal.editTransaction(transactionOriginal, newTrans);
        }
        try {
            // Esegui il salvataggio dei dati qui, dopo aver apportato tutte le modifiche
            accounts.set(originalAccountIndex, accountOriginal);
            jsonReadWrite.setList(accounts, requireContext());

            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
                LinearLayout mainLayout = getActivity().findViewById(R.id.mainLayout);
                mainLayout.setVisibility(View.VISIBLE);
            }
            Toast.makeText(getContext(), "Transazione aggiornata", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Errore durante il salvataggio delle modifiche", Toast.LENGTH_LONG).show();
        }

    }

    //scelta della data
    private void showDatePickerDialog() {
        Calendar newCalendar = Calendar.getInstance();
        int year = newCalendar.get(Calendar.YEAR);
        int month = newCalendar.get(Calendar.MONTH);
        int dayOfMonth = newCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                newCalendar.set(Calendar.YEAR, year);
                newCalendar.set(Calendar.MONTH, monthOfYear);
                newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                calendar = newCalendar;

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String selectedDateString = dateFormat.format(calendar.getTime());
                System.out.println(selectedDateString + " selectedDateString");
                selectedTimeTextView.setText(selectedDateString);
            }
        }, year, month, dayOfMonth);

        datePickerDialog.show();
    }


}
