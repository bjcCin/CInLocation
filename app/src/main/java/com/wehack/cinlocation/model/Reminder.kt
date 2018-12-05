package com.wehack.cinlocation.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity
data class Reminder(
        @PrimaryKey(autoGenerate = true) var id: Long? = null,
        var title: String = "",
        var text: String = "",
        var lat: Double? = 0.0,
        var lon: Double? = 0.0 ,
        var beginDate: Date? = null,
        var endDate: Date? = null,
        var completed: Boolean = false)