package com.wehack.cinlocation

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.wehack.cinlocation.R.id.action_search
import fragments.AddFragment
import fragments.HomeFragment
import fragments.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import com.wehack.cinlocation.R.id.action_sortby


class MainActivity : AppCompatActivity(),
        BottomNavigationView.OnNavigationItemSelectedListener{

    companion object {
        val CURRENT_LOCATION_REQUEST_CODE = 42
        var locationPermissionGranted = false

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, MainActivity::class.java)
            return intent
        }
    }

    private var searchView: SearchView? = null
    private var fragment_home = HomeFragment()
    private var fragment_profile = ProfileFragment()
    private var fragment_add = AddFragment()
    private var toolbar: Toolbar? = null
    private var searchMenuItem: MenuItem? = null
    private var menuToolbar: Menu? = null
    private var bottomNavigation: BottomNavigationView? = null
    private var dialog: AlertDialog? = null
    private var sortMenuItem: MenuItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Toolbar do projeto
        toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        bottomNavigation = bottom_nav

        //BottomNav do projeto
        bottomNavigation?.setOnNavigationItemSelectedListener(this)

        //Status bar transparente (tá bugando o toolbar, deixar off)
        //val w: Window = getWindow()
        //w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)


        //Ao começar o App, a aplicação deverá startar no fragment principal
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment_home).commit()
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    CURRENT_LOCATION_REQUEST_CODE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        menuToolbar = menu
        searchMenuItem = menuToolbar?.findItem(action_search)
        sortMenuItem = menuToolbar?.findItem(action_sortby)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        searchView!!.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView!!.setMaxWidth(Integer.MAX_VALUE)

        // mudança de texto da query
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // envio da consulta
                fragment_home.makeQuery(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                // mudanca do texto
                fragment_home.makeQuery(query)
                return false
            }
        })

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            CURRENT_LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED)
                    locationPermissionGranted = true
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId

        when (id) {
            R.id.action_search -> return true
            R.id.action_sortby -> {
                showDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Method to show an alert dialog with single choice list items
    private fun showDialog() {
        lateinit var dialog: AlertDialog
        val array = arrayOf("Nome", "Data")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ordenar por:")

        builder.setSingleChoiceItems(array, -1,
                { _, which ->

                    fragment_home.sortedBy(which)

                    dialog.dismiss()
                })

        dialog = builder.create()
        dialog.show()
    }


    // Extension function to show toast message
    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.nav_home -> {
                val fragment = HomeFragment()
                searchMenuItem?.setVisible(true)
                sortMenuItem?.setVisible(true)
                addFragment(fragment)
                return true
            }
            R.id.nav_add -> {
                val fragment = AddFragment()
                searchMenuItem?.setVisible(false)
                sortMenuItem?.setVisible(false)
                addFragment(fragment)
                return true
            }
//            R.id.nav_profile -> {
//                val fragment = ProfileFragment()
//                searchMenuItem?.setVisible(false)
//                addFragment(fragment)
//                return true
//            }
        }

        return false
    }

    /**
     * add/replace fragment in container [framelayout]
     */
    @SuppressLint("PrivateResource")
    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
                .replace(R.id.fragment_container, fragment, fragment.javaClass.getSimpleName())
                .addToBackStack(fragment.javaClass.getSimpleName())
                .commit()
    }


}
