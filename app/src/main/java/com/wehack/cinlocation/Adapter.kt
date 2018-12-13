package com.wehack.cinlocation

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.wehack.cinlocation.model.Reminder
import java.util.*

class Adapter (val mData: List<Reminder>?) : RecyclerView.Adapter<Adapter.myViewHolder>(), Filterable  {

    var mDataFiltered: List<Reminder>? = null
    var imagens: ArrayList<Int> = ArrayList()


    init {
        mDataFiltered = mData
        imagens.add(R.drawable.biblioteca_ufpe)
        imagens.add(R.drawable.cin_ufpe)
        imagens.add(R.drawable.conde_boa_vista)
        imagens.add(R.drawable.marco_zero)

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val v: View = inflater.inflate(R.layout.card_item, parent, false)
        return myViewHolder(v, parent.context)

    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val random: Random = Random()
        val x: Int = random.nextInt(3)
        val selectedImagem = imagens.get(x)

        holder.backgroundImage?.setImageResource(selectedImagem)
        holder.title?.setText(mDataFiltered?.get(position)!!.title)
        holder.location?.setText(mDataFiltered?.get(position)!!.text)

    }

    override fun getItemCount(): Int {
        if (mDataFiltered != null)
            return mDataFiltered!!.size
        return 0
    }

    override fun getFilter(): Filter {

       return object : Filter(){
           override fun performFiltering(p0: CharSequence?): FilterResults {
               val charString = p0.toString()
               if(charString.isEmpty())
                   mDataFiltered = mData
               else{
                   val filteredList: ArrayList<Reminder> = ArrayList()
                   if (mData != null) {
                       for(row in mData){
                           if(row.title.toLowerCase().contains(charString.toLowerCase()))
                               filteredList.add(row)
                       }
                   }
                   mDataFiltered = filteredList
               }

               val filterResults =  Filter.FilterResults()
               filterResults.values = mDataFiltered
               return filterResults

           }

           override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
               mDataFiltered = p1?.values as ArrayList<Reminder>
               notifyDataSetChanged()
           }

       }

    }

    inner class myViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var backgroundImage: ImageView? = null
        var title: TextView? = null
        var location: TextView? = null

        init {
            title = itemView.findViewById(R.id.card_title)
            location = itemView.findViewById(R.id.card_location)
            backgroundImage = itemView.findViewById(R.id.card_background)
            itemView.setOnClickListener(this)

        }

        override fun onClick(p0: View?) {
            val pos = adapterPosition
            if(pos != RecyclerView.NO_POSITION){
                val myIntent = Intent(context, EditScreen::class.java)
                myIntent.putExtra("itemId", mDataFiltered?.get(pos)?.id)
                context.startActivity(myIntent)
            } else
                Toast.makeText(context, "Erro ao abrir o arquivo", Toast.LENGTH_SHORT).show()
        }

    }
}