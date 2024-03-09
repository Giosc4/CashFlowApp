package com.example.cashflow

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.cashflow.box.box_budget_fragment
import com.example.cashflow.box.box_list_credito_fragment
import com.example.cashflow.box.box_list_debito_fragment
import com.example.cashflow.box.box_template_fragment
import com.example.cashflow.box.box_transaction_fragment
import com.example.cashflow.dataClass.Account
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private var btnHome: Button? = null
    private var accounts: ArrayList<Account>? = null
    var jsonReadWrite: JsonReadWrite? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(myToolbar)

        // Rimuovi il titolo predefinito per evitare sovrapposizioni
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val hamburgerMenu: ImageButton = findViewById(R.id.menu_hamburger)
        hamburgerMenu.setOnClickListener {
            // Gestisci qui l'apertura del tuo menu o drawer
        }

        val btnHome: ImageView = findViewById(R.id.logo)
        btnHome.setOnClickListener {
            loadFragment(
                HomeFragment(
                    jsonReadWrite!!.readAccountsFromJson(
                        this@MainActivity
                    )
                )
            )
        }

        addBoxFragment(box_template_fragment(), "box_template_fragment")
        addBoxFragment(box_transaction_fragment(), "box_transaction_fragment")
        addBoxFragment(box_budget_fragment(), "box_budget_fragment")
        addBoxFragment(box_list_debito_fragment(), "box_list_debito_fragment")
        addBoxFragment(box_list_credito_fragment(), "box_list_credito_fragment")

        // Inizializza il JsonReadWrite
        jsonReadWrite = JsonReadWrite()
        accounts = jsonReadWrite!!.readAccountsFromJson(this@MainActivity)
        //accounts = null;
        if (accounts == null) {
            // Le righe di codice devono essere eseguite solo all'installazione dell'app.
            val test = Test()
            accounts = test.list
            jsonReadWrite = JsonReadWrite(test.list)
        }
        try {
            jsonReadWrite!!.setList(accounts, this)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        }
//        btnHome = findViewById(R.id.btnHome)
//        btnHome?.setBackgroundColor(Color.parseColor("#37a63e"))
//        btnHome?.setOnClickListener(View.OnClickListener {
//
//        })
        loadFragment(HomeFragment(jsonReadWrite!!.readAccountsFromJson(this@MainActivity)))
    }

    private fun addBoxFragment(fragment: Fragment, tag: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val frame = FrameLayout(this)
        frame.setId(View.generateViewId())
        val dynamicContainer = findViewById<LinearLayout>(R.id.linearContainer)
        dynamicContainer.addView(frame)
        fragmentTransaction.add(frame.id, fragment, tag)
        fragmentTransaction.commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadFragment(HomeFragment(jsonReadWrite!!.readAccountsFromJson(this@MainActivity)))
            } else {
                println("Location permission is required to fetch the location")
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.linearContainer, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }
}
