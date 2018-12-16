package com.wehack.cinlocation

import android.os.SystemClock
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.wehack.cinlocation.database.ReminderManagerImp
import com.wehack.cinlocation.model.Reminder
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ReminderManagerTest {
    companion object {
        const val REMINDER_TITLE = "This is indeed a test"
        const val ONE_MINUTE = 60 * 1000
    }

    @Test
    fun databaseInsertion() {
        val appContext = InstrumentationRegistry.getTargetContext()
        val remManager= ReminderManagerImp.getInstance(appContext)
        val rem = buildFakeReminder()
        val mRemId = remManager?.insert(rem)
        if (mRemId != null) {
            val mRem = remManager?.findById(mRemId)
            assert(mRem?.text == REMINDER_TITLE)
        } else {
            assert(false)
        }
    }

    private fun buildFakeReminder(): Reminder {
        return Reminder(
                title = REMINDER_TITLE,
                text = "This is indeed a fake reminder",
                lat = 0.0,
                lon = 0.0,
                beginDate = Date(SystemClock.elapsedRealtime()),
                endDate = Date(SystemClock.elapsedRealtime() + ONE_MINUTE),
                completed= false,
                image = null,
                placeName = "Fake place"
        )
    }
}
