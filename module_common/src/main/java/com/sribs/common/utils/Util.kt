package com.sribs.common.utils

import org.apache.commons.lang3.math.NumberUtils
import java.security.MessageDigest
import kotlin.math.min

/**
 * @date 2021/8/19
 * @author elijah
 * @Description
 */
object Util {
    fun md5(str: String): String {

        val digest = MessageDigest.getInstance("MD5")
        val result = digest.digest(str.toByteArray())
        //没转16进制之前是16位
        println("result${result.size}")
        //转成16进制后是32字节
        return toHex(result)
    }
    fun toHex(byteArray: ByteArray): String {
        val result = with(StringBuilder()) {
            byteArray.forEach {
                val hex = it.toInt() and (0xFF)
                val hexStr = Integer.toHexString(hex)
                if (hexStr.length == 1) {
                    this.append("0").append(hexStr)
                } else {
                    this.append(hexStr)
                }
            }
            this.toString()
        }
        //转成16进制后是32字节
        return result
    }
    fun sha1(str:String): String {
        val digest = MessageDigest.getInstance("SHA-1")
        val result = digest.digest(str.toByteArray())
        return toHex(result)
    }

    fun sha256(str:String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val result = digest.digest(str.toByteArray())
        return toHex(result)
    }

    fun formatZeroNum(num:String?):String? =
        if (num?.length == 1 && NumberUtils.isNumber(num)) "0$num" else num

    fun formatNoZeroNum(num:String?):String? =
        if(num?.startsWith("0")==true) num.substring(1) else num


    /**
     * @Description 返回第一个不同的位置
     */
    fun stringCompare(old:String,new:String):Int{
        var oldL = old.length
        var newL = new.length
        var offset = newL-oldL
        offset = if(offset<0) offset+1 else offset
        for (i in 0 until min(old.length,new.length)){
            if (old[i]!=new[i]){
                return i+offset
            }
        }
        return new.length
    }

}