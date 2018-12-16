package com.wehack.cinlocation.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.wehack.cinlocation.Adapter
import com.wehack.cinlocation.R
import com.wehack.cinlocation.database.ReminderDatabase
import com.wehack.cinlocation.model.Reminder
import com.wehack.cinlocation.util.SwipableItemCallback
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ProfileFragment: Fragment() {

    var adapter: Adapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        var reminderList: List<Reminder>? = ArrayList()

        doAsync {
            val dao = ReminderDatabase.getInstance(context!!)?.reminderDao()

            reminderList = dao?.getAll()
            val completedReminderList: ArrayList<Reminder> = ArrayList()

            reminderList?.forEach {
                if (it.completed)
                    completedReminderList.add(it)
            }
            uiThread {
                adapter = Adapter(completedReminderList, false)
                val recyclerView: RecyclerView = view.findViewById(R.id.rv2_list)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(context)

            }
        }


//        val deleteButton = view.findViewById(R.id.profile_button) as Button
//
//        deleteButton.setOnClickListener {
//            deleteAllReminders()
//        }

        return view
    }

    fun deleteAllReminders(){

        doAsync {
            val dao = ReminderDatabase.getInstance(context!!)?.reminderDao()
            val listReminders = dao?.getAll()

            if (listReminders != null) {
                for (reminder:Reminder in listReminders){
                    dao.delete(reminder)
                }
            }

            uiThread {
                Toast.makeText(context, "Todos os reminders apagados", Toast.LENGTH_SHORT).show()
            }
        }

    }
}