package com.sribs.bdd

import android.content.Context
import android.content.pm.PackageManager
import com.cbj.sdk.libnet.http.HttpManager
import com.cbj.sdk.libnet.http.helper.HeaderInterceptor

/**
 * @date 2021/6/21
 * @author elijah
 * @Description
 */
object Config {
    var URL:String?=null
    var host:String?=null
    var VERSION:String = "0.0.0"
    var BUILD_LEVEL  = BuildConfig.build_level

    fun init(c: Context){
        val appInfo = c.packageManager.getApplicationInfo(c.packageName, PackageManager.GET_META_DATA)

        when(BUILD_LEVEL){
            0->{//开发
           //     URL ="http://116.62.171.217:8123/mock/228/"
            }
            1->{//测试
//                URL = "http://bdd.mercs.xyz:8897"
//                host = "http://bdd.mercs.xyz:8897"
//                URL = "http://192.168.0.102:8190"
//                host = "http://192.168.0.102:8190"
                URL = "http://106.15.205.38:80"
                host = "http://106.15.205.38:80"
//                URL ="http://139.224.253.98:8190"
//                URL = "http://192.168.0.100:8190"
//                host = "http://192.168.0.100:8190"
            }
            2->{//生产
//                URL ="http://building.mercs.xyz:8081"
//                URL ="http://106.15.205.38:80"
//                URL ="http://139.224.253.98:8190"
                URL = "http://106.15.205.38:80"
                host = "http://106.15.205.38:80"
            }
        }
        VERSION = try {
            c.packageManager.getPackageInfo(c.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "0.0.0"
        }
        HeaderInterceptor.API_VERSION = VERSION
        HttpManager.instance.mHost = host?:""

    }

    var isNetAvailable = false

}