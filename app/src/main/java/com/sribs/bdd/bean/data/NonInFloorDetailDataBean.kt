package com.sribs.bdd.bean.data

import kotlin.properties.Delegates

/**
 * @date 2022/3/8
 * @author leon
 * @Description 非居住类项目各楼层详情
 */
class NonInFloorDetailDataBean(var idx:Int): com.sribs.common.bean.BaseDataBean() {
    override fun getThis(): com.sribs.common.bean.BaseDataBean = this
    override fun setData(b: com.sribs.common.bean.BaseDataBean) {
        if (b !is NonInFloorDetailDataBean)return
        floorNumber = b.floorNumber
        totalFloor = b.totalFloor
        drawingsList = b.drawingsList
        buildingId = b.buildingId
        projectId = b.projectId
    }

    var floorNumber:String by Delegates.observable(""){
            d,old,new->onDataChange(d,old,new)
    }
    var totalFloor:String by Delegates.observable(""){
            d,old,new->
        onDataChange(d,old,new)
    }

    var drawingsList:List<String>? by Delegates.observable(null){
            d,old,new->onDataChange(d,old,new)
    }

    var unitId:Long=-1
    var buildingId:Long=-1
    var projectId:Long=-1

    override fun copy(): com.sribs.common.bean.BaseDataBean? =
        NonInFloorDetailDataBean(idx).also { it.setData(this) }
}