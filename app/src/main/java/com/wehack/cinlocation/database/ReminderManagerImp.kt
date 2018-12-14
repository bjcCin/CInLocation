package com.wehack.cinlocation.database

import android.content.Context
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.wehack.cinlocation.database.dao.ReminderDao
import com.wehack.cinlocation.model.Reminder
import java.util.*

class ReminderManagerImp(context: Context) : ReminderManager {
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

    val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)
    private val reminders: ReminderDao? = ReminderDatabase.getInstance(context)?.reminderDao()


    override fun findById(id: Long): Reminder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAll(): List<Reminder> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insert(reminder: Reminder): Long {
        val remId = reminders?.insert(reminder)
        val now = Date().time
        val timeToExpire = now - reminder.endDate?.time!!
        val geofence = Geofence
                .Builder()
                .setRequestId(remId?.toString())
                .setCircularRegion(
                        reminder.lat!!,
                        reminder.lon!!,
                        GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(timeToExpire)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        return remId!!
    }

    override fun delete(vararg reminder: Reminder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}