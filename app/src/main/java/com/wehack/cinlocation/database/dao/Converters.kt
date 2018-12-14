package com.wehack.cinlocation.database.dao

import android.arch.persistence.room.TypeConverter
import android.net.Uri
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromStringToUri(value: String?): Uri? {
        return Uri.parse(value)
    }

    @TypeConverter
    fun UriToString(value: Uri?): String? {
        return value.toString()
    }
}