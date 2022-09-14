package com.sribs.bdd.bean.data

import com.sribs.bdd.bean.UnitConfigType
import kotlin.properties.Delegates

/**
 * @date 2021/7/6
 * @author elijah
 * @Description
 */
class ProjectConfigNormalFloorDataBean:ProjectConfigBaseDataBean() {
    override fun getThis(): com.sribs.common.bean.BaseDataBean = this
    override fun toString(): String {
        return "ProjectConfigNormalFloorDataBean(type=$type, floorNum='$floorNum', corridorNum='$corridorNum', corridorConfig='$corridorConfig', platformNum='$platformNum', platformConfig='$platformConfig')"
    }

    override val type: UnitConfigType = UnitConfigType.CONFIG_TYPE_FLOOR_NORMAL

    var floorNum:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    var corridorNum:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    var corridorConfig:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    var platformNum:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    var platformConfig:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    override fun isEmpty(): Boolean {
        errorMsg = "请输入层号"
        if (floorNum.isNullOrEmpty())return true
        if (corridorConfig.isNullOrEmpty() && platformConfig.isNullOrEmpty()){
            errorMsg = "请配置楼梯间或休息平台"
            return true
        }
        return false
    }

    override fun setData(b: com.sribs.common.bean.BaseDataBean) {
        b as ProjectConfigBaseDataBean
        floorNum = b.getFloor()?:""
        corridorNum = b.getCorridorNumVal()?:""
        corridorConfig = b.getCorridorConfigVal()?:""
        platformNum = b.getPlatformNumVal()?:""
        platformConfig = b.getPlatformConfigVal()?:""
    }

    override fun getFloor(): String? = floorNum


    override fun getCorridorNumVal(): String? = corridorNum


    override fun getCorridorConfigVal(): String? = corridorConfig


    override fun getPlatformNumVal(): String? =platformNum


    override fun getPlatformConfigVal(): String? =platformConfig
    override fun setFloor(s: String) {
        this.floorNum = s
    }
    override fun setPlatformNumVal(s: String) {
        this.platformNum = s
    }

    override fun setCorridorNumVal(s: String) {
        this.corridorNum = s
    }

    override fun copy(): ProjectConfigBaseDataBean?
    = ProjectConfigNormalFloorDataBean().also { it.setData(this) }
}