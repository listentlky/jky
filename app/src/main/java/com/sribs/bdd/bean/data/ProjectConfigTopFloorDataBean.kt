package com.sribs.bdd.bean.data

import com.sribs.bdd.bean.UnitConfigType
import kotlin.properties.Delegates

/**
 * @date 2021/7/6
 * @author elijah
 * @Description
 */
class ProjectConfigTopFloorDataBean:ProjectConfigBaseDataBean() {
    override fun getThis(): com.sribs.common.bean.BaseDataBean = this
    override fun toString(): String {
        return "ProjectConfigTopFloorDataBean(type=$type, floorNum='$floorNum', corridorNum='$corridorNum', corridorConfig='$corridorConfig')"
    }

    override val type: UnitConfigType = UnitConfigType.CONFIG_TYPE_FLOOR_TOP

    var floorNum:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    var corridorNum:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    var corridorConfig:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }


    override fun isEmpty(): Boolean {
        errorMsg = "请输入层号"
        if (floorNum.isNullOrEmpty())return true
        if (corridorConfig.isNullOrEmpty()){
            errorMsg = "请配置楼梯间"
            return true
        }
        return false
    }
    override fun setData(b: com.sribs.common.bean.BaseDataBean) {
        b as ProjectConfigBaseDataBean
        floorNum = b.getFloor()?:""
        corridorNum = b.getCorridorNumVal()?:""
        corridorConfig = b.getCorridorConfigVal()?:""

    }

    override fun getFloor(): String? = floorNum


    override fun getCorridorNumVal(): String? = corridorNum


    override fun getCorridorConfigVal(): String? = corridorConfig


    override fun setFloor(s: String) {
        this.floorNum = s
    }

    override fun setCorridorNumVal(s: String) {
        this.corridorNum = s
    }

    override fun copy(): ProjectConfigBaseDataBean? =
        ProjectConfigTopFloorDataBean().also { it.setData(this) }

}