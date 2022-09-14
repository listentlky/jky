package com.sribs.db.project.damage

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
@Entity(tableName = "damage_detail")
data class DamageBean(
    @ColumnInfo(name="drawing_id")          var drawingId:Long?,//drawing 表主键
    @ColumnInfo(name="damage_type_name")    var dTypeName:String?,//损伤分类名称，如结构构建损伤、耐久性损伤、渗漏损伤等
    @ColumnInfo(name="annot_ref")           var annotRef:Long?,//pdf标记句柄
    @ColumnInfo(name="axis")                var axis:String?,//轴线
    @ColumnInfo(name="dm_desc")             var dmDesc:String?,//损伤详情

    @ColumnInfo(name="photo_path")          var pohtoPath:String?,//照片路径
    @ColumnInfo(name="damage_detail_type")  var dDetailType:String?,//普通损伤：common；裂缝：crack
    @ColumnInfo(name="leak_length")         var leakLength:String?,//裂缝长度
    @ColumnInfo(name="leak_width")          var leakWidth:String?,//裂缝宽度
    @ColumnInfo(name="monitor_id")          var mntId:String?,//监测点编号

    @ColumnInfo(name="monitor_way")         var mntWay:String?,//监测方法
    @ColumnInfo(name="monitor_length")      var monitorLength:String?,//监测点刻痕长度
    @ColumnInfo(name="monitor_width")       var monitorWidth:String?,//监测点刻痕宽度
    @ColumnInfo(name="monitor_photo_path")  var monitorPhotoPath:String?,//监测点照片路径
    @ColumnInfo(name="create_time")         var createTime:Long?=0,

    @ColumnInfo(name="update_time")         var updateTime:Long?=0,
    @ColumnInfo(name="delete_time")         var deleteTime:Long?=0,
    @ColumnInfo(name="inspector_name")      var inspectorName:String?="",
    @ColumnInfo(name="remote_id")           var remoteId:String?=null,
    @ColumnInfo(name="version")             var version:Int?=1,

    @ColumnInfo(name="status")              var status:Int?=0//1,删除；0，正常，与云端is_deleted相同
) {
    @ColumnInfo(name="id")
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0

    constructor(id:Long,
                drawingId: Long,
                dTypeName:String,
                annotRef: Long?,
                axis:String?,
                dmDesc:String?,
                pohtoPath:String?,
                dDetailType:String?,
                leakLength:String?,
                leakWidth:String?,
                mntId:String?,
                mntWay:String?,
                monitorLength:String?,
                mntWidth:String?,
                mntPhotoPath:String?,
                createTime: Long?,
                updateTime: Long?,
                deleteTime: Long?,
                inspectorName: String,
                remoteId: String?,
                version: Int?,
                status: Int?):this(
        0L,
        "",
        -1L,
        "",
        "",
        "",
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
        0,
        "",
        "",
        0,
        0
    ){
        this.id = id
        this.drawingId = drawingId
        this.dTypeName = dTypeName
        this.annotRef = annotRef
        this.axis = axis
        this.dmDesc = dmDesc
        this.pohtoPath = pohtoPath
        this.dDetailType = dDetailType
        this.leakLength = leakLength
        this.leakWidth = leakWidth
        this.mntId = mntId
        this.mntWay = mntWay
        this.monitorLength = monitorLength
        this.monitorWidth = mntWidth
        this.monitorPhotoPath = mntPhotoPath

        this.createTime = createTime
        this.updateTime = updateTime
        this.deleteTime = deleteTime//如果inspectorId==10000,代表多人检测，对应inspectorName应该是JSON String，记录各个id,name
        this.inspectorName = inspectorName
        this.remoteId = remoteId
        this.version = version
        this.status = status//0,删除；1，正常
    }
}