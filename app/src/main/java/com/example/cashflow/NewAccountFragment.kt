package com.example.cashflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cashflow.dataClass.Account
import java.io.IOException

class NewAccountFragment(private val accounts: ArrayList<Account>) : Fragment() {
    private var jsonReadWrite: JsonReadWrite? = null
    private var edtName: EditText? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_account, container, false)
        jsonReadWrite = JsonReadWrite()
        edtName = view.findViewById(R.id.edtName)
        val btnCreateAccount = view.findViewById<Button>(R.id.btnCreateAccount)
        btnCreateAccount.setOnClickListener {
            val name = edtName?.getText().toString()
            if (name.isEmpty()) {
                Toast.makeText(activity, "Inserisci il nome dell'account", Toast.LENGTH_SHORT)
                    .show()
            } else {
                accounts.add(Account(name))
                try {
                    jsonReadWrite!!.setList(accounts, requireContext())
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
                Toast.makeText(activity, "Conto creato!", Toast.LENGTH_SHORT).show()
                if (activity != null) {
                    val homeFragment = HomeFragment(accounts)
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.linearContainer, homeFragment)
                    transaction.commit()
                }
            }
        }
        return view
    }
}
