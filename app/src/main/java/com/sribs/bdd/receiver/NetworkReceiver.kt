package com.sribs.bdd.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Context.CONNECTIVITY_SERVICE

import android.net.ConnectivityManager
import com.cbj.sdk.libbase.utils.LOG
import com.sribs.bdd.Config


/**
 * @date 2021/8/4
 * @author elijah
 * @Description
 */
@Deprecated("register network callback in APP")
class NetworkReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        LOG.E("123","onReceive")
        val connectMgr = context?.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetwork  = connectMgr?.getNetworkCapabilities(connectMgr?.activeNetwork)
        if (activeNetwork==null){
            Config.isNetAvailable = false
            LOG.E("123","isConnect = false")
            return
        }
        Config.isNetAvailable = true
        LOG.I("123","conn change isConnect=  true")
    }
}