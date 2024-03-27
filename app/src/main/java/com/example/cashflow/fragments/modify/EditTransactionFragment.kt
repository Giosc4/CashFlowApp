package com.example.cashflow.fragments.modify

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
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
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.cashflow.utils.OCRManager
import com.example.cashflow.utils.OCRManager.OCRListener
import com.example.cashflow.R
import com.example.cashflow.dataClass.*
import com.example.cashflow.db.ReadSQL
import com.example.cashflow.db.SQLiteDB
import com.example.cashflow.db.WriteSQL
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditTransactionFragment : Fragment() {
    private var expenseButton: Button? = null
    private var incomeButton: Button? = null
    private var categorySpinner: Spinner? = null
    private var numberEditText: EditText? = null
    private var cameraButton: ImageView? = null
    private var ocrManager: OCRManager? = null
    private var cameraImageUri: Uri? = null
    private var accountSpinner: Spinner? = null
    private var dateButton: Button? = null
    private var selectedTimeTextView: TextView? = null
    private var calendar: Calendar? = null
    private var locationEditText: TextView? = null
    private var doneButton: Button? = null
    private var deleteButton: Button? = null
    private var categories: ArrayList<Category>? = null


    private var accounts: ArrayList<Account>? = null

    private lateinit var transactionOriginal: Transactions
    private lateinit var accountOriginal: Account
    private var accountOriginalId = -1


    //CHANGE ACCCOUNT TRANS
    private var originalTransactionIndex = 0
    private var originalAccountIndex = 0

    private lateinit var db: SQLiteDB
    private lateinit var readSql: ReadSQL
    private lateinit var writeSql: WriteSQL

    companion object {
        private const val PERMISSION_CAMERA = 1
        const val REQUEST_IMAGE_PICK = 123
        private const val ARG_TRANSACTION_ID = "transaction_id"

        fun newInstance(transactionId: Int): EditTransactionFragment {
            val fragment = EditTransactionFragment()
            val args = Bundle()
            args.putInt(ARG_TRANSACTION_ID, transactionId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_transaction, container, false)
        expenseButton = view.findViewById(R.id.expenseButton)
        incomeButton = view.findViewById(R.id.incomeButton)
        categorySpinner = view.findViewById(R.id.categorySpinner)
        numberEditText = view.findViewById(R.id.numberEditText)
        accountSpinner = view.findViewById(R.id.accountSpinner)
        selectedTimeTextView = view.findViewById(R.id.selectedTimeTextView)
        locationEditText = view.findViewById(R.id.locationEditText)
        doneButton = view.findViewById(R.id.doneButton)
        deleteButton = view.findViewById(R.id.deleteButton)
        dateButton = view.findViewById(R.id.dateButton)
        calendar = transactionOriginal.date
        cameraButton = view.findViewById(R.id.cameraButton)
        ocrManager = OCRManager(requireContext())

        db = SQLiteDB(context)
        readSql = ReadSQL(db.writableDatabase)
        writeSql = WriteSQL(db.writableDatabase)


        val transactionId = arguments?.getInt(ARG_TRANSACTION_ID) ?: -1
        if (transactionId != -1) {
            transactionOriginal = readSql.getTransactionById(transactionId)!!
            accountOriginal = readSql.getAccountByTransactionId(transactionId)!!
            accountOriginalId = accountOriginal.id
        } else {
            // Handle error or invalid transaction ID case
        }


        var str = ""
        if (transactionOriginal.amountValue < 0) {
            str = transactionOriginal.amountValue.toString()
            str = str.replace("-", "")
            setExpense()
        } else {
            str = transactionOriginal.amountValue.toString()
            setIncome()
        }
        numberEditText?.setText(str)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val selectedDateString = dateFormat.format(transactionOriginal.date.time)
        selectedTimeTextView?.setText(selectedDateString)

        val city = readSql.getCityById(transactionOriginal.cityId)

        if (city != null) {
            locationEditText?.setText(city.printOnApp())
        }

        dateButton?.setOnClickListener(View.OnClickListener { showDatePickerDialog() })
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
        expenseButton?.setOnClickListener(View.OnClickListener { setExpense() })
        incomeButton?.setOnClickListener(View.OnClickListener { // Cambia il colore del pulsante e imposta la sua proprietà "selected" a true
            setIncome()
        })

        val localCategories = readSql.getCategories()

// Utilizza la variabile locale immutabile per lavorare con le categorie
        val categoryId = transactionOriginal.categoryId
        originalTransactionIndex = localCategories.indexOfFirst { it.id == categoryId }

// Assicurati che originalTransactionIndex sia valido prima di usarlo
        if (originalTransactionIndex >= 0) {
            categorySpinner?.setSelection(originalTransactionIndex)
        }

// Configura l'adapter del categorySpinner usando la variabile locale immutabile
        val categoryAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, localCategories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner?.adapter = categoryAdapter

        categorySpinner?.setAdapter(categoryAdapter)
        categorySpinner?.setSelection(originalTransactionIndex)
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
        accounts = readSql.getAccounts()

        //SPINNER ACCOUNTS
        val accountNames = ArrayList<String>()
        for (account in (accounts as java.util.ArrayList<Account>?)!!) {
            account.name?.let { accountNames.add(it) }
        }
        originalAccountIndex = accountNames.indexOf(accountOriginal.name + "")
        val dataAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, accountNames)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        accountSpinner?.setAdapter(dataAdapter)
        accountSpinner?.setSelection(originalAccountIndex)
        accountSpinner?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedAccount = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Codice da eseguire quando non viene selezionato nessun elemento
            }
        })
        deleteButton?.setOnClickListener(View.OnClickListener { deleteTransaction() })
        doneButton?.setOnClickListener(View.OnClickListener { updateTransaction() })
        cameraButton?.setOnClickListener(View.OnClickListener { // Crea un dialog per scegliere tra Fotocamera e Galleria
            val builder = MaterialAlertDialogBuilder(requireContext())
            builder.setTitle("Scegli la fonte dell'immagine")
            builder.setItems(arrayOf<CharSequence>("Fotocamera", "Galleria")) { dialog, which ->
                if (which == 0) { // Fotocamera
                    // Controlla se il permesso della fotocamera è già stato concesso
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // Se il permesso non è stato concesso, richiedilo
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.CAMERA),
                            PERMISSION_CAMERA
                        )
                    } else {
                        // Il permesso è già stato concesso, procedi con l'apertura della fotocamera
                        openCamera()
                    }
                } else if (which == 1) { // Galleria
                    openGallery()
                }
            }
            builder.show()
        })
        return view
    }

    private fun openCamera() {
        // Creare un file temporaneo per salvare l'immagine catturata dalla fotocamera
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Cashflow")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Cashflow image")
        cameraImageUri = requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            takePictureIntent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                cameraImageUri
            ) // Salva l'immagine nella galleria
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_PICK)
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK)
    }

    // ... Other code ...
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val imageUri = if (cameraImageUri != null) cameraImageUri else data!!.data
            if (imageUri != null) {
                CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(requireActivity())
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK && result != null) {
                val croppedImageUri = result.uri
                processCroppedImage(croppedImageUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result!!.error
                Toast.makeText(requireContext(), "Crop error: " + error.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun processCroppedImage(croppedImageUri: Uri) {
        ocrManager!!.processImage(croppedImageUri, object : OCRListener {
            override fun onTextRecognized(value: String?) {
                // Esegui sulla UI thread
                if (activity != null) {
                    requireActivity().runOnUiThread {
                        Log.d("OCRManager", "Updating UI with OCR result")
                        // Imposta direttamente il testo riconosciuto
                        numberEditText!!.setText(value)
                    }
                }
                Log.d("OCRManager", "Text Recognized: $value")
                numberEditText!!.setText(value)
            }

            override fun onTextNotRecognized(error: String?) {
                if (activity != null) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(e: Exception?) {
                if (activity != null) {
                    requireActivity().runOnUiThread {
                        e!!.printStackTrace()
                        Toast.makeText(context, "Errore OCR", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun setExpense() {
        // Cambia il colore del pulsante e imposta la sua proprietà "selected" a true
        expenseButton!!.setSelected(true)
        expenseButton!!.setBackgroundColor(Color.parseColor("#00cc44")) // Verde quando selezionato
        incomeButton!!.setSelected(false)
        incomeButton!!.setBackgroundColor(Color.parseColor("#e06666")) // rosso quando non selezionato
    }

    private fun setIncome() {
        incomeButton!!.setSelected(true)
        incomeButton!!.setBackgroundColor(Color.parseColor("#00cc44")) // Verde quando selezionato
        expenseButton!!.setSelected(false)
        expenseButton!!.setBackgroundColor(Color.parseColor("#e06666")) // rosso quando non selezionato
    }

    private fun deleteTransaction() {
        try {
            writeSql.deleteTransaction(transactionOriginal.id)
            Toast.makeText(context, "Transazione eliminata", Toast.LENGTH_LONG).show()
            // Logica per tornare indietro o aggiornare UI
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Errore durante l'eliminazione della transazione",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    private fun updateTransaction() {
        val newIncome = incomeButton?.isSelected ?: false
        val newAmount = numberEditText?.text.toString().toDoubleOrNull() ?: 0.0
        val categoryId = categories?.get(categorySpinner?.selectedItemPosition ?: 0)?.id ?: -1
        val accountId = accounts?.get(accountSpinner?.selectedItemPosition ?: 0)?.id ?: -1
        val newDate = calendar?.timeInMillis ?: System.currentTimeMillis()

        val calendarDate: Calendar = Calendar.getInstance().apply {
            timeInMillis = newDate // dove newDate è il valore Long
        }

        val cityId =
            readSql.getIdByCityName(locationEditText?.text.toString()) // Implementare questo metodo in readSQL

        val updatedTransaction = Transactions(
            transactionOriginal.id,
            newIncome,
            newAmount,
            calendarDate,
            cityId,
            categoryId,
            accountId
        )
        try {
            writeSql.updateTransaction(updatedTransaction)
            Toast.makeText(context, "Transazione aggiornata", Toast.LENGTH_LONG).show()
            // Logica per tornare indietro o aggiornare UI
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Errore durante l'aggiornamento della transazione",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    //scelta della data
    private fun showDatePickerDialog() {
        val newCalendar = Calendar.getInstance()
        val year = newCalendar[Calendar.YEAR]
        val month = newCalendar[Calendar.MONTH]
        val dayOfMonth = newCalendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog =
            DatePickerDialog(requireContext(), { view, year, monthOfYear, dayOfMonth ->
                newCalendar[Calendar.YEAR] = year
                newCalendar[Calendar.MONTH] = monthOfYear
                newCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                calendar = newCalendar
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val selectedDateString = dateFormat.format(calendar!!.time)
                println("$selectedDateString selectedDateString")
                selectedTimeTextView!!.text = selectedDateString
            }, year, month, dayOfMonth)
        datePickerDialog.show()
    }

}
