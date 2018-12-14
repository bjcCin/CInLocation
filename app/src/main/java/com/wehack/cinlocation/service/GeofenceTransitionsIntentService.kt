package com.wehack.cinlocation.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.wehack.cinlocation.util.sendNotification

class GeofenceTransitionsIntentService : IntentService("GefenceService") {
    override fun onHandleIntent(intent: Intent) {
        Log.d("__LOCATION", "Transition detected")
        val geofencingEnvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEnvent.hasError()) {
            Log.e("__LOCATION", "Erro ao processar transição de área")
            return
        }

        val transition = geofencingEnvent?.geofenceTransition

        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {

        }
        sendNotification(this,"FUNCIONOU")
    }
}