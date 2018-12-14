package com.wehack.cinlocation.database.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.wehack.cinlocation.model.Reminder

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminder WHERE id = :id")
    fun findById(id: Long): Reminder

    @Query("SELECT * FROM reminder")
    fun getAll(): List<Reminder>

    @Insert(onConflict = REPLACE)
    fun insert(reminder: Reminder): Long

    @Update
    fun update(reminder: Reminder): Int

    @Delete
    fun delete(vararg reminder: Reminder)
}