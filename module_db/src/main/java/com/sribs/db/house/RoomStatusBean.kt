package com.sribs.db.house

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

/**
 * @date 2021/7/30
 * @author elijah
 * @Description
 */
@Entity(tableName = "room_status")
data class RoomStatusBean(
    @ColumnInfo(name="project_id")           var projectId:Long?=null,
    @ColumnInfo(name="unit_id")              var unitId:Long?=null,
    @ColumnInfo(name="config_id")            var configId:Long?=null,
    @ColumnInfo(name="name")                var name:String?=null, // 厨房
    @ColumnInfo(name="finish")              var isFinish:Int?=0,

    @ColumnInfo(name="room_status")         var roomStatus:String?=null,
    @ColumnInfo(name="house_furnish_time")  var roomFurnishTime: Date?=null,
    @ColumnInfo(name="room_note")           var roomNote:String?=null,


    @ColumnInfo(name="create_time")         var createTime: Date?=null,
    @ColumnInfo(name="update_time")         var updateTime: Date?= Date(java.util.Date().time),

){
    @ColumnInfo(name="id")
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0

    constructor(id:Long):this(){
        this.id = id
    }
}
