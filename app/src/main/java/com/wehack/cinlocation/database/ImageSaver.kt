package com.wehack.cinlocation.database

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.support.annotation.NonNull
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class ImageSaver(context: Context?) {
    private var directoryName = "images"
    private var fileName = "image.png"
    private var context: Context
    private var dir: File? = null
    private var external = false
    init{
        this.context = context!!
    }
    fun setFileName(fileName:String):ImageSaver {
        this.fileName = fileName
        return this
    }
    fun setExternal(external:Boolean):ImageSaver {
        this.external = external
        return this
    }
    fun setDirectory(directoryName:String):ImageSaver {
        this.directoryName = directoryName
        return this
    }
    fun save(bitmapImage: Bitmap) {
        var fileOutputStream: FileOutputStream? = null
        try
        {
            fileOutputStream = FileOutputStream(createFile())
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        }
        catch (e:Exception) {
            e.printStackTrace()
        }
        finally
        {
            try
            {
                if (fileOutputStream != null)
                {
                    fileOutputStream.close()
                }
            }
            catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    @NonNull
    private fun createFile():File {
        val directory:File
        if (external)
        {
            directory = getAlbumStorageDir(directoryName)
            if (!directory.exists())
            {
                directory.mkdir()
            }
        }
        else
        {
            directory = File("${context.getFilesDir()}/${directoryName}")
            if (!directory.exists())
            {
                directory.mkdir()
            }
        }
        return File(directory, fileName)
    }
    private fun getAlbumStorageDir(albumName:String):File {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName)
        if (!file.mkdirs())
        {
            Log.e("ImageSaver", "Directory not created")
        }
        return file
    }
    fun load():Bitmap? {
        var inputStream: FileInputStream? = null
        try
        {
            inputStream = FileInputStream(createFile())
            return BitmapFactory.decodeStream(inputStream)
        }
        catch (e:Exception) {
            e.printStackTrace()
        }
        finally
        {
            try
            {
                if (inputStream != null)
                {
                    inputStream.close()
                }
            }
            catch (e:IOException) {
                e.printStackTrace()
            }
        }
        return null
    }
    fun deleteFile():Boolean {
        val file = createFile()
        return file.delete()
    }
    companion object {
        val isExternalStorageWritable:Boolean
            get() {
                val state = Environment.getExternalStorageState()
                return Environment.MEDIA_MOUNTED.equals(state)
            }
        val isExternalStorageReadable:Boolean
            get() {
                val state = Environment.getExternalStorageState()
                return (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
            }
    }
}

