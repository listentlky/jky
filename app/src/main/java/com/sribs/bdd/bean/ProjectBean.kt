package com.sribs.bdd.bean

import java.io.Serializable
import java.sql.Date

/**
* Created by leon on 2022/03/11.
* desc: 项目
*/

data class ProjectBean(
    var projectId: Long?=null,
    var address: String?=null,
    var proLeader: String?=null,//项目负责人
    var buildingList: ArrayList<Building>?,
//    var drawingsList: ArrayList<Drawing>?,//项目图纸
    var engineer: String?,
    var createTime: Long?=0,
    var updateTime: Long?=0,
    var remoteId:String?=null,
    var version:Int?=null,
    var status:Int?=null
    )

    /**
     * 栋
     */
    data class Building(
        var buildingId:Long?,
        var projectId: Long?=null,
        var buildingName:String?,
        var bldType:String?,//建筑类型，inhab,居住类;nonInhab,非居住类建筑
        var bldUnitFloorList: ArrayList<*>?,//居住类建筑，此处是ArrayList<BldUnit>；非居住类建筑，此处是ArrayList<Floor>
//        var damageList: ArrayList<*>?,
        var drawingsList: ArrayList<Drawing>?,//各栋楼图纸
        var createTime: Long?=0,
        var engineer: String?,
        var remoteId:String?=null,
        var version:Int?=null,
        var status:Int?=null
    )
        /**
         * 单元
          */
    data class BldUnit(
        var id: Int?,
        var buildingId:Long?,
        var projectId: Long?=null,
        var unitName: String?,
        var floorList: ArrayList<Floor>?,
        var damageList: ArrayList<*>?,
        var drawingsList: ArrayList<Drawing>?,//单元图纸
        var createTime: Long?,
        var engineer: String?,
        var remoteId:String?=null,
        var version:Int?=null,
        var status:Int?=null
    )
    /**
     * 层
     */
    data class Floor(
        var id:Long?,
        var buildingId:Long?,
        var projectId: Long?,
        var floorId: Long?,
        var floorName: String?
    ){
            var unitId: Long? = -1
            var houseList: ArrayList<House>? = null
            var drawingsList: ArrayList<Drawing>? = null//每层图纸
            var createTime: Long? = 0
            var engineer: String? = ""
            var remoteId:String? = ""
            var version:Int? = -1
            var status:Int? = -1
        }

/**
 * 层
 */
data class Floor2(
    var id:Long?,
    var buildingId:Long?,
    var projectId: Long?,
    var floorId: String?,
    var floorName: String?
){
    var unitId: Long? = -1
    var houseList: ArrayList<House>? = null
    var drawingsList: ArrayList<Drawing>? = null//每层图纸
    var createTime: Long? = 0
    var engineer: String? = ""
    var remoteId:String? = ""
    var version:Int? = -1
    var status:Int? = -1
}

    //楼层
    /**
     * 户
     */
    data class House(
        var id: Int?,
        var projectId: Long?=null,
        var buildingId:Long?,
        var unitId: Int?,
        var floorId: Long?,
        var houseName: String?,
        var roomList: ArrayList<Room>?,
        var damageList: ArrayList<*>?,
        var createTime: Long?,
        var engineer: String?,
        var remoteId:String?=null,
        var version:Int?=null,
        var status:Int?=null
    )
    /**
     * 房间
     */
    data class Room(
        var id: Int?,
        var projectId: Long?=null,
        var buildingId:Long?,
        var unitId: Int?,
        var floorId: Long?,
        var roomId: Int?,
        var roomName: String?,
        var damageList: ArrayList<*>?,
        var roomType: String?,
        var createTime: Long?,
        var engineer: String?,
        var remoteId:String?=null,
        var version:Int?=null,
        var status:Int?=null
    )

    /*
    **图纸
    */
    data class Drawing(
        var id: Int?,
        var projectId: Long?=null,
        var buildingId:Long?,
        var unitId: Int?,
        var floorId: Long?,
        var floorName: String?,
        var fileName: String?,
        var drawingType: String?,//east,west,south,north,overall,floor
        var damageList: ArrayList<DamageBean>?,
        var fileType: String?,//pdf,jpg,png,jpeg
        var sourceUri: String?,//存储的是Uri.toString()
        var cacheAbsPath: String?,//缓存的地址,
        var createTime: Long?,
        var updateTime: Long?,
        var engineer: String?,
        var remoteId:String?=null,
        var version:Int?=null,
        var status:Int?=null
    )
