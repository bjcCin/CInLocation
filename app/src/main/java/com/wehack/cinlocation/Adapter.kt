package com.wehack.cinlocation

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.wehack.cinlocation.database.ReminderManagerImp
import com.wehack.cinlocation.model.Reminder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.FileInputStream
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

        if(mDataFiltered?.get(position)?.image != null){
            val f = File(mDataFiltered?.get(position)?.image)
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            holder.backgroundImage?.setImageBitmap(b)
           // holder.backgroundImage?.setImageURI(mDataFiltered?.get(position)!!.image)
        } else {
            holder.backgroundImage?.setImageResource(R.drawable.sem_foto)
        }

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

    fun sortedBy(value: Int){
        when(value){
            0 -> mDataFiltered = mDataFiltered?.sortedBy { it.title }
            1 -> mDataFiltered = mDataFiltered?.sortedBy { it.beginDate }
        }
        notifyDataSetChanged()
    }



    inner class myViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {

        var backgroundImage: ImageView? = null
        var title: TextView? = null
        var location: TextView? = null
        var deleteButton: ImageButton? = null


        init {
            title = itemView.findViewById(R.id.card_title)
            location = itemView.findViewById(R.id.card_location)
            backgroundImage = itemView.findViewById(R.id.card_background)
            itemView.setOnClickListener{
                reminderSelected()
            }
            deleteButton = itemView.findViewById(R.id.btnHome_delete)
            deleteButton?.setOnClickListener{
                deleteReminder()
            }
        }


        /**
         * Delete reminderById on selected trash icon
         */

        fun deleteReminder(){
            val pos = adapterPosition
            doAsync {
//                val dao = ReminderDatabase.getInstance(context)?.reminderDao()
                val reminderManager = ReminderManagerImp.getInstance(context)
                reminderManager?.delete(mDataFiltered?.get(pos)!!)
                val newReminders = reminderManager?.getAll()
                uiThread {
                    mDataFiltered = newReminders
                    notifyDataSetChanged()
                }
            }
        }

        /**
         * Open edit screen by ReminderId
         */

        fun reminderSelected() {
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