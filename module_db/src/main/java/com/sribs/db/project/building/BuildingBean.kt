package com.sribs.db.project.building

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date
import java.util.*

/**
 * @date 2022/3/15
 * @author leon
 * @Description
 */
@Entity(tableName = "project_building")
data class BuildingBean(
    @ColumnInfo(name="project_id")          var projectId:Long?,//project 表主键
    @ColumnInfo(name="bld_name")            var bldName:String?,//楼名称
    @ColumnInfo(name="bld_type")            var bldType:String?,//楼类别，居住类、非居住类 all
    @ColumnInfo(name="create_time")         var createTime:Long?=0,
    @ColumnInfo(name="update_time")         var updateTime:Long?=0,
    @ColumnInfo(name="delete_time")         var deleteTime:Long?=0,

    @ColumnInfo(name="inspector_name")      var inspectorName:String?="",//用jsonString记录多人检测情况
    @ColumnInfo(name="remote_id")           var remoteId:String?=null,
    @ColumnInfo(name="version")             var version:Int?=1,
    @ColumnInfo(name="status")              var status:Int?=0//1,删除；0，正常，与云端is_deleted相同
) {
    @ColumnInfo(name="id")
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0

    constructor(id:Long, bldName: String, bldType: String, createTime: Long, updateTime: Long, deleteTime: Long, inspectorName: String, remoteId: String, version: Int, status: Int):this(
        0L,"","",null,null,-1,"","",0,0
    ){
        this.id = id
        this.projectId = projectId
        this.bldName = bldName
        this.bldType = bldType
        this.createTime = createTime
        this.updateTime = updateTime
        this.deleteTime = deleteTime
        this.inspectorName = inspectorName
        this.remoteId = remoteId
        this.version = version
        this.status = status
    }

}