package com.example.cashflow.fragments.modify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cashflow.MainActivity
import com.example.cashflow.R
import com.example.cashflow.dataClass.Category
import com.example.cashflow.db.*

class EditCategoryFragment(
    private val categoryId: Int,

) : Fragment() {
    private var edtNameCategory: EditText? = null
    private var editTextDescription: EditText? = null
    private var deleteButton: Button? = null
    private var doneButton: Button? = null
    private val viewModel: DataViewModel by viewModels()
    private var readSQL: ReadSQL? = null
    private var writeSQL: WriteSQL? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_category, container, false)

        edtNameCategory = view.findViewById(R.id.edtNameCategory)
        editTextDescription = view.findViewById(R.id.editTextDescription)
        deleteButton = view.findViewById(R.id.deleteButton)
        doneButton = view.findViewById(R.id.doneButton)
        readSQL = viewModel.getReadSQL()
        writeSQL = viewModel.getWriteSQL()

        loadCategoryData()

        deleteButton!!.setOnClickListener {
            deleteCategory(categoryId)
        }

        doneButton!!.setOnClickListener {
            saveCategory(edtNameCategory!!.text.toString(), editTextDescription!!.text.toString())
        }

        return view
    }

    private fun loadCategoryData() {
        // Simula il caricamento dei dati della categoria. Sostituire con il codice di lettura effettivo.
        val category = readSQL?.getCategoryById(categoryId)
         edtNameCategory?.setText(category?.name)
         editTextDescription?.setText(category?.description)
    }

    private fun deleteCategory(categoryId: Int) {
        try {
            writeSQL?.deleteCategory(categoryId)
            Toast.makeText(context, "Transazione eliminata", Toast.LENGTH_LONG).show()
            // Logica per tornare indietro o aggiornare UI
            Intent(context, MainActivity::class.java).also {
                startActivity(it)
            }
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Errore durante l'eliminazione della transazione",
                Toast.LENGTH_LONG
            ).show()
        }

        // es: writeSQL.deleteCategory(categoryId)
        Toast.makeText(context, "Categoria eliminata.", Toast.LENGTH_SHORT).show()
        // Potresti voler chiudere il fragment dopo l'eliminazione
    }

    private fun saveCategory(name: String, description: String) {
        if (name.isBlank() || description.isBlank()) {
            Toast.makeText(context, "Nome e descrizione sono richiesti.", Toast.LENGTH_SHORT).show()
            Log.d("EditCategoryFragment", "Nome e descrizione sono vuoti")
            return
        }
        // Qui dovrebbe andare il codice per salvare le modifiche di una categoria nel database.
        // es: writeSQL.updateCategory(Category(id, name, description))
        Toast.makeText(context, "Modifiche salvate.", Toast.LENGTH_SHORT).show()
        // Potresti voler chiudere il fragment dopo il salvataggio
    }
}
