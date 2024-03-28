package com.example.cashflow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.cashflow.box.*
import com.example.cashflow.db.*
import com.example.cashflow.fragments.modify.*
import com.example.cashflow.fragments.view.*
import com.google.android.material.navigation.NavigationView

class ModifyActivity : AppCompatActivity() {

    private val viewModel: DataViewModel by viewModels()
    private var readSQL: ReadSQL? = null
    private var writeSQL: WriteSQL? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        val hamburgerMenu: ImageButton = findViewById(R.id.menu_hamburger)
        val btnHome: ImageView = findViewById(R.id.logo)
        val toolbarTitle: TextView = findViewById(R.id.toolbar_title)

        readSQL = viewModel.getReadSQL()
        writeSQL = viewModel.getWriteSQL()

        val fragmentId = intent.getIntExtra("FRAGMENT_ID", -1)
        val accountId = intent.getIntExtra("ACCOUNT_ID", -1)
        val categoryId = intent.getIntExtra("CATEGORY_ID", -1)


        val fragment = when (fragmentId) {
            1 -> EditTransactionFragment.newInstance(accountId)
            2 -> EditCategoryFragment(categoryId)
            3 -> EditTemplateFragment()
            4 -> EditBudgetFragment()
            5 -> EditDebitCreditFragment()
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