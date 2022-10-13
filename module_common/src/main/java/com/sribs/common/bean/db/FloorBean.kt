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
    var floorType:Int?=0, // 0地下 1地上

    var createTime:Long?=null,
    var updateTime:Long?=null,
    var deleteTime:Long?=null,
    var inspectorName:String?=null,
    var version:Int?=null,
    var remoteId:String?=null,
    var status:Int?=null,
    var drawing:List<DrawingV3Bean>?=null,
    var aboveGroundNumber:Int?=0, //楼上层数
    var underGroundNumber:Int?=0, //地下层数


){
    fun isSame(projectId:Long?,bldId: Long?,unitId: Long?,floorId: Long?):Boolean =
        this.projectId == projectId && this.bldId == bldId && this.unitId == unitId && this.floorId == floorId


}