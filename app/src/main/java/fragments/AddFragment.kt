package fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.wehack.cinlocation.MainActivity.Companion.locationPermissionGranted
import com.wehack.cinlocation.R
import kotlinx.android.synthetic.main.fragment_add.*

class AddFragment: Fragment(), OnMapReadyCallback {
    var mMap: GoogleMap? = null
    var toolbar: Toolbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        toolbar = addToolbar
        //setSupportActionBar(toolbar)

        //val mMapFrag = newReminderMap as SupportMapFragment?
       // mMapFrag?.getMapAsync(this)
        return inflater.inflate(R.layout.fragment_add,container, false)!!
    }

    override fun onMapReady(map: GoogleMap?) {
        updateLocationUI()
    }

    fun updateLocationUI() {
        try {
            if (locationPermissionGranted) {
                mMap?.isMyLocationEnabled = true
                mMap?.uiSettings?.isMyLocationButtonEnabled = true
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }
    }
}