package com.sribs.bdd.bean.data

import com.sribs.bdd.bean.UnitConfigType
import kotlin.properties.Delegates

/**
 * @date 2021/7/6
 * @author elijah
 * @Description
 */
class ProjectConfigTopNeighborDataBean:ProjectConfigBaseDataBean() {

    override val type: UnitConfigType=UnitConfigType.CONFIG_TYPE_UNIT_TOP

    override fun getThis(): com.sribs.common.bean.BaseDataBean = this

    var floorNum = ""

    var neighborType:String by Delegates.observable("非复式") {
            d, old, new -> onDataChange(d,old,new)
    }


    var neighborConfig:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    var neighborConfig2:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    override fun isEmpty(): Boolean {
        errorMsg = "请选择配置"
        return when (neighborType) {
            "非复式" -> {
                neighborConfig.isNullOrBlank()
            }
            "复式" -> {
                neighborConfig.isNullOrBlank() || neighborConfig2.isNullOrBlank()
            }
            else -> {
                errorMsg = "请选择户型"
                false
            }
        }
    }

    override fun toString(): String {
        return "ProjectConfigTopNeighborDataBean(type=$type, neighborType='$neighborType', neighborConfig='$neighborConfig', neighborConfig2='$neighborConfig2')"
    }
    override fun setData(b: com.sribs.common.bean.BaseDataBean) {
        b as ProjectConfigBaseDataBean
        neighborType = b.getNeighborTypeVal()?:""
        neighborConfig = b.getConfig1Val()?:""
        neighborConfig2 = b.getConfig2Val()?:""
        floorNum = b.getFloor()?:""
    }

    override fun getFloor(): String? = floorNum

    override fun getConfig1Val(): String? = neighborConfig

    override fun getConfig2Val(): String? = neighborConfig2

    override fun getNeighborTypeVal(): String? = neighborType

    override fun setFloor(s: String) {
        floorNum = s
    }

    override fun copy(): ProjectConfigBaseDataBean? =
        ProjectConfigTopNeighborDataBean().also { it.setData(this) }
}