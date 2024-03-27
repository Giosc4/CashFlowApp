package com.example.cashflow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentActivity
import com.example.cashflow.box.ViewDebitCreditFragment
import com.example.cashflow.db.*
import com.example.cashflow.fragments.modify.EditTransactionFragment
import com.example.cashflow.fragments.view.*
import com.google.android.material.navigation.NavigationView

class ModifyActivity : AppCompatActivity() {

    private lateinit var db: SQLiteDB
    private lateinit var readSQL: ReadSQL
    private lateinit var writeSQL: WriteSQL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        val hamburgerMenu: ImageButton = findViewById(R.id.menu_hamburger)
        val btnHome: ImageView = findViewById(R.id.logo)
        val toolbarTitle: TextView = findViewById(R.id.toolbar_title)

        db = SQLiteDB(this)
        readSQL = ReadSQL(db.writableDatabase)
        writeSQL = WriteSQL(db.writableDatabase)

        val fragmentId = intent.getIntExtra("FRAGMENT_ID", -1)
        val accountId = intent.getIntExtra("ACCOUNT_ID", -1)


        val fragment = when (fragmentId) {
            1 -> EditTransactionFragment.newInstance(accountId)
//            2 -> EditCategoryFragment(readSQL, writeSQL)
//            3 -> EditTemplateFragment(readSQL, writeSQL)
//            4 -> EditBudgetFragment(readSQL, writeSQL)
//            5 -> EditDebitCreditFragment(readSQL, writeSQL)
            else -> null
        }

        fragment?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.linearContainer, it)
                .addToBackStack(null)
                .commit()
        }


        setSupportActionBar(myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        hamburgerMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        myToolbar.setNavigationOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            null
        }
        toolbarTitle.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            null
        }
    }
}