package com.wehack.cinlocation.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import com.wehack.cinlocation.BuildConfig
import com.wehack.cinlocation.MainActivity
import com.wehack.cinlocation.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private val NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel"

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

private fun getUniqueId() = ((System.currentTimeMillis() % 10000).toInt())


@SuppressLint("SimpleDateFormat")
fun saveToInternalStorage(bitmapImage: Bitmap?, context: Context?):String {
    val cw = ContextWrapper(context)
    // path to /data/data/yourapp/app_data/imageDir
    val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
    // Create imageDir
    val date: String = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Date())
    val imageName = date.replace("-","")
            .replace(":","")
            .replace(" ","")

    val mypath = File(directory, imageName)
    var fos: FileOutputStream? = null
    try
    {
        fos = FileOutputStream(mypath)
        // Use the compress method on the BitMap object to write image to the OutputStream
        bitmapImage?.compress(Bitmap.CompressFormat.PNG, 100, fos)
    }
    catch (e:Exception) {
        e.printStackTrace()
    }
    finally
    {
        try
        {
            fos?.close()
        }
        catch (e: IOException) {
            e.printStackTrace()
        }
    }

    return mypath.absolutePath
}

fun stringToDate(text: String?): Date{

    val df = SimpleDateFormat("dd/MM/yyyy")
    df.setLenient(false)
    val date: Date = df.parse(text)

    return date
}