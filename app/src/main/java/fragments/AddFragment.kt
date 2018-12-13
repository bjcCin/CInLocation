package fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.vicmikhailau.maskededittext.MaskedEditText
import com.wehack.cinlocation.Adapter
import com.wehack.cinlocation.MainActivity.Companion.locationPermissionGranted
import com.wehack.cinlocation.R
import com.wehack.cinlocation.database.ReminderDatabase
import com.wehack.cinlocation.model.Reminder
import kotlinx.android.synthetic.main.fragment_add.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng


class AddFragment : Fragment() {
    var mMap: SupportMapFragment? = null
    var toolbar: Toolbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        val addbutton = view.findViewById(R.id.addCadastro) as Button
        val editImageButton = view.findViewById(R.id.viewFloatBtn) as FloatingActionButton
        val reminderText = view.findViewById(R.id.add_reminderText) as EditText
        val reminderTitle = view.findViewById(R.id.add_reminderTittle) as AppCompatEditText



        addbutton.setOnClickListener {
            val remindEndDate = view.findViewById(R.id.addDateE_textInputLayout) as? MaskedEditText
            val remindStartDate = view.findViewById(R.id.addDateS_textInputLayout) as? MaskedEditText

            addReminder(reminderTitle.text.toString(), reminderText.text.toString(),
                    remindEndDate?.text.toString(), remindStartDate?.text.toString())
        }

        editImageButton.setOnClickListener {
            Toast.makeText(context, "Editar Imagem", Toast.LENGTH_SHORT).show()
        }

        mMap = SupportMapFragment.newInstance()

        mMap?.getMapAsync(OnMapReadyCallback { googleMap ->
            mapConfigs(googleMap)
        })

        getChildFragmentManager().beginTransaction().replace(R.id.addMap, mMap).commit();

        return view
    }

    fun mapConfigs(googleMap: GoogleMap){
        val latLng = LatLng(-8.0556681, -34.951578)
        googleMap.addMarker(MarkerOptions().position(latLng)
                .title("Centro de Informatica - UFPE"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.setMinZoomPreference(googleMap.minZoomLevel + 17)

        googleMap.setOnMapClickListener {
            Toast.makeText(context, "Map clicado", Toast.LENGTH_SHORT).show()
        }

    }


    @SuppressLint("SimpleDateFormat")
    fun addReminder(title: String, text: String, textEndDate: String?, textStartDate: String?) {

        val df = SimpleDateFormat("dd/MM/yyyy")
        df.setLenient(false)
        Log.d("datas", "final ${textEndDate} inicial ${textStartDate}")
        val endDate: Date = df.parse(textEndDate)
        val startDate: Date = df.parse(textStartDate)

        val rem = Reminder(title = title, text = text, endDate = endDate, beginDate = startDate)
        Log.e("printData", "${endDate} and ${startDate}")


        doAsync {
            val dao = ReminderDatabase.getInstance(context!!)?.reminderDao()
            dao?.insert(rem)
            uiThread {
                Toast.makeText(context, "Reminder cadastrado com sucesso", Toast.LENGTH_SHORT).show()
            }


        }
    }


}