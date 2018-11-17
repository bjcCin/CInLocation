package com.wehack.cinlocation

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import android.widget.Toast
import fragments.AddFragment
import fragments.HomeFragment
import fragments.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
        BottomNavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemReselectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Toolbar do projeto
        val toolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        //BottomNav do projeto
        bottom_nav.setOnNavigationItemSelectedListener(this)

        //Status bar transparente (tá bugando o toolbar, deixar off)
        //val w: Window = getWindow()
        //w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)


        //Ao começar o App, a aplicação deverá startar no fragment principal
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId

        if (id == R.id.profile_id)
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()

        return super.onOptionsItemSelected(item)
    }

    //Bons padroes idicam que aqui deve ser implementado a funcao de atualizar a lista
    override fun onNavigationItemReselected(item: MenuItem) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        //Seleciona o fragmento de tela que o usuário selecionou no navBottom
        val fragment: Fragment = when (id) {
            R.id.nav_home -> HomeFragment()
            R.id.nav_add -> AddFragment()
            R.id.nav_search -> SearchFragment()
            else -> HomeFragment()
        }


        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
        return true
    }


}
