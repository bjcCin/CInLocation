package com.wehack.cinlocation

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import com.vicmikhailau.maskededittext.MaskedEditText
import com.wehack.cinlocation.database.ReminderDatabase
import com.wehack.cinlocation.model.Reminder
import kotlinx.android.synthetic.main.edit_screen.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import android.provider.MediaStore.Images.Media.getBitmap
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.app.FragmentActivity
import android.view.MenuItem
import android.widget.*
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import java.text.SimpleDateFormat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.OnMapReadyCallback
import fragments.AddFragment
import fragments.HomeFragment
import fragments.Utils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class EditScreen() : AppCompatActivity() {

    companion object {
        const val PLACE_PICKER_REQUEST = 1
        const val CHANGE_REMINDER_IMAGE = 2
    }

    var item: Item? = null

    var toolbar: Toolbar? = null
    var image: ImageView? = null
    var editPhoto: FloatingActionButton? = null
    var reminderSelected: Reminder? = null
    var editText: EditText? = null
    var title: AppCompatEditText? = null
    var endDate: MaskedEditText? = null
    var startDate: MaskedEditText? = null
    var mMap: SupportMapFragment? = null
    var btnDelete: ImageButton? = null
    var btnSalvar: Button? = null

    var latitude: Double? = null
    var longitude: Double? = null

    var imageURI: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_screen)

        val id: Long = intent?.extras?.get("itemId") as Long

        toolbar = viewToolbar
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setTitle(null);
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editText = view_reminderText
        title = view_reminderTittle
        startDate = view_dateS
        endDate = view_dateE

        mMap = viewReminderMap as SupportMapFragment


        btnDelete = btnView_delete
        btnDelete?.setOnClickListener {
            deleteReminder(id)
        }


        editPhoto = viewFloatBtn
        editPhoto?.setOnClickListener(){
            val pickPhotoIntent = Intent(Intent.ACTION_GET_CONTENT)
            pickPhotoIntent.setType("image/*")
            startActivityForResult(pickPhotoIntent,CHANGE_REMINDER_IMAGE)
        }

        btnSalvar = btnView_salvar
        btnSalvar?.setOnClickListener{
            updateReminder(id)
        }


        image = viewImage

        getReminderbyReminderId(id)
    }

    /**
     * Result of change reminder image and change location
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CHANGE_REMINDER_IMAGE) {

            val bitMapURI = data.data
            val mBitmap: Bitmap?
            mBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, bitMapURI)
            image?.setImageBitmap(mBitmap)

            doAsync {
                val path = Utils().saveToInternalStorage(mBitmap, applicationContext)
                uiThread {
                    imageURI = path
                }
            }

        }

        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK )
                updateMapLocation(data)

    }

    /**
     * Loading Reminder by reminder Id and set screen data
     */
    @SuppressLint("SimpleDateFormat")
    fun getReminderbyReminderId(id: Long){
        doAsync {
            val dao = ReminderDatabase.getInstance(applicationContext)?.reminderDao()
            reminderSelected = dao?.findById(id)

            uiThread {

                editText?.setText(reminderSelected?.text)
                title?.setText(reminderSelected?.title)
                toolbar?.setTitle(reminderSelected?.title)
                val df = SimpleDateFormat("dd/MM/yyyy")
                startDate?.setText(df.format(reminderSelected?.beginDate))
                endDate?.setText(df.format(reminderSelected?.endDate))
                latitude = reminderSelected?.lat
                longitude = reminderSelected?.lon

                if (reminderSelected?.image != null){
                    val f = File(reminderSelected?.image)
                    val b = BitmapFactory.decodeStream(FileInputStream(f))
                    image?.setImageBitmap(b)
                } else {
                    image?.setImageResource(R.drawable.sem_foto)
                }

                mMap?.getMapAsync(OnMapReadyCallback { googleMap ->
                    mapConfigs(googleMap, reminderSelected?.lat, reminderSelected?.lon)
                })

            }
        }
    }

    /**
     * Config map by Reminder Lat/Long
     */
    fun mapConfigs(googleMap: GoogleMap, lat: Double?, lon: Double?){
        val latLng = LatLng(lat!!, lon!!)
        googleMap.addMarker(MarkerOptions().position(latLng))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.setMinZoomPreference(googleMap.minZoomLevel + 18)

        googleMap.setOnMapClickListener {
            val pickerIntent = PlacePicker.IntentBuilder()
            startActivityForResult(pickerIntent.build(this), PLACE_PICKER_REQUEST)
        }

        googleMap.uiSettings.setAllGesturesEnabled(false)
    }


    /**
     * Behaviour of press back button on toolbar
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.getItemId()

        if (id == android.R.id.home) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     * Delete reminder and back to home screen
     */
    fun deleteReminder(id: Long){
        doAsync {
            val dao = ReminderDatabase.getInstance(applicationContext)?.reminderDao()
            val reminder: Reminder? = dao?.findById(id)
            if (reminder != null) {
                dao.delete(reminder)
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }

        }
    }


    /**
     * From result edit map location
     */
    private fun updateMapLocation(data: Intent?) {
        val place = PlacePicker.getPlace(this, data)

        val latlng = place.latLng
        latitude  = place.latLng.latitude
        longitude = place.latLng.longitude
        val name = place.name
        mMap?.getMapAsync {
            it.addMarker(
                    MarkerOptions().position(latlng)
                            .title(String.format("Lembrete em %s", name))
            ).showInfoWindow()
            it.moveCamera(CameraUpdateFactory.newLatLng(latlng))
            it.setMinZoomPreference(it.minZoomLevel + 17)
        }
    }


    private fun updateReminder(id: Long){

        doAsync {
            val dao = ReminderDatabase.getInstance(applicationContext)?.reminderDao()
            val rem = dao?.findById(id)

            rem?.lat = latitude
            rem?.lon = longitude
            rem?.title = title?.text.toString()
            rem?.text = editText?.text.toString()

            if (imageURI == null)
                rem?.image = reminderSelected?.image
            else
                rem?.image = imageURI

            rem?.beginDate = Utils().stringToDate(startDate?.text.toString())
            rem?.endDate = Utils().stringToDate(endDate?.text.toString())

            if(rem != null){
                dao.update(rem)
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }

            uiThread {
                Toast.makeText(applicationContext, "Lembrete salvo com sucesso!", Toast.LENGTH_SHORT).show()
            }

        }

    }


}