package com.wehack.cinlocation

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.wehack.cinlocation.database.ReminderManagerImp
import com.wehack.cinlocation.model.Reminder
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.wehack.cinlocation", appContext.packageName)
    }

    @Test
    fun databaseConnection() {
        val appContext = InstrumentationRegistry.getTargetContext()
        val remManager= ReminderManagerImp.getInstance(appContext)
        val remText = "This is indeed a test"
        val rem = Reminder(text = remText)
        val mRemId = remManager?.insert(rem)
        if (mRemId != null) {
            val mRem = remManager?.findById(mRemId)
            assert(mRem?.text == remText)
        } else {
            assert(false)
        }
    }
}
