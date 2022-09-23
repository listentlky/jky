package com.sribs.common.bean.db


/**
 * @date 2021/7/8
 * @author elijah
 * @Description
 */
data class BuildingBean(
    var id:Long?=null,
    var projectId:Long?=null,
    var bldName:String?=null,
    var bldType:String?=null,
    var createTime:Long?=null,
    var updateTime:Long?=null,
    var deleteTime:Long?=null,
    var leader:String?=null,
    var inspectorName:String?=null,
    var version:Int?=null,
    var remoteId:String?=null,
    var status:Int?=null,
    var drawing:List<DrawingV3Bean>?=ArrayList()

){
    fun isSame(id: Long?,projectId:Long?):Boolean =
        this.id == id && this.projectId == projectId


}