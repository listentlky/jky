package com.sribs.db.house

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

/**
 * @date 2021/7/29
 * @author elijah
 * @Description
 */
@Entity(tableName = "house_status")
data class HouseStatusBean(
    @ColumnInfo(name="project_id")           var projectId:Long?=null,
    @ColumnInfo(name="unit_id")              var unitId:Long?=null,
    @ColumnInfo(name="config_id")            var configId:Long?=null,
    @ColumnInfo(name="name")                var name:String?=null, //层(室)
    @ColumnInfo(name="house_type")          var houseType:Int?=-1,//0 楼梯间， 1休息平台  2室
    @ColumnInfo(name="status")              var status:Int?=0,//状态 //本地，已上传
    @ColumnInfo(name="finish")              var isFinish:Int?=0,
    @ColumnInfo(name="inspector")           var inspector:String?=null,
    @ColumnInfo(name="house_status")        var houseStatus:String?=null,
    @ColumnInfo(name="house_furnish_time")  var houseFurnishTime:Date?=null,
    @ColumnInfo(name="create_time")         var createTime: Date?=null,
    @ColumnInfo(name="update_time")         var updateTime: Date?= Date(java.util.Date().time),
    @ColumnInfo(name="version")             var version:Int?=1
){
    @ColumnInfo(name="id")
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0
}
