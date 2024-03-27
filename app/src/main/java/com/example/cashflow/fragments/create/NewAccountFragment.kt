package com.example.cashflow.fragments.create

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cashflow.MainActivity
import com.example.cashflow.R
import com.example.cashflow.dataClass.*
import com.example.cashflow.db.ReadSQL
import com.example.cashflow.db.WriteSQL

class NewAccountFragment(private val readSQL: ReadSQL, private val writeSQL: WriteSQL)  : Fragment() {
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
            if (!readSQL.doesAccountExist(name)) {
                val success =
                    writeSQL.createAccount(name, 0.0) // Assumiamo che il saldo iniziale sia 0
                if (success) {
                    Toast.makeText(activity, "Conto creato!", Toast.LENGTH_SHORT).show()

                    // Riavvia MainActivity
                    val intent = Intent(activity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(activity, "Errore nella creazione del conto", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(activity, "Un conto con questo nome esiste già", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
