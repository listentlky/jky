package com.sribs.db.project.unit.config

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

/**
 * @date 2021/7/7
 * @author elijah
 * @Description
 *
 */
@Entity(tableName = "project_unit_config")
data class ConfigBean(
    @ColumnInfo(name="project_id")        var projectId:Long?,//project 表主键
    @ColumnInfo(name="bld_id")            var bldId:Long?,//building 表主键
    @ColumnInfo(name="unit_id")           var unitId:Long?,
    @ColumnInfo(name="floor_idx")         var floorIdx:Int?,//层号
    @ColumnInfo(name="neighbor_idx")      var neighborIdx:Int?=null,
    @ColumnInfo(name="config_type")       var configType:Int?=null,// @link UnitConfigType
    @ColumnInfo(name="floor_num")         var floorNum:String?=null,
    @ColumnInfo(name="neighbor_num")      var neighborNum:String?=null,
    @ColumnInfo(name="corridor_num")      var corridorNum:String?=null,
    @ColumnInfo(name="corridor_config")   var corridorConfig:String?=null,
    @ColumnInfo(name="platform_num")      var platformNum:String?=null,
    @ColumnInfo(name="platform_config")   var platformConfig:String?=null,
    @ColumnInfo(name="config1")           var config1:String?=null,
    @ColumnInfo(name="config2")           var config2:String?=null,
    @ColumnInfo(name="unit_type")         var unitType:Int?=0,//户型
    @ColumnInfo(name="house_status")      var houseStatus:Int?=0, //0:本地，1：已上传 2：云端 3：下载配置 4：下载全部
    @ColumnInfo(name="create_time")       var createTime:Date?=null,
    @ColumnInfo(name="update_time")       var updateTime:Date?=Date(java.util.Date().time)
) {
    @ColumnInfo(name="id")
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0

    constructor(id:Long):this(
        0L,
        0L,
        0L,
        0,
        0,
        0,
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        0,
        0,
        null,
        null
    ){
        this.id = id
    }
}