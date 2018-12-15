package fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wehack.cinlocation.Adapter
import com.wehack.cinlocation.R
import com.wehack.cinlocation.database.ReminderDatabase
import com.wehack.cinlocation.model.Reminder
import com.wehack.cinlocation.util.SwipableItemCallback
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class HomeFragment : Fragment() {

    var adapter: Adapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val inflate = inflater.inflate(R.layout.fragment_home,container, false)!!

        var reminderList: List<Reminder>? = ArrayList()
        doAsync {
            val dao = ReminderDatabase.getInstance(context!!)?.reminderDao()
            val long: Long = 3
           // dao?.getAll()?.forEach { dao.delete(it) }

//            val reminder: Reminder = dao?.findById(long)!!
            reminderList = dao?.getAll()

            uiThread {
                for (reminder:Reminder in reminderList!!){
                    Log.i("imagemID", reminder.image.toString())
                }

                adapter = Adapter(reminderList)
                val recyclerView: RecyclerView = inflate.findViewById(R.id.rv_list)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(context)
                val swipeHandler = object : SwipableItemCallback(context!!) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                        adapter?.removeAt(viewHolder?.adapterPosition!!)
                    }
                }
                val itemTouchHelper = ItemTouchHelper(swipeHandler)
                itemTouchHelper.attachToRecyclerView(recyclerView)
            }

        }

        return inflate
    }

    fun makeQuery(query: String){
        adapter?.getFilter()?.filter(query)

    }

    fun sortedBy(option: Int){
        adapter?.sortedBy(option)
    }


}