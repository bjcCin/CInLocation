package fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.vicmikhailau.maskededittext.MaskedEditText
import com.wehack.cinlocation.R
import com.wehack.cinlocation.database.ReminderDatabase
import com.wehack.cinlocation.model.Reminder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*


class AddFragment : Fragment() {
    companion object {
        const val PLACE_PICKER_REQUEST = 1
    }
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

        getChildFragmentManager().beginTransaction().replace(R.id.addMap, mMap).commit()

        return view
    }

    fun mapConfigs(googleMap: GoogleMap){
        val latLng = LatLng(-8.0556681, -34.951578)
        googleMap.addMarker(MarkerOptions().position(latLng)
                .title("Centro de Informatica - UFPE"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.setMinZoomPreference(googleMap.minZoomLevel + 17)

        googleMap.setOnMapClickListener {
            val pickerIntent = PlacePicker.IntentBuilder()
            startActivityForResult(pickerIntent.build(this.activity), PLACE_PICKER_REQUEST)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                updateMapLocation(data)
            }
        }
    }

    private fun updateMapLocation(data: Intent?) {
        val place = PlacePicker.getPlace(this.context, data)
        val latlng = place.latLng
        val name = place.name
        mMap?.getMapAsync {
            it.addMarker(
                    MarkerOptions().position(latlng)
                            .title(String.format("Lembre-me ao chegar em: %s", name))
            ).showInfoWindow()
            it.moveCamera(CameraUpdateFactory.newLatLng(latlng))
            it.setMinZoomPreference(it.minZoomLevel + 17)
        }
    }


}