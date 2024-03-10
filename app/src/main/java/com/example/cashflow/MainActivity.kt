package com.example.cashflow

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.cashflow.box.*
import com.example.cashflow.dataClass.*
import com.example.cashflow.statistics.*
import com.google.android.material.navigation.NavigationView
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private var accounts: ArrayList<Account>? = null
    var jsonReadWrite: JsonReadWrite? = null
    var city: City? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var posizione: Posizione? = null

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        val hamburgerMenu: ImageButton = findViewById(R.id.menu_hamburger)
        val btnHome: ImageView = findViewById(R.id.logo)


        posizione = Posizione(this)
        posizione!!.requestDeviceLocation(object : Posizione.DeviceLocationCallback {
            override fun onLocationFetched(city: City?) {
                this@MainActivity.city = city
            }

            override fun onLocationFetchFailed(e: Exception?) {
                // Gestisci l'errore in base alle tue esigenze
            }
        })

        setSupportActionBar(myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            var fragment: Fragment? = null

            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    // Pulisci il backstack per evitare pile di istanze di MainActivity
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    // Opzionale: animazione di transizione personalizzata
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                    Log.d("Menu-Hamburger", "HomeFragment")
                }

                R.id.new_conto -> {
                    fragment = NewAccountFragment(accounts!!)
                    Log.d("Menu-Hamburger", "NewAccountFragment")
                }

                R.id.new_transaction -> {
                    fragment = NewTransactionFragment(accounts!!, null)
                    Log.d("Menu-Hamburger", "NewTransactionFragment")
                }

                R.id.new_budget -> {
                    fragment = NewBudgetFragment()
                    Log.d("Menu-Hamburger", "NewBudgetFragment")
                }

                R.id.new_debit_credit -> {
                    fragment = NewDebitCreditFragment(accounts!!)
                    Log.d("Menu-Hamburger", "NewDebitCreditFragment")
                }

                R.id.line_chart -> {
                    fragment = Line_chart(accounts!!)
                    Log.d("Menu-Hamburger", "Line_chart")
                }

                R.id.maps -> {
                    fragment = MapFragment(accounts!!)
                    Log.d("Menu-Hamburger", "MapFragment")
                }

                R.id.income_chart -> {
                    fragment = Income_expense(true, accounts!!)
                    Log.d("Menu-Hamburger", "Income_expense")
                }

                R.id.expense_chart -> {
                    fragment = Income_expense(false, accounts!!)
                    Log.d("Menu-Hamburger", "Income_expense")
                }
                // Aggiungere qui ulteriori casi per altri elementi del menu
            }

            // Se il fragment non è null, effettuare la transazione
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.linearContainer, it)
                    .addToBackStack(null)
                    .commit()
            }

            // Chiudere il drawer dopo la selezione dell'elemento
            drawerLayout.closeDrawer(GravityCompat.START)
            true // Consumare l'evento di click
        }

        hamburgerMenu.setOnClickListener {
            // Gestisci qui l'apertura del tuo menu o drawer
            drawerLayout.openDrawer(GravityCompat.START)
        }

        myToolbar.setNavigationOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        btnHome.setOnClickListener {
            loadHomeFragment()
        }


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

        addBoxFragment(box_template_fragment(), "box_template_fragment")
        addBoxFragment(box_transaction_fragment(), "box_transaction_fragment")
        addBoxFragment(box_budget_fragment(), "box_budget_fragment")
        addBoxFragment(box_list_debito_fragment(), "box_list_debito_fragment")
        addBoxFragment(box_list_credito_fragment(), "box_list_credito_fragment")
        addBoxFragment(box_account_fragment(accounts), "box_account_fragment")
    }

    private fun addBoxFragment(fragment: Fragment, tag: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val frame = LinearLayout(this)
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
                loadHomeFragment()
            } else {
                println("Location permission is required to fetch the location")
            }
        }
    }

    private fun loadHomeFragment() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }
}
