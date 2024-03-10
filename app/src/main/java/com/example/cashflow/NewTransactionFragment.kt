package com.example.cashflow

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
import com.example.cashflow.OCRManager.OCRListener
import com.example.cashflow.dataClass.Account
import com.example.cashflow.dataClass.CategoriesEnum
import com.example.cashflow.dataClass.City
import com.example.cashflow.dataClass.Transactions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar

class NewTransactionFragment(
    private val accounts: ArrayList<Account>,
    private val cityPosition: City?
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
    private var jsonReadWrite: JsonReadWrite? = null
    private var categories: ArrayList<String>? = null
    private var cameraButton: ImageView? = null
    private var selectedTimeTextView: TextView? = null
    private var ocrManager: OCRManager? = null
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
        fragment_container = view.findViewById(R.id.fragment_container)
        dateButton = view.findViewById(R.id.dateButton)
        deleteButton = view.findViewById(R.id.deleteButton)
        deleteButton?.setVisibility(View.INVISIBLE)
        deleteButton?.setVisibility(View.GONE)
        selectedDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val dataFormattata = dateFormat.format(selectedDate?.getTime())
        selectedTimeTextView?.setText(dataFormattata + "")
        if (cityPosition != null && cityPosition.nameCity != null) {
            locationEditText?.setText(cityPosition.printOnApp())
        } else {
            locationEditText?.setText("Nessun nome di città disponibile")
            Toast.makeText(context, "Nessun nome di città disponibile", Toast.LENGTH_SHORT).show()
        }
        ripetizioneCheckBox?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                accountLayout3?.setVisibility(View.VISIBLE)
                endTimeTextView?.setVisibility(View.VISIBLE)
                selectedTimeTextView?.setVisibility(View.VISIBLE)
            } else {
                accountLayout3?.setVisibility(View.GONE)
                endTimeTextView?.setVisibility(View.GONE)
                selectedTimeTextView?.setVisibility(View.GONE)
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
            } else {
                // Apri il fragment del giroconto
                val girocontoFragment = GirocontoFragment(accounts)
                fragment_container?.setVisibility(View.VISIBLE)
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

        // Spinner CATEGORIES
        categories = ArrayList()
        for (category in CategoriesEnum.entries) {
            categories!!.add(category.name)
        }
        val categoryAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories!!)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner?.setAdapter(categoryAdapter)
        categorySpinner?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedCategory = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Codice da eseguire quando non viene selezionato nessun elemento
            }
        })


        //SPINNER ACCOUNTS
        val accountNames = ArrayList<String>()
        for (account in accounts) {
            accountNames.add(account.name)
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
            } else {
                setNameTemplate?.setVisibility(View.GONE)
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


    @Throws(IOException::class)
    private fun saveTransaction() {
        if (numberEditText != null && accountSpinner != null) {
            val income = incomeButton!!.isSelected
            val expense = expenseButton!!.isSelected
            val numberText = if (numberEditText!!.getText() != null) numberEditText!!.getText()
                .toString() else ""

// Estrai il testo dall'EditText e imposta un valore di default se null
val rawNumberText = numberEditText?.text?.toString() ?: ""
// Verifica se almeno uno dei pulsanti della spesa o entrata è stato premuto
            if (!income && !expense) {
                Toast.makeText(context, "Inserisci Spesa o Entrata", Toast.LENGTH_SHORT).show()
                return  // Esce dal metodo senza salvare la transazione
            }

// Controlla se il testo è vuoto o rappresenta zero in formati diversi
            if (rawNumberText.isEmpty() || rawNumberText == "0" || rawNumberText == "0.00") {
                Toast.makeText(context, "Aggiungi un prezzo", Toast.LENGTH_SHORT).show()
                return  // Esce dal metodo senza salvare la transazione
            }
            val amount: Double
            amount = try {
                numberText.toDouble()
            } catch (e: NumberFormatException) {
                // Il valore inserito non è un numero valido, mostra un messaggio di errore
                Toast.makeText(context, "Numero numerico non valido", Toast.LENGTH_SHORT).show()
                return  // Esce dal metodo senza salvare la transazione
            }
            val accountSelected =
                if (accountSpinner!!.getSelectedItem() != null) accountSpinner!!.getSelectedItem()
                    .toString() else ""

            //formatting date for Toast
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val dataFormattata = dateFormat.format(selectedDate!!.time)
            println("dataFormattata $dataFormattata")
            var location = ""
            location = if (cityPosition != null) {
                if (cityPosition.nameCity != null) cityPosition.nameCity.toString() else ""
            } else {
                "Nessuna città disponibile"
            }
            val selectedCategory =
                if (categorySpinner!!.getSelectedItem() != null) categorySpinner!!.getSelectedItem()
                    .toString() else ""

            // Resto del codice per salvare la transazione
            Toast.makeText(
                context,
                "Transazione salvata: $amount, $accountSelected, $dataFormattata, $location",
                Toast.LENGTH_LONG
            ).show()
            val newTrans = Transactions(
                income,
                amount,
                selectedDate,
                cityPosition,
                CategoriesEnum.valueOf(selectedCategory)
            )
            jsonReadWrite = JsonReadWrite()
            for (account in accounts) {
                if (account.name == accountSelected) {
                    account.listTrans.add(newTrans)
                    account.updateBalance()
                    println("New Transaction: $newTrans")
                    jsonReadWrite!!.setList(accounts, requireContext())
                    break
                }
            }
            if (activity != null) {
                requireActivity().supportFragmentManager.popBackStack()
                val mainLayout = requireActivity().findViewById<LinearLayout>(R.id.drawer_layout)
                mainLayout.visibility = View.VISIBLE
            }
        } else {
            // Gestisci il caso in cui uno dei componenti UI sia nullo
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
