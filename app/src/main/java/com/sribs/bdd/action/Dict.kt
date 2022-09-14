package com.sribs.bdd.action

import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import io.reactivex.schedulers.Schedulers
import com.sribs.bdd.R
import com.sribs.bdd.bean.UnitConfigType
import com.sribs.common.server.IDatabaseService

object Dict {

    var sLeaderMap = HashMap<String,String>()
    var sInspectorMap = HashMap<String,String>()
    fun init(){
        val srv = ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB).navigation() as IDatabaseService
        srv.getLeader().subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .subscribe({
                it.forEach { b->
                    sLeaderMap[b.id!!] = b.name!!
                }
            },{
                it.printStackTrace()
            })
        srv.getInspector().subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .subscribe ({
                it.forEach { b->
                    sInspectorMap[b.id!!] = b.name!!
                    sLeaderMap[b.id!!] = b.name!!
                }
            },{
                it.printStackTrace()
            })

    }

    fun getLeaderId(name:String):String? = sLeaderMap.filter { it.value == name }
        .keys.first()

    fun getLeaderName(id:String):String? = sLeaderMap[id]

    fun getLeaders():Array<String>? = sLeaderMap.values.toTypedArray()

    fun getInspectorId(name:String):String? = sInspectorMap.filter { it.value == name }
        .keys.first()

    fun getInspectorName(id:String):String? = sInspectorMap[id]

    fun getInspectorNames(id:String):String? {
        return if (id.contains(",")){    // 网络获取名称，分割
            var name = ""
            id.split(",").forEach {
                if (name.isNotEmpty()){   //本地按ui显示“、” 分割
                    name = "$name、"
                }
                name = "${name}${getInspectorName(it)}"
            }
            name
        }else{
            getInspectorName(id)
        }
    }


    fun getInspectors():Array<String>? = sLeaderMap.values.toTypedArray()


    fun isConfigCustom(c: Context,configType:Int, config:String):Boolean{
        var arr = when(configType){
            UnitConfigType.CONFIG_TYPE_FLOOR_BOTTOM.value-> c.resources.getStringArray(R.array.unit_config_floor_bottom)
            UnitConfigType.CONFIG_TYPE_FLOOR_NORMAL.value-> c.resources.getStringArray(R.array.unit_config_floor_bottom)
            UnitConfigType.CONFIG_TYPE_FLOOR_TOP.value-> c.resources.getStringArray(R.array.unit_config_floor_bottom)
            UnitConfigType.CONFIG_TYPE_UNIT_BOTTOM.value-> c.resources.getStringArray(R.array.unit_config_unit_bottom_unit_config)
            UnitConfigType.CONFIG_TYPE_UNIT_NORMAL.value-> c.resources.getStringArray(R.array.unit_config_unit_normal_unit_config)
            UnitConfigType.CONFIG_TYPE_UNIT_TOP.value-> c.resources.getStringArray(R.array.unit_config_unit_normal_unit_config)
            else -> emptyArray()
        }
        return !arr.contains(config)
    }
}