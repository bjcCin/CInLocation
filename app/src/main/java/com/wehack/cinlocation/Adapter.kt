package com.wehack.cinlocation

import android.content.Context
import android.hardware.camera2.TotalCaptureResult
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class Adapter (val mData: List<Item>?) : RecyclerView.Adapter<Adapter.myViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder? {

        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val v: View = inflater.inflate(R.layout.card_item, parent, false)
        return myViewHolder(v, parent.context)

    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        holder.backgroundImage?.setImageResource(mData?.get(position)!!.background)
        holder.title?.setText(mData?.get(position)!!.title)
        holder.location?.setText(mData?.get(position)!!.location)
        holder.edit?.setOnClickListener {
            holder.itemClick()
        }

    }

    override fun getItemCount(): Int {
        if (mData != null)
            return mData.size
        return 0
    }

    inner class myViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {
        var backgroundImage: ImageView? = null
        var title: TextView? = null
        var location: TextView? = null
        var edit: Button? = null


        init {
            title = itemView.findViewById(R.id.card_title)
            location = itemView.findViewById(R.id.card_location)
            backgroundImage = itemView.findViewById(R.id.card_background)
            edit = itemView.findViewById(R.id.btn_edit)

        }


        fun itemClick() {
            Toast.makeText(context,"${title?.text}",Toast.LENGTH_SHORT).show()
        }


    }
}