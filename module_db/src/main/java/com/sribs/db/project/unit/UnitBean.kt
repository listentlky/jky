package com.sribs.db.project.unit

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date
import java.util.*

/**
 * @date 2021/7/7
 * @author elijah
 * @Description
 */
@Entity(tableName = "project_unit")
data class UnitBean(
    @ColumnInfo(name="project_id")        var projectId:Long?,//project 表主键
    @ColumnInfo(name="bld_id")           var bldId:Long?,
    @ColumnInfo(name="unit_no")           var unitNo:String?,
    @ColumnInfo(name="floor_size")         var floorSize:Int?,//
    @ColumnInfo(name="neighbor_size")      var neighborSize:Int?,//
    @ColumnInfo(name="floor_type")        var floor_type:Int?=0,
    @ColumnInfo(name="create_time")         var createTime:Date?=null,
    @ColumnInfo(name="update_time")         var updateTime:Date?=Date(java.util.Date().time),

    @ColumnInfo(name="remote_id")           var remoteId:String?=null,
    @ColumnInfo(name="version")             var version:Int?=1,
    @ColumnInfo(name="status")              var status:Int?=0,

    @ColumnInfo(name="leader_id")           var leaderId:String?=null,
    @ColumnInfo(name="leader_name")         var leaderName:String?=null,
    @ColumnInfo(name="inspectors")           var inspectors:String?=null

) {
    @ColumnInfo(name="id")
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0

    constructor(id:Long):this(
        0L,0,"",0,0,0
    ){
        this.id = id
    }

}