package com.wehack.cinlocation.service

import android.app.IntentService
import android.content.Intent
import com.wehack.cinlocation.database.ReminderManagerImp

/**
 * Serviço que cadastra e remove lembretes e registra as geofences na data especificada pelo usuário
 */
class AlarmService : IntentService("AlarmService") {
    /**
     * Pequeno hack que re-insere um reminder no banco, isso fará com que a geofence seja registrada
     * uma vez que o alarm referente a mesma acaba de ser disparado e o banco deve
     * sobrescrever a mesma entrada
     */
    override fun onHandleIntent(intent: Intent?) {
        val reminderId = intent?.getLongExtra("reminderId", 0L)!!
        if (reminderId > 0L) {
            val reminder =
            ReminderManagerImp
                    .getInstance(this)
                    ?.findById(reminderId)
            if (reminder != null) {
                ReminderManagerImp
                        .getInstance(this)
                        ?.insert(reminder)
            }
        }
    }
}
