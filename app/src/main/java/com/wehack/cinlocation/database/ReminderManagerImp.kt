package com.wehack.cinlocation.database

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.wehack.cinlocation.database.dao.ReminderDao
import com.wehack.cinlocation.model.Reminder
import com.wehack.cinlocation.service.GeofenceTransitionsIntentService
import java.util.*

class ReminderManagerImp(private val context: Context) : ReminderManager {
    companion object {
        const val GEOFENCE_RADIUS_IN_METERS = 500.toFloat()
        private var INSTANCE: ReminderManager? = null

        fun getInstance(context: Context): ReminderManager? {
            if (INSTANCE == null) {
                synchronized(ReminderManagerImp::class.java) {
                    INSTANCE = ReminderManagerImp(context)
                }
            }
            return INSTANCE
        }
    }

    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)
    private val geofenceIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceTransitionsIntentService::class.java)
        PendingIntent
                .getService(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT)
    }
    private val reminders: ReminderDao? = ReminderDatabase.getInstance(context)?.reminderDao()


    override fun findById(id: Long): Reminder? {
        return reminders?.findById(id)
    }

    override fun getAll(): List<Reminder>? {
        return reminders?.getAll()
    }

    override fun insert(reminder: Reminder): Long? {
        val remId = reminders?.insert(reminder)
        val geofence = buildGeofence(reminder)
        registerGeofencing(geofence)
        return remId
    }

    fun buildGeofence(reminder: Reminder): Geofence {
        val now = Date().time
        val timeToExpire = now - reminder.endDate?.time!!
        return Geofence
            .Builder()
            .setRequestId(reminder.id.toString())
            .setCircularRegion(
                    reminder.lat!!,
                    reminder.lon!!,
                    GEOFENCE_RADIUS_IN_METERS
            )
            .setExpirationDuration(timeToExpire)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()
    }

    override fun update(reminder: Reminder): Reminder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(vararg reminder: Reminder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun buildGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder()
                .setInitialTrigger(0)
                .addGeofences(listOf(geofence))
                .build()
    }

    private fun registerGeofencing(geofence: Geofence) {
        if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geofencingClient
                    .addGeofences(buildGeofencingRequest(geofence), geofenceIntent)
                    .addOnSuccessListener {
                        Log.d("__LOCATION", "Geofence created!")
                    }
                    .addOnFailureListener {
                        Log.d("__LOCATION", "Error while creating geofence")
                    }
        } else {
            Log.d("__LOCATION", "User needs to grant permission!")
        }
    }
}