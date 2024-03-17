package com.example.cashflow.fragments

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
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
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    private var ripetizioneCheckBox: CheckBox? = null
    private var templateCheckBox: CheckBox? = null
    private var setNameTemplate: EditText? = null
    private var girocontoButton: Button? = null
    private var fragment_container: FrameLayout? = null
    private var cameraImageUri: Uri? = null
    private var dateButton: Button? = null
    private var locationEditText: TextView? = null
    private var categories: ArrayList<Category>? = null
    private var cameraButton: ImageView? = null
    private var selectedTimeTextView: TextView? = null
    private var ocrManager: OCRManager? = null

    private var accountSpinnerProv: Spinner? = null
    private var accountSpinnerArrivo: Spinner? = null
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
        ripetizioneCheckBox = view.findViewById(R.id.ripetizioneCheckBox)
        templateCheckBox = view.findViewById(R.id.templateCheckBox)
        setNameTemplate = view.findViewById(R.id.setNameTemplate)
        girocontoButton = view.findViewById(R.id.girocontoButton)
//        fragment_container = view.findViewById(R.id.fragment_container)
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
        } else {
            locationEditText?.setText("Nessun nome di città disponibile")
            Log.e("NewTransactionFragment", "Nessuna città disponibile")
            Toast.makeText(context, "Nessun nome di città disponibile", Toast.LENGTH_SHORT).show()
        }
        ripetizioneCheckBox?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                accountLayout3?.setVisibility(View.VISIBLE)
                endTimeTextView?.setVisibility(View.VISIBLE)
                selectedTimeTextView?.setVisibility(View.VISIBLE)
                templateCheckBox?.setVisibility(View.GONE)
                Log.d("NewTransactionFragment", "Ripetizione selezionata")
            } else {
                accountLayout3?.setVisibility(View.GONE)
                endTimeTextView?.setVisibility(View.GONE)
                selectedTimeTextView?.setVisibility(View.GONE)
                templateCheckBox?.setVisibility(View.VISIBLE)
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
        girocontoButton?.setOnClickListener(View.OnClickListener {
            // Verifica se il fragment del giroconto è già aperto
            val existingFragment =
                requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (existingFragment is GirocontoFragment) {
                // Chiudi il fragment del giroconto senza salvare e nascondi il fragment_container
                requireActivity().supportFragmentManager.popBackStack()
                fragment_container?.setVisibility(View.GONE)
                accountSpinner?.setVisibility(View.VISIBLE)
            } else {
                // Apri il fragment del giroconto
                val girocontoFragment = GirocontoFragment(accounts)
                fragment_container?.setVisibility(View.VISIBLE)
                accountSpinner?.setVisibility(View.GONE)
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, girocontoFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        })
        dateButton?.setOnClickListener(View.OnClickListener {
            showDatePickerDialog(
                selectedTimeTextView
            )
        })
        numberEditText?.setFilters(arrayOf(
            InputFilter { source, start, end, dest, dstart, dend -> // Check if the input contains a decimal point
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
            incomeButton?.setBackgroundColor(Color.parseColor("#a7c5f9")) // azzurro quando non selezionato
        })
        incomeButton?.setOnClickListener(View.OnClickListener {
            // Cambia il colore del pulsante e imposta la sua proprietà "selected" a true
            incomeButton?.setSelected(true)
            incomeButton?.setBackgroundColor(Color.parseColor("#00cc44")) // Verde quando selezionato
            expenseButton?.setSelected(false)
            expenseButton?.setBackgroundColor(Color.parseColor("#a7c5f9")) // azzurro quando non selezionato
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
                /*
                NON SUCCEDE NULLA PERCHè NEL METODO saveTransaction() VIENE SEELEZIONATO CON accountSpinner.getSelectedItem
               String accountSelected = accountSpinner.getSelectedItem() != null ? accountSpinner.getSelectedItem().toString() : "";
                 */
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                // SE NULLA è SELEZIONATO ALLORA VIENE PRESO IL PRIMO ACCOUNT
            }
        })
        templateCheckBox?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                setNameTemplate?.setVisibility(View.VISIBLE)
                ripetizioneCheckBox?.setVisibility(View.GONE)
            } else {
                setNameTemplate?.setVisibility(View.GONE)
                ripetizioneCheckBox?.setVisibility(View.VISIBLE)
            }
        })
        // Add OnClickListener for the "DONE" button
        doneButton?.setOnClickListener(View.OnClickListener {
            try {
                saveTransaction() // Save the transaction
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        })
        // Imposta un listener per il pulsante della fotocamera
        cameraButton?.setOnClickListener(View.OnClickListener { // Crea un dialog per scegliere tra Fotocamera e Galleria
            val builder = MaterialAlertDialogBuilder(requireContext())
            builder.setTitle("Scegli la fonte dell'immagine")
            builder.setItems(
                arrayOf<CharSequence>("Fotocamera", "Galleria"),
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        if (which == 0) {
                            if (ContextCompat.checkSelfPermission(
                                    requireActivity(),
                                    Manifest.permission.CAMERA
                                )
                                != PackageManager.PERMISSION_GRANTED
                            ) {
                                ActivityCompat.requestPermissions(
                                    requireActivity(),
                                    arrayOf(Manifest.permission.CAMERA),
                                    PERMISSION_CAMERA
                                )
                            } else if (isCameraAppAvailable) {
                                // Il permesso è già stato concesso e l'app della fotocamera è disponibile
                                openCamera()
                            } else {
                                // Nessuna app della fotocamera disponibile
                                Toast.makeText(
                                    context,
                                    "Nessuna app della fotocamera trovata",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (which == 1) { // Galleria
                            openGallery()
                        }
                    }
                })
            builder.show()
        })
        return view
    }

    private val isCameraAppAvailable: Boolean
        private get() {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val packageManager = requireActivity().packageManager
            return if (takePictureIntent.resolveActivity(packageManager) != null) {
                // Camera app exists
                Log.d("CameraCheck", "Camera app is available.")
                true
            } else {
                // No camera app
                Log.e("CameraCheck", "No camera app found.")
                false
            }
        }

    private fun openCamera() {
        isCameraAppAvailable
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        Log.d("takePictureIntent", takePictureIntent.toString())
        // Ensure there is a camera app to handle the intent
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            // Create a file to save the photo
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "cashflow_image")
            values.put(MediaStore.Images.Media.DESCRIPTION, "date: " + Calendar.getInstance().time)
            cameraImageUri = requireActivity().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
            Log.d("NewTransactionFragment", "openCamera() cameraImageUri: $cameraImageUri")
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } else {
            Log.e("NewTransactionFragment", "No camera app found")
            Toast.makeText(
                context,
                "Please install a camera app to take photos",
                Toast.LENGTH_SHORT
            ).show()
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

    private fun saveTransaction() {
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

        val dataString = selectedTimeTextView?.text.toString()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        try {
            val date = dateFormat.parse(dataString)
            selectedDate?.time = date
        } catch (e: ParseException) {
            Toast.makeText(context, "Formato data non valido", Toast.LENGTH_LONG).show()
            return
        }

        val isRipetizione = ripetizioneCheckBox?.isChecked ?: false
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


        // Trasformazione dei dati in una Transazione
        val newTrans = Transactions(
            income = isIncome,
            amount = amount,
            date = selectedDate!!,
            cityId = cityId,
            categoryId = categoryId,
            accountId = accountId
        )
        // Logica per Giroconto
        if (girocontoButton?.isSelected == true) {
            handleGiroconto(newTrans)
        } else {
            // Salva la transazione base
            writeSQL.insertTransaction(newTrans)
        }
        // Logica per Ripetizione
        if (isRipetizione) {
            handleRipetizione(newTrans)
        }
        // Logica per Template
        if (isTemplate) {
            handleTemplate(newTrans)
        }
        // Altre operazioni finali, ad esempio aggiornamento UI
        Toast.makeText(context, "Transazione salvata", Toast.LENGTH_SHORT).show()
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun handleGiroconto(transaction: Transactions) {
        // Supponendo che hai già configurato gli spinner e ottenuto gli ID dei conti selezionati

        // Configurazione dell'adapter dello spinner di provenienza con una lista di oggetti Account
        accountSpinnerProv?.adapter = ArrayAdapter<Account>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            accounts // Assumendo che 'accounts' sia la tua lista di oggetti Account
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

// Configurazione dell'adapter dello spinner di arrivo, analogamente
        accountSpinnerArrivo?.adapter = ArrayAdapter<Account>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            accounts // La stessa lista o una diversa, a seconda della logica dell'app
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        val accountProvenienza = accountSpinnerProv?.selectedItem as Account
        val accountIdProvenienza = accountProvenienza.id

        val accountArrivo = accountSpinnerArrivo?.selectedItem as Account
        val accountIdArrivo = accountArrivo.id


        // Creiamo la transazione di uscita dal conto di provenienza
        val exitTransaction = Transactions(
            id = 0, // Lasciato a 0 se l'ID è auto-generato dal database
            income = false,
            amount = transaction.amountValue,
            date = transaction.date,
            cityId = transaction.cityId,
            categoryId = transaction.categoryId,
            accountId = accountIdProvenienza
        )
        // Inseriamo la transazione di uscita nel database
        writeSQL.insertTransaction(exitTransaction)

        // Aggiorniamo il saldo del conto di provenienza
        val currentBalanceProvenienza = readSQL.getAccountBalanceById(accountIdProvenienza)
        writeSQL.updateAccountBalance(
            accountIdProvenienza,
            currentBalanceProvenienza - transaction.amountValue
        )

        // Creiamo la transazione di entrata nel conto di arrivo
        val entryTransaction = Transactions(
            id = 0, // Lasciato a 0 se l'ID è auto-generato dal database
            income = true,
            amount = transaction.amountValue,
            date = transaction.date,
            cityId = transaction.cityId,
            categoryId = transaction.categoryId,
            accountId = accountIdArrivo
        )
        // Inseriamo la transazione di entrata nel database
        writeSQL.insertTransaction(entryTransaction)

        // Aggiorniamo il saldo del conto di arrivo
        val currentBalanceArrivo = readSQL.getAccountBalanceById(accountIdArrivo)
        writeSQL.updateAccountBalance(
            accountIdArrivo,
            currentBalanceArrivo + transaction.amountValue
        )

        // Notifichiamo l'utente del successo dell'operazione
        Toast.makeText(context, "Giroconto effettuato con successo", Toast.LENGTH_SHORT).show()
    }


    private fun updateAccountBalance(accountId: Int, amount: Double) {
        // Ottieni il saldo corrente del conto
        val currentBalance = readSQL.getAccountBalanceById(accountId)
        val newBalance = currentBalance + amount
        writeSQL.updateAccountBalance(accountId, newBalance)
    }

    private fun handleRipetizione(transaction: Transactions) {
        // Ottieni i dettagli della ripetizione, come la data di fine e la frequenza
        val endDate = dateEndRepButton?.text.toString()
        val repetition = timeSpinner?.selectedItem.toString()

        var nextDate = transaction.date.clone() as Calendar

        while (nextDate.before(endDate)) {
            when (repetition) {
                "Ogni giorno" -> nextDate.add(Calendar.DAY_OF_MONTH, 1)
                "Ogni settimana" -> nextDate.add(Calendar.WEEK_OF_YEAR, 1)
                "Ogni mese" -> nextDate.add(Calendar.MONTH, 1)
                "Ogni anno" -> nextDate.add(Calendar.YEAR, 1)
            }
            val newTransaction = Transactions(
                income = transaction.isIncome,
                amount = transaction.amountValue,
                date = nextDate.clone() as Calendar,
                cityId = transaction.cityId,
                categoryId = transaction.categoryId,
                accountId = transaction.accountId
            )
            writeSQL.insertTransaction(newTransaction)
        }
    }

    private fun handleTemplate(transaction: Transactions) {
        // Salva i dettagli della transazione come template per utilizzo futuro
        val template = TemplateTransaction(
            id = 0, // Assumiamo che l'ID venga generato dal DB
            name = setNameTemplate?.text.toString(),
            isIncome = transaction.isIncome,
            amount = transaction.amountValue,
            categoryId = transaction.categoryId,
            accountId = transaction.accountId
        )
        // Assumi di avere un metodo writeSQL.insertTemplateTransaction() per salvare il template
        writeSQL.insertTemplateTransaction(template)
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
    private fun prepareSelectedDate() {
        val timestamp = selectedTimeTextView?.text.toString().toLongOrNull()
        if (timestamp != null) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp
            selectedDate = calendar
        } else {
            Toast.makeText(context, "Formato data non valido", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val PERMISSION_CAMERA = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
        const val REQUEST_IMAGE_PICK = 123
    }
}
