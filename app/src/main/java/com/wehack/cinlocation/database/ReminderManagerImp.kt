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

/**
 * Singleton que gerencia o cadastro de reminders
 *
 * @param context da aplicacacao
 * @property geofencingClient instância do cliente de cadastro de geofences
 * @property geofenceIntent PendingIntent disparado quando o usuário entra em uma geofence
 * @property reminders instância do banco de dados
 */
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

    /**
     * Busca um reminder por id
     * @param id do Reminder
     */
    override fun findById(id: Long): Reminder? {
        return reminders?.findById(id)
    }

    /**
     * Busca todos os reminder no banco de dados
     */
    override fun getAll(): List<Reminder>? {
        return reminders?.getAll()
    }

    /**
     * Insere um novo reminder
     *
     * @param reminder a ser inserido
     */
    override fun insert(reminder: Reminder): Long? {
        val remId = reminders?.insert(reminder)
        reminder.id = remId
        val geofence = buildGeofence(reminder)
        registerGeofencing(geofence)
        return remId
    }

    /**
     * Atualiza um Reminder, remove a geofence antiga e insere uma nova.
     * Isso é necessário porque a API não disponibiliza uma forma de atualizar uma geofence já
     * registrada
     *
     * @return numero de linhas atualizadas no banco de dados
     */
    override fun update(reminder: Reminder): Int? {
        val numberAffectedRows = reminders?.update(reminder)
        removeGeofence(reminder.id.toString())
        registerGeofencing(buildGeofence(reminder))
        return numberAffectedRows
    }

    /**
     * Remove um reminder e a geofence associada
     *
     * @param reminder a ser removido
     */
    override fun delete(reminder: Reminder) {
        reminders?.delete(reminder)
        removeGeofence(reminder.id.toString())
    }

    /**
     * Constroi um objeto Geofence a partir dos dados do Reminder
     *
     * @param reminder a partir do qual será criado a geofence
     * @return geofence com os dados do reminder passado
     */
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

    /**
     * Constroi objeto GeofencingRequest
     */
    private fun buildGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder()
                .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
                .addGeofences(listOf(geofence))
                .build()
    }

    /**
     * Registra uma nova geofence
     *
     * @param geofence que se deseja registrar
     */
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

    /**
     * Remove a geofence de um reminder
     *
     * @param reminder cuja geofence será removida
     */
    private fun removeGeofence(reminderId: String) {
        geofencingClient
                .removeGeofences(listOf(reminderId))
                .addOnSuccessListener {
                    Log.d("__LOCATION", "Geofence removida com sucesso")
                }
                .addOnFailureListener {
                    Log.d("__LOCATION", "Erro ao remover geofence")
                }
    }
}