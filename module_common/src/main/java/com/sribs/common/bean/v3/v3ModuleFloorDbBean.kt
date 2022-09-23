package com.sribs.common.bean.v3

import com.sribs.common.bean.db.DamageBean
import com.sribs.common.bean.db.DrawingV3Bean

/**
 * @date 2021/7/8
 * @author elijah
 * @Description
 */
data class v3ModuleFloorDbBean(
    var id:Long?=null,
    var projectId:Long?=null,
    var bldId:Long?=null,
    var moduleId:Long?=null,
    var floorId:Long?=null,
    var floorName:String?=null,
    var drawingsList: List<DrawingV3Bean>? = null,
    var aboveNumber:Int?=0,
    var afterNumber:Int?=0,
    var createTime:Long?=null,
    var updateTime:Long?=null,
    var deleteTime:Long?=null,
    var version:Int?=null,
    var remoteId:String?=null,
    var status:Int?=0,
){
    fun isSame(projectId:Long?,bldId: Long?,moduleId: Long?,floorId: Long?):Boolean =
        this.projectId == projectId && this.bldId == bldId && this.moduleId == moduleId && this.floorId == floorId


}