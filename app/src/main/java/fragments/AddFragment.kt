package fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.provider.MediaStore
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
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.vicmikhailau.maskededittext.MaskedEditText
import com.wehack.cinlocation.R
import com.wehack.cinlocation.database.ReminderDatabase
import com.wehack.cinlocation.model.Reminder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.wehack.cinlocation.MainActivity
import com.wehack.cinlocation.R.id.nav_home
import com.wehack.cinlocation.database.ImageSaver
import org.jetbrains.anko.imageURI
import java.io.*


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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        val addbutton = view.findViewById(R.id.addCadastro) as Button
        editImageButton = view.findViewById(R.id.viewFloatBtn) as FloatingActionButton?
        val reminderText = view.findViewById(R.id.add_reminderText) as EditText
        val reminderTitle = view.findViewById(R.id.add_reminderTittle) as AppCompatEditText
        val toolbar = view.findViewById(R.id.addToolbar) as Toolbar
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
                val path = saveToInternalStorage(mBitmap)
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

    @SuppressLint("SimpleDateFormat")
    private fun saveToInternalStorage(bitmapImage:Bitmap?):String {
        val cw = ContextWrapper(context)
        // path to /data/data/yourapp/app_data/imageDir
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        // Create imageDir
        val date: String = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Date())
        val imageName = date.replace("-","")
                            .replace(":","")
                            .replace(" ","")

        val mypath = File(directory, imageName)
        var fos: FileOutputStream? = null
        try
        {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage?.compress(Bitmap.CompressFormat.PNG, 100, fos)
        }
        catch (e:Exception) {
            e.printStackTrace()
        }
        finally
        {
            try
            {
                fos?.close()
            }
            catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return mypath.absolutePath
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

        googleMap.uiSettings.setAllGesturesEnabled(false)

    }

    @SuppressLint("SimpleDateFormat")
    fun addReminder(title: String, text: String, textEndDate: String?, textStartDate: String?, imageUri: String?) {

        val df = SimpleDateFormat("dd/MM/yyyy")
        df.setLenient(false)
        Log.d("datas", "final ${textEndDate} inicial ${textStartDate}")
        val endDate: Date = df.parse(textEndDate)
        val startDate: Date = df.parse(textStartDate)

        val rem = Reminder(title = title, text = text, endDate = endDate, beginDate = startDate, image = imageUri, lat = latitude , lon = longitude)
        Log.e("printData", "${endDate} and ${startDate}")

        val dao = ReminderDatabase.getInstance(context!!)?.reminderDao()

        doAsync {
            dao?.insert(rem)
            uiThread {
            }

        }
    }


    private fun updateMapLocation(data: Intent?) {
        val place = PlacePicker.getPlace(this.context, data)
        val latlng = place.latLng
        latitude  = place.latLng.latitude
        longitude = place.latLng.longitude
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