package com.sribs.bdd.bean.data

import kotlin.properties.Delegates

/**
 * @date 2021/7/1
 * @author elijah
 * @Description 项目 单元详情
 */
class ProjectUnitDetailDataBean(var idx:Int): com.sribs.common.bean.BaseDataBean() {
    override fun getThis(): com.sribs.common.bean.BaseDataBean = this
    override fun setData(b: com.sribs.common.bean.BaseDataBean) {
        if (b !is ProjectUnitDetailDataBean)return
        unitNumber = b.unitNumber
        totalFloor = b.totalFloor
        totalNeighbor = b.totalNeighbor
        floorType = b.floorType
        unitId = b.unitId
        projectId = b.projectId
    }

    var unitNumber:String by Delegates.observable(""){
            d,old,new->onDataChange(d,old,new)
    }
    var totalFloor:String by Delegates.observable(""){
            d,old,new->
        onDataChange(d,old,new)
    }
    var totalNeighbor:String by Delegates.observable(""){
            d,old,new->onDataChange(d,old,new)
    }

    var floorType:String by Delegates.observable("单楼梯间"){
            d,old,new->onDataChange(d,old,new)
    }

    var unitId:Long=-1
    var projectId:Long=-1

    override fun copy(): com.sribs.common.bean.BaseDataBean? =
        ProjectUnitDetailDataBean(idx).also { it.setData(this) }
}