package com.sribs.bdd.v3.util

import android.util.Log

/**
 * create time: 2022/9/6
 * author: bruce
 * description:
 */
object LogUtils {

    var MAX_LENGTH = 3900

    val TAG: String? = "bruce"

    fun d(msg: String) {
        var content = msg
        if(content.length> MAX_LENGTH){
            while (content.length> MAX_LENGTH){
                var splitMsg = content.substring(0, MAX_LENGTH)
                content = content.replace(splitMsg,"")
                Log.d(TAG, splitMsg)
            }
            Log.d(TAG, content)
        }else{
            Log.d(TAG, content)
        }
    }
}