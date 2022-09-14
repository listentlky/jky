package com.sribs.common.bean.db

import java.sql.Date

/**
 * @date 2021/7/8
 * @author elijah
 * @Description
 */
data class DrawingBean(
    var id:Long?=null,
    var projectId:Long?,//project 表主键
    var bldId:Long?,//building 表主键
    var unitId:Long?,//unit 表主键
    var floorId:Long?,//楼层id
    var floorName:String?,//楼层名称
    var fileName: String?,
    var drawingType: String?,//east,west,south,north,overall,floor
    var fileType: String?,//pdf,jpg,png,jpeg
    var localAbsPath: String?,//移动端缓存地址
    var remoteAbsPath: String?,//服务端缓存地址,
    var createTime:Long?=0,
    var updateTime:Long?=0,
    var deleteTime:Long?=0,
    var inspectorName:String?="",
    var remoteId:String?=null,
    var version:Int?=1,
    var status:Int?=0
){
    fun isSame(projectId:Long?,bldId: Long?,unitId: Long?,floorId: Long?,id:Long?):Boolean =
        this.projectId == projectId && this.bldId == bldId && this.unitId == unitId && this.floorId == floorId&& this.id == id


}