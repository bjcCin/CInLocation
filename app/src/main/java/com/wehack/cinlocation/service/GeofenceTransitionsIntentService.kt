package com.wehack.cinlocation.service

import android.app.IntentService
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.wehack.cinlocation.R
import com.wehack.cinlocation.database.ReminderManagerImp
import com.wehack.cinlocation.util.fail
import com.wehack.cinlocation.util.sendNotification

/**
 * Serviço que gerencia o a entrada em geofences e dispara notificações para o usuário
 *
 * @property getFirstTriggeredGeofence retorna a primeira geofence que disparou já que a API
 *           retorna uma lista de geofences
 */
class GeofenceTransitionsIntentService : IntentService("GefenceService") {
    override fun onHandleIntent(intent: Intent) {
        val geofencingEnvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEnvent.hasError()) {
            fail("Erro ao receber dados da localização")
            return
        }

        val geofence = getFirstTriggeredGeofence(geofencingEnvent)
        val reminder = ReminderManagerImp
                .getInstance(this)
                ?.findById(geofence.requestId.toLong())

        sendNotification(
                this,
                getString(R.string.titulo_notificacao),
                reminder?.title ?: "Toque para mais detalhes")
    }

    private fun getFirstTriggeredGeofence(event: GeofencingEvent): Geofence =
        event.triggeringGeofences.first()
}