package com.sribs.common.bean.v3

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
    var floorId:String?=null,
    var floorName:String?=null,
    var floorType:Int,
    var floorIndex:Int,
    var drawingsList: List<DrawingV3Bean>? = null,
    var aboveNumber:Int?=0,
    var afterNumber:Int?=0,
    var createTime:String?=null,
    var updateTime:String?=null,
    var deleteTime:String?=null,
    var version:Int?=null,
    var remoteId:String?=null,
    var status:Int?=0,
){
    fun isSame(projectId:Long?,bldId: Long?,moduleId: Long?,floorId: String?):Boolean =
        this.projectId == projectId && this.bldId == bldId && this.moduleId == moduleId && this.floorId == floorId


}