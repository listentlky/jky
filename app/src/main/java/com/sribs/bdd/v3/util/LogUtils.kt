package com.sribs.bdd.v3.util

import android.util.Log

/**
 * create time: 2022/9/6
 * author: bruce
 * description:
 */
object LogUtils {

    val TAG: String? = "bdd"

    fun d(msg: String) {
        Log.d(TAG, msg)
    }
}