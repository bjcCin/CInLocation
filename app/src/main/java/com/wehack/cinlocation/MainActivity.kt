package com.wehack.cinlocation

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem

import android.widget.Toast
import fragments.AddFragment
import fragments.HomeFragment
import fragments.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*
import android.app.SearchManager
import android.content.Context
import android.support.v7.widget.SearchView
import com.wehack.cinlocation.R.id.action_search


class MainActivity : AppCompatActivity(),
        BottomNavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemReselectedListener {

    private var searchView: SearchView? = null
    private var fragment_home = HomeFragment()
    private var fragment_profile = ProfileFragment()
    private var fragment_add = AddFragment()
    private var toolbar: Toolbar? = null
    private var searchMenuItem: MenuItem? = null
    private var menuToolbar: Menu? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Toolbar do projeto
        toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        //BottomNav do projeto
        bottom_nav.setOnNavigationItemSelectedListener(this)

        //Status bar transparente (tá bugando o toolbar, deixar off)
        //val w: Window = getWindow()
        //w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)


        //Ao começar o App, a aplicação deverá startar no fragment principal
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment_home).commit()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        menuToolbar = menu
        searchMenuItem = menuToolbar?.findItem(action_search)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        searchView!!.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView!!.setMaxWidth(Integer.MAX_VALUE)

        // query text changes
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // query submit
                fragment_home.makeQuery(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                // query change
                fragment_home.makeQuery(query)
                return false
            }
        })

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId

        if (id == R.id.action_search){
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    //Bons padroes idicam que aqui deve ser implementado a funcao de atualizar a lista
    override fun onNavigationItemReselected(item: MenuItem) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        item.setVisible(false)

        //Seleciona o fragmento de tela que o usuário selecionou no navBottom
        val fragment: Fragment = when (id) {
            R.id.nav_home -> fragment_home
            R.id.nav_add -> fragment_add
            R.id.nav_profile -> fragment_profile
            else -> HomeFragment()
        }


        if(id == R.id.nav_add  || id == R.id.nav_profile)
            searchMenuItem?.setVisible(false)
        else
            searchMenuItem?.setVisible(true)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
        return true
    }


}
