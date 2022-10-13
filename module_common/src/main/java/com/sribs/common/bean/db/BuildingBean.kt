package com.sribs.common.bean.db


/**
 * @date 2021/7/8
 * @author elijah
 * @Description
 */
data class BuildingBean(
    var id:Long?=null,
    var UUID:String,
    var projectUUID:String,
    var projectId:Long?=null,
    var bldName:String?=null,
    var bldType:String?=null,
    var createTime:Long?=null,
    var updateTime:Long?=null,
    var deleteTime:Long?=null,
    var leader:String?=null,
    var inspectorName:String?="",
    var parentVersion:Int?=null,
    var version:Int?=null,
    var remoteId:String?=null,
    var status:Int?=null,
    var drawing:List<DrawingV3Bean>?= ArrayList(),
    var aboveGroundNumber:Int?=0, //楼上层数
    var underGroundNumber:Int?=0, //地下层数

){
    fun isSame(id: Long?,projectId:Long?):Boolean =
        this.id == id && this.projectId == projectId


}