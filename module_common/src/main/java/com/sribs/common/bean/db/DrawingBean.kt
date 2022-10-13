package com.sribs.common.bean.db

/**
 * @date 2021/7/8
 * @author elijah
 * @Description
 */
data class DrawingBean(
    var id:Long?=-1,
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

class DrawingV3Bean():BaseDbBean(){

    override fun getThis(): BaseDbBean = this

    var drawingID:Long=-1
    var fileName: String?=""
    var fileType: String?=""//pdf,jpg,png,jpeg
    var drawingType: String?=""//east,west,south,north,overall,floor
    var localAbsPath: String?=""//移动端缓存地址
    var remoteAbsPath: String?=""//服务端缓存地址,
    var damage:ArrayList<DamageV3Bean>?=ArrayList() //损伤

    constructor(drawingID:Long,fileName: String?,fileType: String?,drawingType: String?,localAbsPath: String?,
                remoteAbsPath: String?,damage:ArrayList<DamageV3Bean>):this(){
        this.drawingID = drawingID
        this.fileName = fileName
        this.fileType = fileType
        this.drawingType = drawingType
        this.localAbsPath = localAbsPath
        this.remoteAbsPath = remoteAbsPath
        this.damage = damage
    }


    var direction:String?="" //方向
    var rotate:Int?=0//角度
    constructor(drawingID:Long,fileName: String?,fileType: String?,drawingType: String?,localAbsPath: String?,
                remoteAbsPath: String?,damage:ArrayList<DamageV3Bean>,direction:String?,rotate:Int?):this(){
        this.drawingID = drawingID
        this.fileName = fileName
        this.fileType = fileType
        this.drawingType = drawingType
        this.localAbsPath = localAbsPath
        this.remoteAbsPath = remoteAbsPath
        this.damage = damage
        this.direction = direction
        this.rotate = rotate
    }

    var floorName:String?=""
    override fun toString(): String {
        return "DrawingV3Bean(drawingID=$drawingID, fileName=$fileName, fileType=$fileType, drawingType=$drawingType, localAbsPath=$localAbsPath, remoteAbsPath=$remoteAbsPath, damage=$damage, direction=$direction, rotate=$rotate, floorName=$floorName)"
    }


}

