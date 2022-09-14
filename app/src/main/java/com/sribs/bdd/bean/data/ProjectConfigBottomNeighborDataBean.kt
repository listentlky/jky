package com.sribs.bdd.bean.data

import com.sribs.bdd.bean.UnitConfigType
import kotlin.properties.Delegates

/**
 * @date 2021/7/6
 * @author elijah
 * @Description
 */
class ProjectConfigBottomNeighborDataBean:ProjectConfigBaseDataBean() {

    override val type: UnitConfigType=UnitConfigType.CONFIG_TYPE_UNIT_BOTTOM

    override fun getThis(): com.sribs.common.bean.BaseDataBean = this

    var floorNum:String = ""

    var neighborConfig:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    override fun isEmpty(): Boolean {
        errorMsg = "请选择配置"
        return neighborConfig.isNullOrBlank()
    }

    override fun toString(): String {
        return "ProjectConfigBottomNeighborDataBean(neighborConfig='$neighborConfig')"
    }
    override fun setData(b: com.sribs.common.bean.BaseDataBean) {
        b as ProjectConfigBaseDataBean
        neighborConfig = b.getConfig1Val()?:""
        floorNum = b.getFloor()?:""
    }

    override fun getFloor(): String? = floorNum

    override fun getConfig1Val(): String? = neighborConfig

    override fun setFloor(s: String) {
        floorNum = s
    }

    override fun copy(): ProjectConfigBaseDataBean?
            = ProjectConfigBottomNeighborDataBean().also { it.setData(this) }

}