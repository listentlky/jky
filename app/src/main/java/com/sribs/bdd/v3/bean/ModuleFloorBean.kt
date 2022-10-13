package com.sribs.bdd.bean.data

import com.facebook.stetho.inspector.protocol.module.Inspector
import com.sribs.bdd.bean.Drawing
import com.sribs.bdd.bean.RoomItemBean
import com.sribs.common.bean.db.DrawingV3Bean
import com.sribs.common.bean.db.UnitBean
import java.util.*
import kotlin.collections.ArrayList

//楼层
data class ModuleFloorBean(var name: String, var pictureList: ArrayList<ModuleFloorPictureBean>?,var floor:String?)

data class ModuleFloorIndexBean(
    var name: String,
    var floor: String,
    var pictureList: ArrayList<ModuleFloorPictureBean>?
)

//楼层配置图片
data class ModuleFloorPictureBean(
    var name: String,
    var uri: String?,
    var url: String?,
    var chose: Boolean = false
)

//每一栋楼
data class ModuleFloor(
    var id:Long?,
    val projectId: Long?,
    var bldId: Long?,
    var moduleId: Long?,
    var remoteId: String? = null,
    var floorId:Long?,
    var floorName:String?=null,
    var floorList: ArrayList<DrawingV3Bean>?=null,
    var aboveNumber:Int?=0,
    var afterNumber:Int?=0,
    var version: Int? = 0,
    var status: Int=0,
    var createTime: String?=null,

    )

//每栋楼的模块
data class BuildingFloorItem(
    var moduleid: Long?,
    var buildingId: String?,
    var name: String?,
    var updateTime: String?
) {

}




