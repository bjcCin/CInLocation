package com.wehack.cinlocation.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.wehack.cinlocation.database.ReminderManagerImp
import com.wehack.cinlocation.util.sendNotification

class GeofenceTransitionsIntentService : IntentService("GefenceService") {
    override fun onHandleIntent(intent: Intent) {
        val geofencingEnvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEnvent.hasError()) {
            Log.e("__LOCATION", "Erro ao processar transição de área")
            return
        }

        val geofence = getFirstTriggeredGeofence(geofencingEnvent)
        val reminder = ReminderManagerImp
                .getInstance(this)
                ?.findById(geofence.requestId.toLong())

//        sendNotification(
//                this,
//                reminder?.title ?: "Novo Lembrete",
//                reminder?.text ?: "Toque para mais detalhes")
        sendNotification(
                this,
                "Você tem um lembrete para este local",
                reminder?.title ?: "Toque para mais detalhes")
    }

    private fun getFirstTriggeredGeofence(event: GeofencingEvent): Geofence =
        event.triggeringGeofences.first()
}