package com.example.cashflow.fragments.create

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.cashflow.MainActivity
import com.example.cashflow.OCRManager
import com.example.cashflow.OCRManager.OCRListener
import com.example.cashflow.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import com.example.cashflow.dataClass.*
import com.example.cashflow.db.*
import java.io.File
import java.text.ParseException
import java.util.Date
import java.util.Locale

class NewTransactionFragment(
    private val readSQL: ReadSQL, private val writeSQL: WriteSQL, private val cityPosition: City?
) : Fragment() {
    private var expenseButton: Button? = null
    private var incomeButton: Button? = null
    private var doneButton: Button? = null
    private var deleteButton: Button? = null
    private var categorySpinner: Spinner? = null
    private var numberEditText: EditText? = null
    private var accountSpinner: Spinner? = null
    private var selectedDate: Calendar? = null
    private var timeSpinner: Spinner? = null
    private var dateEndRepButton: Button? = null
    private var accountLayout3: LinearLayout? = null
    private var endTimeTextView: TextView? = null
    private var pianificazioneCheckBox: CheckBox? = null
    private var templateCheckBox: CheckBox? = null
    private var setNameTemplate: EditText? = null
    private var girocontoButton: Button? = null

    private var textViewTitle: TextView? = null
    private var textViewProvenienza: TextView? = null
    private var accountSpinnerProv: Spinner? = null
    private var textViewArrivo: TextView? = null
    private var accountSpinnerArrivo: Spinner? = null

    private var cameraImageUri: Uri? = null
    private var dateButton: Button? = null
    private var locationEditText: TextView? = null
    private var categories: ArrayList<Category>? = null
    private var cameraButton: ImageView? = null
    private var selectedTimeTextView: TextView? = null
    private var ocrManager: OCRManager? = null

    private var accounts: ArrayList<Account> = readSQL.getAccounts()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_transaction, container, false)

        // Initialize the EditText, Spinner, and Button variables
        expenseButton = view.findViewById(R.id.expenseButton)
        incomeButton = view.findViewById(R.id.incomeButton)
        doneButton = view.findViewById(R.id.doneButton)
        categorySpinner = view.findViewById(R.id.categorySpinner)
        numberEditText = view.findViewById(R.id.numberEditText)
        accountSpinner = view.findViewById(R.id.accountSpinner)
        locationEditText = view.findViewById(R.id.locationEditText)
        cameraButton = view.findViewById(R.id.cameraButton)
        ocrManager = OCRManager(requireContext())
        selectedTimeTextView = view.findViewById(R.id.selectedTimeTextView)
        timeSpinner = view.findViewById(R.id.timeSpinner)
        dateEndRepButton = view.findViewById(R.id.dateEndRepButton)
        accountLayout3 = view.findViewById(R.id.accountLayout3)
        endTimeTextView = view.findViewById(R.id.endTimeTextView)
        pianificazioneCheckBox = view.findViewById(R.id.pianificazioneCheckBox)
        templateCheckBox = view.findViewById(R.id.templateCheckBox)
        setNameTemplate = view.findViewById(R.id.setNameTemplate)
        girocontoButton = view.findViewById(R.id.girocontoButton)

        textViewTitle = view.findViewById(R.id.textViewTitle)
        textViewProvenienza = view.findViewById(R.id.textViewProvenienza)
        accountSpinnerProv = view.findViewById(R.id.accountSpinnerProv)
        textViewArrivo = view.findViewById(R.id.textViewArrivo)
        accountSpinnerArrivo = view.findViewById(R.id.accountSpinnerArrivo)

        dateButton = view.findViewById(R.id.dateButton)
        deleteButton = view.findViewById(R.id.deleteButton)

        deleteButton?.setVisibility(View.INVISIBLE)
        deleteButton?.setVisibility(View.GONE)

        accounts = readSQL.getAccounts()


        selectedDate = Calendar.getInstance()
        val timestamp = selectedDate?.timeInMillis
        val formattedDate =
            timestamp?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it)) }
        selectedTimeTextView?.setText(formattedDate)
        if (cityPosition != null && cityPosition.nameCity != null) {
            locationEditText?.setText(cityPosition.printOnApp())
            Log.d("NewTransactionFragment", "City: $cityPosition")
            Log.d("NewTransactionFragment S", "City: ${cityPosition.toString()}")

        } else {
            locationEditText?.setText("Nessun nome di città disponibile")
            Log.e("NewTransactionFragment", "Nessuna città disponibile")
            Toast.makeText(context, "Nessun nome di città disponibile", Toast.LENGTH_SHORT).show()
        }
        pianificazioneCheckBox?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                accountLayout3?.setVisibility(View.VISIBLE)
                endTimeTextView?.setVisibility(View.VISIBLE)
                selectedTimeTextView?.visibility = if (isChecked) View.VISIBLE else View.GONE
                templateCheckBox?.setVisibility(View.GONE)
                Log.d("NewTransactionFragment", "Pianificazione selezionata")
            } else {
                accountLayout3?.setVisibility(View.GONE)
                endTimeTextView?.setVisibility(View.GONE)
                selectedTimeTextView?.setVisibility(View.GONE)
                templateCheckBox?.setVisibility(View.VISIBLE)
                Log.d("NewTransactionFragment", "Pianificazione non selezionata")
            }
        })
        val repeatOptions = arrayOf("Ogni giorno", "Ogni settimana", "Ogni mese", "Ogni anno")
        val timeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, repeatOptions)
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeSpinner?.setAdapter(timeAdapter)
        dateEndRepButton?.setOnClickListener(View.OnClickListener {
            showDatePickerDialog(
                endTimeTextView
            )
        })

        girocontoButton?.setOnClickListener {
            it.isSelected = !it.isSelected

            if (textViewTitle?.visibility == View.GONE) {
                // If the views are currently hidden, show them
                textViewTitle!!.visibility = View.VISIBLE
                textViewProvenienza!!.visibility = View.VISIBLE
                accountSpinnerProv!!.visibility = View.VISIBLE
                textViewArrivo!!.visibility = View.VISIBLE
                accountSpinnerArrivo!!.visibility = View.VISIBLE
                accountSpinner!!.visibility = View.GONE
            } else {
                // If the views are currently shown, hide them
                textViewTitle!!.visibility = View.GONE
                textViewProvenienza!!.visibility = View.GONE
                accountSpinnerProv!!.visibility = View.GONE
                textViewArrivo!!.visibility = View.GONE
                accountSpinnerArrivo!!.visibility = View.GONE
                accountSpinner!!.visibility = View.VISIBLE

            }
        }

        dateButton?.setOnClickListener(View.OnClickListener {
            showDatePickerDialog(
                selectedTimeTextView
            )
        })
        numberEditText?.setFilters(arrayOf(
            InputFilter { source, start, end, dest, dstart, dend ->
                val modifiedSource = source.toString().replace(',', '.')

                var hasDecimalSeparator = dest.toString().contains(".")
                // Get the current number of decimal places
                var decimalPlaces = 0
                if (hasDecimalSeparator) {
                    val split =
                        dest.toString().split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    if (split.size > 1) {
                        decimalPlaces = split[1].length
                    }
                }
                // Check if the input is a valid decimal number
                for (i in start until end) {
                    val inputChar = source[i]

                    // Allow digits and a decimal point
                    if (!Character.isDigit(inputChar) && inputChar != '.') {
                        return@InputFilter ""
                    }
                    // Allow only two decimal places
                    if (hasDecimalSeparator && decimalPlaces >= 2) {
                        return@InputFilter ""
                    }
                    // Increment the decimal places count if a decimal point is encountered
                    if (inputChar == '.') {
                        hasDecimalSeparator = true
                    } else if (hasDecimalSeparator) {
                        decimalPlaces++
                    }
                }
                null
            }
        ))

        // Imposta OnClickListener per i pulsanti "EXPENSE" e "INCOME"
        expenseButton?.setOnClickListener(View.OnClickListener {
            // Cambia il colore del pulsante e imposta la sua proprietà "selected" a true
            expenseButton?.setSelected(true)
            expenseButton?.setBackgroundColor(Color.parseColor("#00cc44")) // Verde quando selezionato
            incomeButton?.setSelected(false)
            incomeButton?.setBackgroundColor(Color.parseColor("#FF6464")) // rosso quando non selezionato
        })
        incomeButton?.setOnClickListener(View.OnClickListener {
            // Cambia il colore del pulsante e imposta la sua proprietà "selected" a true
            incomeButton?.setSelected(true)
            incomeButton?.setBackgroundColor(Color.parseColor("#00cc44")) // Verde quando selezionato
            expenseButton?.setSelected(false)
            expenseButton?.setBackgroundColor(Color.parseColor("#FF6464")) // rosso quando non selezionato
        })

        categories = readSQL.getCategories()
        val categoryNames = categories?.map { it.name }
        val categoryAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categoryNames ?: listOf()
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner?.adapter = categoryAdapter

        categorySpinner
        //SPINNER ACCOUNTS
        val accountNames = ArrayList<String>()
        for (account in accounts) {
            account.name?.let { accountNames.add(it) }
        }
        val dataAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, accountNames)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        accountSpinner?.setAdapter(dataAdapter)
        accountSpinner?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedAccount = parent.getItemAtPosition(position).toString()
                Log.d("NewTransactionFragment", "Account selezionato: $selectedAccount")
                /*
                NON SUCCEDE NULLA PERCHè NEL METODO saveTransaction() VIENE SEELEZIONATO CON accountSpinner.getSelectedItem
               String accountSelected = accountSpinner.getSelectedItem() != null ? accountSpinner.getSelectedItem().toString() : "";
                 */
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                // SE NULLA è SELEZIONATO ALLORA VIENE PRESO IL PRIMO ACCOUNT
            }
        })

        val dataAdapterProv =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, accountNames)
        dataAdapterProv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        accountSpinnerProv?.setAdapter(dataAdapterProv)
        accountSpinnerProv?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedAccountProv = parent.getItemAtPosition(position).toString()
                val accountNamesArrivo = ArrayList(accountNames)
                accountNamesArrivo.remove(selectedAccountProv)
                val dataAdapterArrivo = ArrayAdapter(
                    context!!,
                    android.R.layout.simple_spinner_item,
                    accountNamesArrivo
                )
                dataAdapterArrivo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                accountSpinnerArrivo?.setAdapter(dataAdapterArrivo)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                // Handle nothing selected
            }
        })

        templateCheckBox?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                setNameTemplate?.visibility = if (isChecked) View.VISIBLE else View.GONE
                pianificazioneCheckBox?.setVisibility(View.GONE)
                dateButton?.setVisibility(View.GONE)
                selectedTimeTextView?.setVisibility(View.GONE)
                locationEditText?.setVisibility(View.GONE)
            } else {
                setNameTemplate?.setVisibility(View.GONE)
                pianificazioneCheckBox?.setVisibility(View.VISIBLE)
                dateButton?.setVisibility(View.VISIBLE)
                selectedTimeTextView?.setVisibility(View.VISIBLE)
                locationEditText?.setVisibility(View.VISIBLE)
            }
        })
        // Add OnClickListener for the "DONE" button
        doneButton?.setOnClickListener {
            Log.d(
                "NewTransactionFragment",
                "Done clicked, girocontoButton isSelected: ${girocontoButton?.isSelected}"
            )
            try {
                if (girocontoButton?.isSelected == true) {
                    Log.d("NewTransactionFragment", "Handling Giroconto")
                    handleGiroconto()
                } else if (pianificazioneCheckBox?.isChecked == true) {
                    Log.d("NewTransactionFragment", "Handling Pianificazione")
                    handlePianificazione()
                } else if (templateCheckBox?.isChecked == true) {
                    Log.d("NewTransactionFragment", "Handling Template")
                    handleTemplate()
                } else {
                    Log.d("NewTransactionFragment", "Saving regular transaction")
                    saveTransaction()
                }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
        // Imposta un listener per il pulsante della fotocamera
        cameraButton?.setOnClickListener {
            val options = arrayOf<CharSequence>("Fotocamera", "Galleria")
            val builder = MaterialAlertDialogBuilder(requireContext())
            builder.setTitle("Scegli la fonte dell'immagine")
            builder.setItems(options) { dialog, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            builder.show()
        }
        return view
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            // Errore durante la creazione del file
            null
        }
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.android.fileprovider", // Cambia con il tuo applicationId
                it
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Crea un nome univoco per l'immagine basato su un timestamp
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_$timeStamp"
        // Ottiene il directory in cui salvare l'immagine
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        // Crea un file vuoto per salvare l'immagine
        return File.createTempFile(
            imageFileName, /* prefisso del nome del file */
            ".jpg", /* estensione del file */
            storageDir /* directory */
        ).also {
            // Salva un riferimento al path del file
            cameraImageUri = Uri.fromFile(it)
        }
    }


    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val imageUri: Uri?
            // Gestione del risultato della selezione dell'immagine dalla galleria
            if (requestCode == REQUEST_IMAGE_PICK && data != null && data.data != null) {
                imageUri = data.data
                startCrop(imageUri)
                Log.d(
                    "REQUEST_IMAGE_PICK NewTransactionFragment",
                    "onActivityResult() imageUri: $imageUri"
                )
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Assicurati che l'URI dell'immagine non sia null
                if (cameraImageUri != null) {
                    imageUri = cameraImageUri
                    startCrop(imageUri)
                    Log.d(
                        "REQUEST_IMAGE_CAPTURE NewTransactionFragment",
                        "onActivityResult() cameraImageUri: $cameraImageUri"
                    )
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (result != null) {
                    val croppedImageUri = result.uri
                    // Processa l'immagine croppata come necessario, es. OCR
                    processCroppedImage(croppedImageUri)
                    Log.d(
                        "CropImage CROP_IMAGE_ACTIVITY_REQUEST_CODE",
                        "onActivityResult() croppedImageUri: $croppedImageUri"
                    )
                }
            }
        }
    }

    private fun startCrop(imageUri: Uri?) {
        Log.d("startCrop()", "imageUri: $imageUri")
        CropImage.activity(imageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(requireContext(), this)
    }

    private fun processCroppedImage(croppedImageUri: Uri) {
        ocrManager!!.processImage(croppedImageUri, object : OCRListener {
            override fun onTextRecognized(value: String?) {
                // Esegui sulla UI thread
                if (activity != null) {
                    requireActivity().runOnUiThread {
                        Log.d("OCRManager", "Updating UI with OCR result")
                        var processedText = value
                        if (value?.contains(",") == true) {
                            processedText = value.replace(',', '.')
                        }
                        numberEditText!!.setText(processedText)
                    }
                }
                Log.d("OCRManager", "Text Recognized: $value")
            }

            override fun onTextNotRecognized(error: String?) {
                requireActivity().runOnUiThread {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
                Log.d("OCRManager", "No text recognized")
                Toast.makeText(
                    context,
                    "ERRORE NELLA LETTURA DELL'IMPORTO\nRiprovare",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onFailure(e: Exception?) {
                requireActivity().runOnUiThread {
                    e?.printStackTrace()
                    Toast.makeText(context, "Errore OCR", Toast.LENGTH_SHORT).show()
                    Log.e("OCRManager", "Error processing image", e)
                }
            }
        })
    }

    private fun saveCity(city: City): Int {
        if (city.nameCity == null) {
            Log.e("NewTransactionFragment", "City name is null")
            Toast.makeText(context, "Nome della città non valido", Toast.LENGTH_LONG).show()
            return -1
        }
        // Check if the city already exists
        val existingCityId = readSQL.getIdByCityName(city.nameCity ?: "")
        Log.d("NewTransactionFragment", "Existing city ID: $existingCityId")

        if (existingCityId != -1) {
            // City already exists, no need to save again
            Log.d("NewTransactionFragment", "City already exists with ID: $existingCityId")
            return existingCityId
        } else {
            // Save the new city and return its ID
            Log.d("NewTransactionFragment", "Saving new city: $city")
            return writeSQL.insertCity(city)
        }
    }

    private fun saveTransaction() {
        Log.d("saveTransaction", "Saving transaction")
        val isIncome = incomeButton?.isSelected ?: false
        val rawNumberText = numberEditText?.text.toString()
        val accountName = accountSpinner?.selectedItem.toString()
        val accountId = readSQL.getIdByAccountName(accountName)
        val categoryName = categorySpinner?.selectedItem.toString()
        val categoryId = categories?.firstOrNull { it.name == categoryName }?.id ?: -1
        val cityId = cityPosition?.id ?: -1
        val amount = rawNumberText.toDoubleOrNull() ?: 0.0
        val isIncomeSelected = incomeButton?.isSelected ?: false
        val isExpenseSelected = expenseButton?.isSelected ?: false

        // Formattazione della data
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateString = selectedDate?.let { dateFormat.format(it.time) } ?: ""
        try {
            val date = dateFormat.parse(dateString) ?: Calendar.getInstance().time
            selectedDate?.time = date
        } catch (e: ParseException) {
            Toast.makeText(context, "Formato data non valido", Toast.LENGTH_LONG).show()
            return
        }

        val isPianificazione = pianificazioneCheckBox?.isChecked ?: false
        val isTemplate = templateCheckBox?.isChecked ?: false

        if (!isIncomeSelected && !isExpenseSelected) {
            // Se nessuno dei due pulsanti è selezionato, mostra un messaggio all'utente
            Toast.makeText(context, "Seleziona Entrata o Uscita", Toast.LENGTH_LONG).show()
            return // Interrompe l'esecuzione ulteriore della funzione
        }

        if (accountId == -1) {
            Toast.makeText(context, "Seleziona un account", Toast.LENGTH_LONG).show()
            return
        }
        if (categoryId == -1) {
            Toast.makeText(context, "Seleziona una categoria", Toast.LENGTH_LONG).show()
            return
        }
        if (amount == 0.0) {
            Toast.makeText(context, "Inserisci un importo valido", Toast.LENGTH_LONG).show()
            return
        }
        var valueCityId = cityId
        if (cityPosition != null) {
            valueCityId = saveCity(cityPosition)
            Log.d("saveTransaction", "City ID: $valueCityId")
        }

        // Trasformazione dei dati in una Transazione
        val newTrans = Transactions(
            income = isIncome,
            amount = amount,
            date = selectedDate!!,
            cityId = valueCityId,
            categoryId = categoryId,
            accountId = accountId
        )

        writeSQL.insertTransaction(newTrans)


        Toast.makeText(context, "Transazione salvata", Toast.LENGTH_SHORT).show()
        Log.d("saveTransaction", "Transazione salvata " + newTrans.toString())
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun handleGiroconto() {
        Log.d("handleGiroconto", "Handling Giroconto")

        val accountProvName = accountSpinnerProv?.selectedItem.toString()
        val accountArrivoName = accountSpinnerArrivo?.selectedItem.toString()

        val accountProvId = accounts.firstOrNull { it.name == accountProvName }?.id ?: -1
        val accountArrivoId = accounts.firstOrNull { it.name == accountArrivoName }?.id ?: -1

        if (accountProvId == -1 || accountArrivoId == -1) {
            Toast.makeText(
                context,
                "Assicurati di aver selezionato entrambi gli account.",
                Toast.LENGTH_SHORT
            ).show()
            Log.e("handleGiroconto", "Account di provenienza o di arrivo non trovato.")
            return
        }

        val amountText = numberEditText?.text.toString()
        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(context, "L'importo deve essere maggiore di 0.", Toast.LENGTH_SHORT)
                .show()
            Log.e("handleGiroconto", "Importo non valido.")
            return
        }

        val cityId = cityPosition?.nameCity?.let { cityName ->
            readSQL.getCityIdByName(cityName)
        } ?: -1

        val validCategoryId = categories?.firstOrNull()?.id ?: -1


        Log.d("handleGiroconto", "City ID: $cityId, Category ID: $validCategoryId")
        if (cityId == -1 || validCategoryId == -1) {
            Toast.makeText(context, "Città o Categoria non valida.", Toast.LENGTH_SHORT).show()
            Log.e("handleGiroconto", "Città o Categoria non valida.")
            return
        }

        val exitTransaction = Transactions(
            income = false,
            amount = amount,
            date = selectedDate ?: Calendar.getInstance(),
            cityId = cityId,
            categoryId = validCategoryId,
            accountId = accountProvId
        )

        val entryTransaction = Transactions(
            income = true,
            amount = amount,
            date = selectedDate ?: Calendar.getInstance(),
            cityId = cityId,
            categoryId = validCategoryId,
            accountId = accountArrivoId
        )

        Log.d("handleGiroconto", "Exit Transaction: $exitTransaction")
        Log.d("handleGiroconto", "Entry Transaction: $entryTransaction")

        if (writeSQL.insertTransaction(exitTransaction) != -1L && writeSQL.insertTransaction(
                entryTransaction
            ) != -1L
        ) {
            Toast.makeText(context, "Giroconto completato con successo.", Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        } else {
            Toast.makeText(
                context,
                "Errore durante il completamento del giroconto.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handlePianificazione() {
        Log.d("NewTransactionFragment", "Handling Pianificazione")

        // Raccogli i dati necessari dalla UI
        val isIncome = incomeButton?.isSelected ?: false
        val rawAmountText = numberEditText?.text.toString()
        val amount = rawAmountText.toDoubleOrNull() ?: 0.0
        val categorySelected = categorySpinner?.selectedItem.toString()
        val categoryId = categories?.firstOrNull { it.name == categorySelected }?.id ?: -1
        val accountSelected = accountSpinner?.selectedItem.toString()
        val accountId = readSQL.getIdByAccountName(accountSelected)
        val repetition = timeSpinner?.selectedItem.toString()

        // Utilizza SimpleDateFormat per formattare correttamente la data di inizio e fine
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startDateString = selectedDate?.let { dateFormat.format(it.time) } ?: ""
        val endDateString = endTimeTextView?.text.toString()

        // Verifica la validità dei dati raccolti
        if (amount <= 0.0) {
            Toast.makeText(context, "Inserisci un importo valido", Toast.LENGTH_SHORT).show()
            Log.e("handlePianificazione", "Invalid amount: $amount")
            return
        }

        if (categoryId == -1) {
            Toast.makeText(context, "Seleziona una categoria", Toast.LENGTH_SHORT).show()
            Log.e("handlePianificazione", "Category ID not found")
            return
        }

        if (accountId == -1) {
            Toast.makeText(context, "Seleziona un account", Toast.LENGTH_SHORT).show()
            Log.e("handlePianificazione", "Account ID not found")
            return
        }

        // Prepara l'ID della città se disponibile
        val cityId = if (cityPosition != null) saveCity(cityPosition) else null
        Log.d("handlePianificazione", "City ID: $cityId")

        // Crea un nuovo oggetto Planning
        val newPlanning = Planning(
            income = if (isIncome) 1 else 0,
            amount = amount,
            date = startDateString,
            cityId = cityId,
            categoryId = categoryId,
            accountId = accountId,
            repetition = repetition,
            endDate = endDateString
        )

        // Inserisci l'oggetto Planning nel database
        if (writeSQL.insertPlanning(newPlanning) != -1L) {
            Toast.makeText(context, "Pianificazione salvata con successo", Toast.LENGTH_SHORT).show()
            // Potresti voler aggiungere qui codice per chiudere il frammento o aggiornare l'UI
        } else {
            Toast.makeText(context, "Errore nel salvare la pianificazione", Toast.LENGTH_SHORT).show()
        }
    }


    private fun handleTemplate() {
        Log.d("NewTransactionFragment", "Handling Template")

        val templateName = setNameTemplate?.text.toString()
        val isIncome = incomeButton?.isSelected ?: false
        val amount = numberEditText?.text.toString().toDoubleOrNull() ?: 0.0

        val categoryName = categorySpinner?.selectedItem.toString()
        Log.d("handleTemplate", "Category name: $categoryName")

        val categoryId = readSQL.getCategoryIdByName(categoryName)
        Log.d("handleTemplate", "Category ID: $categoryId")

        Log.d("handleTemplate", "categorySpinner name: ${categorySpinner?.selectedItem.toString()}")

        val accountName = accountSpinner?.selectedItem.toString()
        val accountId = readSQL.getIdByAccountName(accountName)


        // Verifica la validità dei dati inseriti
        if (templateName.isBlank()) {
            Toast.makeText(context, "Inserisci un nome per il template.", Toast.LENGTH_SHORT).show()
            Log.e("handleTemplate", "Template name is blank.")
            return
        }

        if (amount <= 0.0) {
            Toast.makeText(context, "Inserisci un importo valido.", Toast.LENGTH_SHORT).show()
            Log.e("handleTemplate", "Invalid amount: $amount")
            return
        }

        if (categoryId == -1) {
            Toast.makeText(context, "Seleziona una categoria.", Toast.LENGTH_SHORT).show()
            Log.e("handleTemplate", "Category ID not found.")
            return
        }

        if (accountId == -1) {
            Toast.makeText(context, "Seleziona un account.", Toast.LENGTH_SHORT).show()
            Log.e("handleTemplate", "Account ID not found.")
            return
        }

        // Creazione del template
        val templateTransaction = TemplateTransaction(
            id = 0, // L'ID sarà generato dal database
            name = templateName,
            income = isIncome,
            amount = amount,
            category_id = categoryId,
            account_id = accountId
        )

        Log.d("handleTemplate", "Template: $templateTransaction")

        // Utilizza il template per creare una transazione effettiva
        if (writeSQL.insertTemplateTransaction(templateTransaction) != -1L) {
            Toast.makeText(context, "Template salvato correttamente.", Toast.LENGTH_SHORT).show()
            Log.d("NewTransactionFragment", "Template saved: $templateTransaction")
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        } else {
            Toast.makeText(context, "Errore nel salvataggio del template.", Toast.LENGTH_SHORT)
                .show()
            Log.e("handleTemplate", "Error saving template: $templateTransaction")
        }
    }


    private fun showDatePickerDialog(textView: TextView?) {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { view, year, monthOfYear, dayOfMonth -> // Aggiorna la variabile selectedDate con la data selezionata dall'utente
                selectedDate!![Calendar.YEAR] = year
                selectedDate!![Calendar.MONTH] = monthOfYear
                selectedDate!![Calendar.DAY_OF_MONTH] = dayOfMonth
                val selectedDateString =
                    year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                textView!!.text = selectedDateString
            },
            year,
            month,
            dayOfMonth
        )

        // Mostra il dialog per la selezione della data
        datePickerDialog.show()
    }


    companion object {
        private const val PERMISSION_CAMERA = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
        const val REQUEST_IMAGE_PICK = 123
    }
}
