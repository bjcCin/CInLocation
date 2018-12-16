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
 * Testes.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ReminderManagerTest {
    companion object {
        const val REMINDER_TITLE = "This is indeed a test"
        const val NEW_REMINDER_TITLE = "This is an updated title"
        const val FIVE_MINUTES = 5 * 60 * 1000
    }

    @Test
    fun testDBInsertion() {
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

    @Test
    fun testDBDeletion() {
        val appContext = InstrumentationRegistry.getTargetContext()
        val remMgr = ReminderManagerImp.getInstance(appContext)
        val rem = buildFakeReminder()
        val id = remMgr?.insert(rem)
        rem.id = id
        remMgr?.delete(rem)
        val newRem = remMgr?.findById(id!!)
        assert(newRem == null)
    }

    @Test
    fun testDBUpdate() {
        val appContext = InstrumentationRegistry.getTargetContext()
        val remMgr = ReminderManagerImp.getInstance(appContext)
        val rem = buildFakeReminder()
        remMgr?.insert(rem)
        rem.title = NEW_REMINDER_TITLE
        remMgr?.update(rem)
        val newRem = remMgr?.findById(rem.id!!)
        if (newRem != null) {
            assert(newRem?.title == NEW_REMINDER_TITLE)
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
                endDate = Date(SystemClock.elapsedRealtime() + FIVE_MINUTES),
                completed= false,
                image = null,
                placeName = "Fake place"
        )
    }
}
