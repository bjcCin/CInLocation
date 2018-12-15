package com.wehack.cinlocation.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import com.wehack.cinlocation.BuildConfig
import com.wehack.cinlocation.MainActivity
import com.wehack.cinlocation.R

private val NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel"

/**
 * Dispara uma notificação
 *
 * @param context da aplicação
 * @param message titulo da notificação
 * @param content texto da notificação
 */
fun sendNotification(context: Context, message: String, content: String="") {
    val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
        val name = context.getString(R.string.app_name)
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                name,
                NotificationManager.IMPORTANCE_DEFAULT)

        notificationManager.createNotificationChannel(channel)
    }

    val intent = MainActivity.newIntent(context.applicationContext)

    val stackBuilder = TaskStackBuilder.create(context)
            .addParentStack(MainActivity::class.java)
            .addNextIntent(intent)
    val notificationPendingIntent = stackBuilder
            .getPendingIntent(getUniqueId(), PendingIntent.FLAG_UPDATE_CURRENT)

    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(message)
            .setContentIntent(notificationPendingIntent)
            .setContentText(content)
            .setAutoCancel(true)
            .build()

    notificationManager.notify(getUniqueId(), notification)
}

/**
 * Lança uma exceção com uma mensagem
 *
 * @param message da exceção
 */
fun fail(message: String = ""): Nothing {
    throw Exception(message)
}


private fun getUniqueId() = ((System.currentTimeMillis() % 10000).toInt())