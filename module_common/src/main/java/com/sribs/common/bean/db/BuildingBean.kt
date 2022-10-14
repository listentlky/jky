package com.sribs.common.bean.db


/**
 * @date 2021/7/8
 * @author elijah
 * @Description
 */
data class BuildingBean(
    var id:Long?=null,
    var UUID:String,
    var projectRemoteId:String,
    var projectUUID:String,
    var projectId:Long?=null,
    var bldName:String?=null,
    var bldType:String?=null,
    var createTime:Long?=null,
    var updateTime:Long?=null,
    var deleteTime:Long?=null,
    var isDeleted:Int?=0,
    var leader:String?=null,
    var inspectorName:String?="",
    var superiorVersion:Long?=0,
    var parentVersion:Long?=0,
    var version:Long?=System.currentTimeMillis(),
    var remoteId:String?=null,
    var status:Int?=null,
    var drawing:List<DrawingV3Bean>?= ArrayList(),
    var aboveGroundNumber:Int?=0, //楼上层数
    var underGroundNumber:Int?=0, //地下层数
    var isChanged: Boolean? = false

){
    fun isSame(id: Long?,projectId:Long?):Boolean =
        this.id == id && this.projectId == projectId


}