package com.wehack.cinlocation

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Status bar transparente
        val w: Window = getWindow()
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val mList: ArrayList<Item>? = ArrayList()
        mList?.add(Item(R.drawable.biblioteca_ufpe, "Pegar livro de matemática", "BC UFPE"))
        mList?.add(Item(R.drawable.cin_ufpe, "Primeiro acompanhamento android", "CIn UFPE"))
        mList?.add(Item(R.drawable.riomar, "Comprar celular", "Rio Mar"))
        mList?.add(Item(R.drawable.marco_zero, "Passar na InLoco", "Marco Zero"))
        mList?.add(Item(R.drawable.conde_boa_vista, "Tirar o VEM", "Conde da BV"))

        Log.e("mList",mList?.get(0)?.title)
        val adapter = Adapter(mList)

        val recyclerView: RecyclerView = findViewById(R.id.rv_list)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

}
