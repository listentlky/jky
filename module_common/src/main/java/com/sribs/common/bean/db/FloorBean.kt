package com.sribs.common.bean.db

/**
 * @date 2021/7/8
 * @author elijah
 * @Description
 */
data class FloorBean(
    var id:Long?=null,
    var projectId:Long?=null,
    var bldId:Long?=null,
    var unitId:Long?=null,
    var floorId:Long?=null,
    var floorName:String?=null,

    var createTime:Long?=null,
    var updateTime:Long?=null,
    var deleteTime:Long?=null,
    var inspectorName:String?=null,
    var version:Int?=null,
    var remoteId:String?=null,
    var status:Int?=null,
){
    fun isSame(projectId:Long?,bldId: Long?,unitId: Long?,floorId: Long?):Boolean =
        this.projectId == projectId && this.bldId == bldId && this.unitId == unitId && this.floorId == floorId


}