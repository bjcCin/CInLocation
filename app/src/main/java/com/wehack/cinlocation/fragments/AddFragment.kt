package com.wehack.cinlocation.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatEditText
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.vicmikhailau.maskededittext.MaskedEditText
import com.wehack.cinlocation.MainActivity
import com.wehack.cinlocation.R
import com.wehack.cinlocation.database.ReminderManagerImp
import com.wehack.cinlocation.model.Reminder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import com.wehack.cinlocation.util.stringToDate
import com.wehack.cinlocation.util.saveToInternalStorage
import com.wehack.cinlocation.util.validation
import kotlinx.android.synthetic.main.fragment_add.*


class AddFragment : Fragment() {

    companion object {
        const val PLACE_PICKER_REQUEST = 1
        const val CHANGE_REMINDER_IMAGE = 2
    }

    var mMap: SupportMapFragment? = null

    var addImage: ImageView? = null
    var imageURI: String? = null
    var editImageButton: FloatingActionButton? = null

    var latitude: Double? = null
    var longitude: Double? = null
    var placeName: String? = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        val addbutton = view.findViewById(R.id.addCadastro) as Button
        editImageButton = view.findViewById(R.id.viewFloatBtn) as FloatingActionButton?
        val reminderText = view.findViewById(R.id.add_reminderText) as EditText
        val reminderTitle = view.findViewById(R.id.add_reminderTittle) as AppCompatEditText
        val remindEndDate = view.findViewById(R.id.addDateE_textInputLayout) as? MaskedEditText
        val remindStartDate = view.findViewById(R.id.addDateS_textInputLayout) as? MaskedEditText
        addImage = view.findViewById(R.id.add_imageView) as ImageView

        addbutton.setOnClickListener {

            addReminder(reminderTitle.text.toString(), reminderText.text.toString(),
                    remindEndDate?.text.toString(), remindStartDate?.text.toString(), imageURI)
        }

        editImageButton?.setOnClickListener {
            val pickPhotoIntent = Intent(Intent.ACTION_GET_CONTENT)
            pickPhotoIntent.setType("image/*")
            startActivityForResult(pickPhotoIntent,CHANGE_REMINDER_IMAGE)
        }

        mMap = SupportMapFragment.newInstance()

        mMap?.getMapAsync(OnMapReadyCallback { googleMap ->
            mapConfigs(googleMap)
        })

        getChildFragmentManager().beginTransaction().replace(R.id.addMap, mMap).commit()

        return view
    }

    /**
     * OnResult of addImage button changes image of collapse toolbar
     */
    @SuppressLint("SimpleDateFormat")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CHANGE_REMINDER_IMAGE) {
            val bitMapURI = data.data
            Log.i("bitmapUri", bitMapURI.toString())
            val mBitmap: Bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, bitMapURI)
            addImage?.setImageBitmap(mBitmap)
            doAsync {
                val path = saveToInternalStorage(mBitmap, context)
                uiThread {
                    imageURI = path
                }
            }

        }

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                updateMapLocation(data)
            }
        }
    }


    /**
     * Set map configurations
     *
     * @param googleMap do framelayout que contém o map
     */
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

        googleMap.uiSettings.setAllGesturesEnabled(false)

    }


    /**
     * Cadastra um reminder no app
     *
     * @param title titulo a ser salvo
     * @param text texto do lembrete
     * @param textStartDate data inicial
     * @param textEndDate data final
     * @param imageUri local da imagem
     */
    @SuppressLint("SimpleDateFormat")
    fun addReminder(title: String, text: String, textEndDate: String?, textStartDate: String?, imageUri: String?) {

        val endDate: Date? = stringToDate(textEndDate)
        val startDate: Date? = stringToDate(textStartDate)

        val rem = Reminder(
                title = title,
                text = text,
                endDate = endDate,
                beginDate = startDate,
                image = imageUri,
                lat = latitude,
                lon = longitude,
                placeName = placeName)

        val isValid = validation(rem)

        if(isValid == "ok"){
            doAsync {
                ReminderManagerImp
                        .getInstance(context!!)
                        ?.insert(rem)
                uiThread {
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                }

            }
        } else
            Snackbar.make(newReminderFragment, isValid, Snackbar.LENGTH_SHORT).show()


    }

    /**
     * Faz o upload da localizacao do lembrete
     *
     * @param data vem do startActivityfromResult
     */
    private fun updateMapLocation(data: Intent?) {
        val place = PlacePicker.getPlace(this.context, data)

        val latlng = place.latLng
        latitude  = place.latLng.latitude
        longitude = place.latLng.longitude
        placeName = place.name.toString()
        mMap?.getMapAsync {
            it.addMarker(
                    MarkerOptions().position(latlng)
                            .title(String.format("Lembrete em %s", placeName))
            ).showInfoWindow()
            it.moveCamera(CameraUpdateFactory.newLatLng(latlng))
            it.setMinZoomPreference(it.minZoomLevel + 17)
        }
    }


}