package com.wehack.cinlocation

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class Adapter (val mData: List<Item>?) : RecyclerView.Adapter<Adapter.myViewHolder>(), Filterable  {

    var mDataFiltered: List<Item>? = null

    init {
        mDataFiltered = mData
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val v: View = inflater.inflate(R.layout.card_item, parent, false)
        return myViewHolder(v, parent.context)

    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        holder.backgroundImage?.setImageResource(mDataFiltered?.get(position)!!.background)
        holder.title?.setText(mDataFiltered?.get(position)!!.title)
        holder.location?.setText(mDataFiltered?.get(position)!!.location)

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
                   val filteredList: ArrayList<Item> = ArrayList()
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
               mDataFiltered = p1?.values as ArrayList<Item>
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
            Toast.makeText(context,"${title?.text}",Toast.LENGTH_SHORT).show()
        }



    }
}