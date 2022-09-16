package com.sribs.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.sql.Date
import java.util.*

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

    var gson = Gson()

    @TypeConverter
    fun stringToObject(data:String):List<String>{
        if (data == null) {
            return Collections.emptyList();
        }
        var listType = object : TypeToken<List<String>>(){}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun objectToString(someObjects:List<String>):String{
        return gson.toJson(someObjects)
    }

}