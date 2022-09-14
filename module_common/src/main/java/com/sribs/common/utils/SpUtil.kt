package com.sribs.common.utils

import android.content.Context

/**
 * @date 2021/9/14
 * @author elijah
 * @Description
 */
object SpUtil {

    const val SP = "user_sp"

    var mContext:Context?=null

    fun getDefaultContext():Context = mContext!!

    fun saveUser(c: Context, account:String, pwd:String){
        val sp = c.getSharedPreferences(SP,Context.MODE_PRIVATE)
        val edit = sp.edit()
        edit.putString("account",account)
        edit.putString("pwd",pwd)
        edit.commit()
    }

    fun getUser(c:Context):Pair<String?,String?>{
        val sp = c.getSharedPreferences(SP,Context.MODE_PRIVATE)
        val account = sp.getString("account","")
        val pwd = sp.getString("pwd","")
        return Pair(account,pwd)
    }


}