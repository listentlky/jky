package com.sribs.bdd.utils

import java.util.*

/**
 * create time: 2022/10/11
 * author: bruce
 * description:
 */
object UUIDUtil {

    fun getUUID(name:String):String{
        return UUID(name.hashCode().toLong(),UUID.randomUUID().hashCode().toLong()).toString()
    }

    fun getUUID():String{
        return UUID.randomUUID().toString()
    }
}