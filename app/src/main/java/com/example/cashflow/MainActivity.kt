package com.example.cashflow

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.example.cashflow.box.*
import com.example.cashflow.dataClass.*
import com.example.cashflow.db.*


class MainActivity : AppCompatActivity() {
    private lateinit var db: SQLiteDB
    private lateinit var readSQL: ReadSQL
    private lateinit var writeSQL: WriteSQL
    private var accounts: ArrayList<Account>? = null
    private var city: City? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var posizione: Posizione? = null

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        val hamburgerMenu: ImageButton = findViewById(R.id.menu_hamburger)
        val btnHome: ImageView = findViewById(R.id.logo)


        db = SQLiteDB(this)
        readSQL = ReadSQL(db.writableDatabase)
        writeSQL = WriteSQL(db.writableDatabase)


//        val isDeleted: Boolean = this.deleteDatabase("cashflow.db")
//        if (isDeleted) {
//            Log.d("SQLiteDB", "Database deleted successfully")
//        } else {
//            Log.d("SQLiteDB", "Failed to delete database")
//        }

        accounts = readSQL.getAccounts()

        posizione = Posizione(this)
        posizione!!.requestDeviceLocation(object : Posizione.DeviceLocationCallback {
            override fun onLocationFetched(city: City?) {
                this@MainActivity.city = city ?: return
            }

            override fun onLocationFetchFailed(e: Exception?) {
                Log.e("MainActivity", "Failed to fetch location", e)
            }
        })

        setSupportActionBar(myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Passa l'ID dell'elemento del menu a MenuManagerFragment
            loadMenuManagerFragment(menuItem.itemId, city!!)
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
            loadBoxManagerFragment()
        }
        loadBoxManagerFragment()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadBoxManagerFragment()
            } else {
                println("Location permission is required to fetch the location")
            }
        }
    }

    private fun loadBoxManagerFragment() {
        val fragment = box_manager_fragment(readSQL, writeSQL)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.linearContainer, fragment)
            commit()
        }
    }

    private fun loadMenuManagerFragment(selectedMenuId: Int, city: City) {
        val menuManagerFragment = MenuManagerFragment.newInstance(selectedMenuId, city)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.linearContainer, menuManagerFragment)
            addToBackStack(null)
            commit()
        }
    }


    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }
}
