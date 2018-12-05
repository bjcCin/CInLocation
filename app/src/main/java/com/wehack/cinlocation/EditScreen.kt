package com.wehack.cinlocation

import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.edit_screen.*

class EditScreen() : AppCompatActivity() {

    var item: Item? = null

    var toolbar: Toolbar? = null
    var image: ImageView? = null
    var buttom: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_screen)

        toolbar = viewToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        buttom = viewFloatBtn
        buttom?.setOnClickListener(){
            Snackbar.make(it, "Test", Snackbar.LENGTH_LONG).show()
        }


        image = viewImage
        //image?.setImageResource(item.background)

    }


}