package com.wehack.cinlocation.database

import com.wehack.cinlocation.model.Reminder

interface ReminderManager {
    fun findById(id: Long): Reminder

    fun getAll(): List<Reminder>

    fun insert(reminder: Reminder): Long

    fun delete(vararg reminder: Reminder)
}