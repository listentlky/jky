package com.sribs.bdd.bean

import com.sribs.common.bean.db.DrawingV3Bean
import com.sribs.common.bean.db.UnitBean

//楼层
data class BuildingFloorBean(var name:String,var pictureList: ArrayList<BuildingFloorPictureBean>?,var floor:String?,var floorType:Int,var floorIndex:Int) // floorType 0地下 1地上

data class BuildingFloorIndexBean(var name:String,var floor:String,var pictureList: ArrayList<BuildingFloorPictureBean>?)
//楼层配置图片
data class BuildingFloorPictureBean(var name: String,var uri:String?,var url:String?,var chose:Boolean=false)
//每一栋楼
data class BuildingFloor(val projectId:Long?,
                         var bldId: Long?,
                         var unitId:Long?=null,
                         var unitNo:String?=null,
                         var remoteId:String?=null,
                         var version:Int?=null,
                         var localId:Long=-1, var name: String, var inspector: String,
                         var time:String, var floorList:ArrayList<BuildingFloorBean>?,
                         var above:Int, var after:Int, var status: Int){
    var isCardSel:Boolean = false
    var isMenuChecked:Boolean = false
    var isFinish:Boolean = false
    var type = RoomItemBean.TYPE_CONTENT
    var houseType:Int = 0
    var tag = ""
    var spanCount = 1

    /**
     * @Description 分类标签
     */
    constructor(tag:String):this(-1,-1,-1,"","",-1,
        -1,"","","",null,0,0,0){
        type = RoomItemBean.TYPE_GROUP
        this.tag = tag
        spanCount = 3
    }

    companion object{
        fun copyFromUnitBean(unitbean:UnitBean):BuildingFloor{
            return BuildingFloor(unitbean.projectId,unitbean.bldId,unitbean.unitId,unitbean.unitNo,unitbean.remoteId, unitbean.version,
                -1,"",unitbean.inspectors!!,"",null,0,0,0)
        }
    }

}

//每栋楼的模块
data class BuildingModule(
    var projectUUID:String?,
    var projectId: Long?,
    var buildingRemoteId:String?,
    var buildingUUID:String?,
    var buildingId:Long?,
    var moduleUUID:String?,
    var moduleid:Long?,
    var moduleName: String?,
    var drawings: List<DrawingV3Bean>?=ArrayList(),
    var inspectors: String="",
    var leaderId: String?,
    var leaderName: String?,
    var aboveGroundNumber: Int?,
    var underGroundNumber: Int?,
    var isDeleted: Int?,
    var superiorVersion: Long?,
    var parentVersion: Long?,
    var version: Long?,
    var status: Int,
    var createTime: String?,
    var deleteTime: String?,
    var updateTime:String?,
    var remoteId: String?,
    var isChanged: Int?
    ){

}




