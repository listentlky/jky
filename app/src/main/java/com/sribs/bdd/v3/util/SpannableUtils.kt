package com.sribs.bdd.v3.util

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan

/**
 * create time: 2022/11/14
 * author: bruce
 * description:
 */
object SpannableUtils {

    fun setTextColor(text:String,start:Int,end:Int,color:Int): SpannableStringBuilder {
        var style = SpannableStringBuilder(text)
        style.setSpan(ForegroundColorSpan(color),start,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return style
    }
}