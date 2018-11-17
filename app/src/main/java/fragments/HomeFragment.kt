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
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val inflate = inflater.inflate(R.layout.fragment_home,container, false)!!

        val mList: ArrayList<Item>? = ArrayList()
        mList?.add(Item(R.drawable.biblioteca_ufpe, "Pegar livro de matemática", "BC UFPE"))
        mList?.add(Item(R.drawable.cin_ufpe, "Primeiro acompanhamento android", "CIn UFPE"))
        mList?.add(Item(R.drawable.riomar, "Comprar celular", "Rio Mar"))
        mList?.add(Item(R.drawable.marco_zero, "Passar na InLoco", "Marco Zero"))
        mList?.add(Item(R.drawable.conde_boa_vista, "Tirar o VEM", "Conde da BV"))

        val adapter = Adapter(mList)

        val recyclerView: RecyclerView = inflate.findViewById(R.id.rv_list)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        return inflate
    }


}