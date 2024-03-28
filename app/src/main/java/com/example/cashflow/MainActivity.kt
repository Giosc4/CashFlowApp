package com.example.cashflow

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import com.google.android.material.navigation.NavigationView
import com.example.cashflow.box.*
import com.example.cashflow.dataClass.*
import com.example.cashflow.db.*
import com.example.cashflow.utils.Posizione


class MainActivity : AppCompatActivity() {
    private val viewModel: DataViewModel by viewModels()
    private var readSQL: ReadSQL? = null
    private var writeSQL: WriteSQL? = null


    private var accounts: ArrayList<Account>? = null
    private var city: City? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        val hamburgerMenu: ImageButton = findViewById(R.id.menu_hamburger)
        val btnHome: ImageView = findViewById(R.id.logo)
        val toolbar_title: TextView = findViewById(R.id.toolbar_title)

        var posizione: Posizione? = null

        readSQL = viewModel.getReadSQL()
        writeSQL = viewModel.getWriteSQL()



//        val isDeleted: Boolean = this.deleteDatabase("cashflow.db")
//        if (isDeleted) {
//            Log.d("SQLiteDB", "Database deleted successfully")
//        } else {
//            Log.d("SQLiteDB", "Failed to delete database")
//        }

        accounts = readSQL!!.getAccounts()

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        }
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
            city?.let {
                loadMenuManagerFragment(menuItem.itemId, it)
                Log.d("MainActivity", "City: $it")
            } ?: run {
                loadMenuManagerFragment(menuItem.itemId, city!!)
                Log.e("MainActivity", "City: $city")
            }
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
        toolbar_title.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            null
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
        val fragment = box_manager_fragment()
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
