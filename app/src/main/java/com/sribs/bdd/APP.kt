package com.sribs.bdd

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.TextUtils
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.baidu.mapapi.SDKInitializer
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libnet.http.HttpManager
import com.cbj.sdk.libnet.http.helper.HeaderInterceptor
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.pgyer.pgyersdk.PgyerSDKManager
import com.radaee.pdf.Document
import com.sribs.bdd.receiver.NetworkReceiver
import com.sribs.common.utils.SpUtil


/**
 * @date 2021/6/21
 * @author elijah
 * @Description
 */
class APP : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
        initPagerSdk(this)
    }

    override fun onCreate() {
        super.onCreate()
        com.sribs.bdd.Config.init(this)
        HttpManager
            .instance
            .addInterceptor(HeaderInterceptor())
            .initHttpClient(applicationContext, com.sribs.bdd.Config.URL!!, false)
        if (BuildConfig.DEBUG) {
            ARouter.openDebug()
            ARouter.openLog()
        }
        ARouter.init(this)
        SDKInitializer.initialize(this)
        initActivityManager()
//        TakePhotoUtil.useDefault()
//        registerBroadcast()
        initNetworkCallback()
        SpUtil.mContext = this.applicationContext

    }

    private fun initPagerSdk(application: Application){
        PgyerSDKManager
            .Init()
            .setContext(application)
            .start();
    }



    private fun initActivityManager(){
        registerActivityLifecycleCallbacks(object :ActivityLifecycleCallbacks{
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }

        })
    }
    private fun registerBroadcast(){
        var filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        applicationContext.registerReceiver(
            NetworkReceiver(),filter
        )
    }

    private fun initNetworkCallback(){
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        val currentNetwork = connectivityManager.activeNetwork
        connectivityManager.registerDefaultNetworkCallback(object :ConnectivityManager.NetworkCallback(){
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                LOG.E("123","onAvailable  network=$network")
                com.sribs.bdd.Config.isNetAvailable = true
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                LOG.E("123","onLost network=$network")
                com.sribs.bdd.Config.isNetAvailable = false
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
            }

            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                super.onLinkPropertiesChanged(network, linkProperties)
            }
        })
    }
}