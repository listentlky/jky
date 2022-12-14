package com.sribs.db.project.drawing

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
@Entity(tableName = "project_drawing")
data class DrawingBean(
    @ColumnInfo(name="project_id")          var projectId:Long?,//project 表主键
    @ColumnInfo(name="bld_id")              var bldId:Long?,//building 表主键
    @ColumnInfo(name="unit_id")             var unitId:Long?,//unit 表主键
    @ColumnInfo(name="floor_id")            var floorId:Long?,//楼层id
    @ColumnInfo(name="floor_name")          var floorName:String?,//楼层名称
    @ColumnInfo(name="file_name")           var fileName: String?,
    @ColumnInfo(name="drawing_type")        var drawingType: String?,//east,west,south,north,overall,floor
    @ColumnInfo(name="file_type")           var fileType: String?,//pdf,jpg,png,jpeg
    @ColumnInfo(name="local_abs_path")      var localAbsPath: String?,//移动端缓存地址
    @ColumnInfo(name="remote_abs_path")     var remoteAbsPath: String?,//服务端缓存地址,
    @ColumnInfo(name="create_time")         var createTime:Long?=0,
    @ColumnInfo(name="update_time")         var updateTime:Long?=0,
    @ColumnInfo(name="delete_time")         var deleteTime:Long?=0,//如果inspectorId==10000,代表多人检测，对应inspectorName应该是JSON String，记录各个id,name
    @ColumnInfo(name="inspector_name")      var inspectorName:String?="",
    @ColumnInfo(name="remote_id")           var remoteId:String?=null,
    @ColumnInfo(name="version")             var version:Int?=1,
    @ColumnInfo(name="status")              var status:Int?=0//1,删除；0，正常，与云端is_deleted相同
) {
    @ColumnInfo(name="id")
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0

    constructor(id:Long, projectId: Long, bldId: Long, unitId: Long, floorId: Long, floorName: String?, fileName: String, drawingType: String, fileType: String, localAbsPath: String, remoteAbsPath: String, createTime: Long, updateTime: Long, deleteTime:Long, inspectorName: String, remoteId: String, version: Int, status: Int):this(
        0L,0,0,0,"","","","","","",null,null,0,"","",0,0
    ){
        this.id = id
        this.projectId = projectId
        this.bldId = bldId
        this.unitId = unitId
        this.floorId = floorId
        this.floorName = floorName
        this.fileName = fileName
        this.drawingType = drawingType
        this.fileType = fileType
        this.localAbsPath = localAbsPath
        this.remoteAbsPath = remoteAbsPath
        this.createTime = createTime
        this.updateTime = updateTime
        this.deleteTime = deleteTime
        this.inspectorName = inspectorName
        this.remoteId = remoteId
        this.version = version
        this.status = status
    }

}