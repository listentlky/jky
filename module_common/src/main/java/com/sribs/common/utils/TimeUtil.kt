package com.sribs.common.utils

import java.sql.Date
import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * @date 2021/8/5
 * @author elijah
 * @Description
 */
object TimeUtil {
    val YMD = SimpleDateFormat("yyyy.MM.dd")

    val YMD_HMS = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    val YM = SimpleDateFormat("yyyy-MM")

    fun isBefore(t1:String,t2:String):Boolean{
        return try {
            val d1 = YMD_HMS.parse(t1)
            val d2 = YMD_HMS.parse(t2)
            d1.before(d2)
        }catch (e:Exception){
            e.printStackTrace()
            false
        }
    }

    fun time2YMD(t:String):String =
        try{
            val d1 = YMD_HMS.parse(t)
            YMD.format(d1)
        } catch (e:ParseException){
            e.printStackTrace()
            ""
        } catch (e:Exception){
            e.printStackTrace()
            ""
        }


    fun time2Date(t:String):Date? =
        try{
            if (t.length == "yyyy-MM-dd HH:mm:ss".length){
                Date(YMD_HMS.parse(t).time)
            }else if(t.length == "yyyy-MM".length){
                Date(YM.parse(t).time)
            }else{
                null
            }
        }catch (e:Exception){
            null
        }

    fun date2Time(d:Date?):String? =
        if (d==null) null else
            try{
                YMD_HMS.format(d)
            } catch (e:Exception){
                null
            }

    fun date2YMD(d:Date?):String? =
        if (d==null) null else
            try {
                YMD.format(d)
            }catch (e:Exception){
                null
            }
}