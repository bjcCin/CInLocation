package fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import com.wehack.cinlocation.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Utils {

//    private fun addFragment(fragment: Fragment) {
//        FragmentTransaction.tr
//        fragment.supportFragmentManager
//                .beginTransaction()
//                .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
//                .replace(R.id.fragment_container, fragment, fragment.javaClass.getSimpleName())
//                .addToBackStack(fragment.javaClass.getSimpleName())
//                .commit()
//    }

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
}
