package fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wehack.cinlocation.Adapter
import com.wehack.cinlocation.Item
import com.wehack.cinlocation.R
import com.wehack.cinlocation.database.ReminderDatabase
import com.wehack.cinlocation.model.Reminder

import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class HomeFragment : Fragment() {

    var adapter: Adapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val inflate = inflater.inflate(R.layout.fragment_home,container, false)!!

        val mList: ArrayList<Item>? = ArrayList()
        mList?.add(Item(R.drawable.biblioteca_ufpe, "Pegar livro de matemática", "BC UFPE"))
        mList?.add(Item(R.drawable.cin_ufpe, "Primeiro acompanhamento android", "CIn UFPE"))
        mList?.add(Item(R.drawable.riomar, "Comprar celular", "Rio Mar"))
        mList?.add(Item(R.drawable.marco_zero, "Passar na InLoco", "Marco Zero"))
        mList?.add(Item(R.drawable.conde_boa_vista, "Tirar o VEM", "Conde da BV"))

        val remText = "Shopping"
        val rem = Reminder(text = remText, title = "Comprar Celular")

        var reminderList: List<Reminder>? = ArrayList()
        doAsync {
            val dao = ReminderDatabase.getInstance(context!!)?.reminderDao()
            val long: Long = 3
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