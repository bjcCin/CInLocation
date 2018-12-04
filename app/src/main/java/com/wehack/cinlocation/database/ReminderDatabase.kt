package com.wehack.cinlocation.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.wehack.cinlocation.database.dao.Converters
import com.wehack.cinlocation.database.dao.ReminderDao
import com.wehack.cinlocation.model.Reminder

@Database(entities = [Reminder::class], version = 3)
@TypeConverters(Converters::class)
abstract class ReminderDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao

    companion object {
        private var INSTANCE: ReminderDatabase? = null

        fun getInstance(context: Context): ReminderDatabase? {
            if (INSTANCE == null) {
                synchronized(ReminderDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            ReminderDatabase::class.java, "reminders.db")
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return INSTANCE
        }
    }
}