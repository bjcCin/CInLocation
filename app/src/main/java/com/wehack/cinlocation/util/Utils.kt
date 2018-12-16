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
import com.wehack.cinlocation.model.Reminder
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

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

private fun getUniqueId() = ((System.currentTimeMillis() % 10000).toInt())


/**
 * Salva uma imagem na pasta privada do APP
 *
 * @param bitmapImage que da imagem que deseja salvar
 */
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

/**
 * Converte uma string em uma data válida
 *
 * @param text data que será convertida
 */
@SuppressLint("SimpleDateFormat")
fun stringToDate(text: String?): Date?{

    val df = SimpleDateFormat("dd/MM/yyyy")
    df.setLenient(false)

    var date: Date? = null
    if(text != ""){
        try {
            date = df.parse(text)
        } catch (pe:ParseException){
            date = null
        }
    }

    return date
}
/**
 * Lança uma exceção com uma mensagem
 *
 * @param message da exceção
 */
fun fail(message: String = ""): Nothing {
    throw Exception(message)
}

/**
 * Valida se os campos do reminder
 *
 * @param reminder à validar
 */
fun validation(reminder: Reminder): String{

    if(reminder.title == "") return "Titulo não deve está vazio"
    if(reminder.text == "") return "Lembrete vazio"
    if(reminder.endDate == null) return "Data final inválida"
    if(reminder.beginDate == null) return "Data inicial inválida"
    if(reminder.endDate?.compareTo(reminder.beginDate)!! < 0) return "Data final deve ser maior que a inicial"

    return "ok"
}