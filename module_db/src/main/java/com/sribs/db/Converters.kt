package com.sribs.db

import androidx.room.TypeConverter
import java.sql.Date

/**
 * @date 2021/7/7
 * @author elijah
 * @Description
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value:Long?): Date?{
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateTomTimestamp(date:Date?):Long?{
        return date?.time?.toLong()
    }

}