package fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.wehack.cinlocation.R
import com.wehack.cinlocation.database.ReminderDatabase
import com.wehack.cinlocation.model.Reminder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ProfileFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val deleteButton = view.findViewById(R.id.profile_button) as Button

        deleteButton.setOnClickListener {
            deleteAllReminders()
        }

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