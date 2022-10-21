package com.sribs.db.project.building

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sribs.common.bean.db.DrawingV3Bean
import com.sribs.common.utils.TimeUtil
import java.sql.Date
import java.util.*
import kotlin.collections.ArrayList

/**
 * @date 2022/3/15
 * @author leon
 * @Description
 */
@Entity(tableName = "project_building")
data class BuildingBean(
    @ColumnInfo(name="uuid")                var uuid:String,//楼表唯一ID，与云端同步
    @ColumnInfo(name="project_remoteId")        var projectRemoteId:String,//项目表唯一ID，与云端同步
    @ColumnInfo(name="project_uuid")        var projectUUID:String,//项目表唯一ID，与云端同步
    @ColumnInfo(name="project_id")          var projectId:Long?,//project 表主键
    @ColumnInfo(name="bld_name")            var bldName:String?,//楼名称
    @ColumnInfo(name="bld_type")            var bldType:String?,//楼类别，居住类、非居住类 all
    @ColumnInfo(name="create_time")         var createTime:String?=TimeUtil.stampToDate(""+System.currentTimeMillis()),
    @ColumnInfo(name="update_time")         var updateTime:String?=TimeUtil.stampToDate(""+System.currentTimeMillis()),
    @ColumnInfo(name="delete_time")         var deleteTime:String?="",
    @ColumnInfo(name="isDeleted")         var isDeleted:Int?=0,
    @ColumnInfo(name="leader")              var leader:String?="",//负责人
    @ColumnInfo(name="inspector_name")      var inspectorName:String?="",//用jsonString记录多人检测情况
    @ColumnInfo(name="remote_id")           var remoteId:String?=null,
    @ColumnInfo(name="superiorVersion")       var superiorVersion:Long?=0,
    @ColumnInfo(name="parentVersion")       var parentVersion:Long?=0,
    @ColumnInfo(name="version")             var version:Long?=System.currentTimeMillis(),
    @ColumnInfo(name="status")              var status:Int?=0,//1,删除；0，正常，与云端is_deleted相同
    @ColumnInfo(name="drawing")              var drawing:List<DrawingV3Bean>?=ArrayList(),//基于楼图纸列表
    @ColumnInfo(name="aboveGroundNumber")             var aboveGroundNumber:Int?=0,//楼上层数
    @ColumnInfo(name="underGroundNumber")              var underGroundNumber:Int?=0,//地下层数
    @ColumnInfo(name = "isChanged") var isChanged: Int? = 0
) {
    @ColumnInfo(name="id")
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0

    constructor(uuid:String,projectRemoteId:String,projectUUID:String,id:Long, bldName: String, bldType: String, createTime: String, updateTime: String, deleteTime: String,
                leader: String?,inspectorName: String, remoteId: String,superiorVersion:Long,parentVersion:Long, version: Long, status: Int,drawing: List<DrawingV3Bean>?,isChanged: Int):this(
        "","","",0L,"","","","","",0,"","","",0,0,0,0,
        ArrayList(),0,0
    ){
        this.id = id
        this.uuid = uuid
        this.projectRemoteId = projectRemoteId
        this.projectUUID = projectUUID
        this.projectId = projectId
        this.bldName = bldName
        this.bldType = bldType
        this.createTime = createTime
        this.updateTime = updateTime
        this.deleteTime = deleteTime
        this.isDeleted = isDeleted
        this.leader = leader
        this.inspectorName = inspectorName
        this.remoteId = remoteId
        this.superiorVersion = superiorVersion
        this.parentVersion = parentVersion
        this.version = version
        this.status = status
        this.drawing = drawing
        this.isChanged = isChanged
    }

}