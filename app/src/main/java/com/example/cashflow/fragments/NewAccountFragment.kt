package com.example.cashflow.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cashflow.R
import com.example.cashflow.dataClass.*
import com.example.cashflow.db.SQLiteDB
import com.example.cashflow.db.readSQL
import com.example.cashflow.db.writeSQL

class NewAccountFragment(private val readSQL: readSQL, private val writeSQL: writeSQL)  : Fragment() {
    private var edtName: EditText? = null

    private var accounts: ArrayList<Account>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_account, container, false)

        edtName = view.findViewById(R.id.edtName)
        val btnCreateAccount = view.findViewById<Button>(R.id.btnCreateAccount)
        btnCreateAccount.setOnClickListener {
            createAccount()
        }
        return view
    }

    private fun createAccount() {
        val name = edtName?.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(activity, "Inserisci il nome dell'account", Toast.LENGTH_SHORT).show()
        } else {
            // Controlla se l'account esiste già nel database
            if (!readSQL.doesAccountExist(name)) {
                // Crea un nuovo account nel database
                val success =
                    writeSQL.createAccount(name, 0.0) // Assumiamo che il saldo iniziale sia 0
                if (success) {
                    Toast.makeText(activity, "Conto creato!", Toast.LENGTH_SHORT).show()
                    // Opcionalmente, ritorna al fragment o all'activity precedente
                    //TODO: Test if this workd
                    //getFatherFragmentManager()
                    fragmentManager?.popBackStack()
                } else {
                    Toast.makeText(activity, "Errore nella creazione del conto", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(activity, "Un conto con questo nome esiste già", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
