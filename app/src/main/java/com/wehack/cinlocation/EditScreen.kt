package com.wehack.cinlocation

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
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
import android.widget.EditText
import android.widget.ImageView
import com.vicmikhailau.maskededittext.MaskedEditText
import com.wehack.cinlocation.database.ReminderDatabase
import com.wehack.cinlocation.model.Reminder
import kotlinx.android.synthetic.main.edit_screen.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import android.provider.MediaStore.Images.Media.getBitmap
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import java.text.SimpleDateFormat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.OnMapReadyCallback
import java.io.File
import java.io.FileInputStream


class EditScreen() : AppCompatActivity() {

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_screen)

        val id: Long = intent?.extras?.get("itemId") as Long

        toolbar = viewToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editText = view_reminderText
        title = view_reminderTittle
        startDate = view_dateS
        endDate = view_dateE
//
//        if (mMap == null) {
//            mMap = SupportMapFragment.newInstance()
//            mMap?.getMapAsync(OnMapReadyCallback { googleMap ->
//                val latLng = LatLng(1.289545, 103.849972)
//                googleMap.addMarker(MarkerOptions().position(latLng)
//                        .title("Singapore"))
//                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
//            })
//        }

        mMap = viewReminderMap as SupportMapFragment


        editPhoto = viewFloatBtn
        editPhoto?.setOnClickListener(){
            val pickPhotoIntent = Intent(Intent.ACTION_GET_CONTENT)
            pickPhotoIntent.setType("image/*")
            startActivityForResult(pickPhotoIntent,1)
        }

        image = viewImage

        getReminderbyReminderId(id)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val chosenImageUri = data.data

            val mBitmap: Bitmap?
            mBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, chosenImageUri)
            image?.setImageBitmap(mBitmap)

        }
    }

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

    fun mapConfigs(googleMap: GoogleMap, lat: Double?, lon: Double?){
        val latLng = LatLng(lat!!, lon!!)
        googleMap.addMarker(MarkerOptions().position(latLng))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.setMinZoomPreference(googleMap.minZoomLevel + 18)

        googleMap.setOnMapClickListener {
            Toast.makeText(applicationContext, "Map clicado", Toast.LENGTH_SHORT).show()
        }

        googleMap.uiSettings.setAllGesturesEnabled(false)

    }


}