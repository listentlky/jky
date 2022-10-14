package com.sribs.db.v3.project

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sribs.common.bean.db.DrawingV3Bean
import java.sql.Date
import java.util.*

@Entity(tableName = "v3_module_floor")
data class v3ModuleFloorRoom(
    @ColumnInfo(name = "project_id") var projectId: Long?,//project 表主键
    @ColumnInfo(name = "building_id") var bldId: Long?,//building 表主键
    @ColumnInfo(name = "module_id") var moduleId: Long?,//module 表主键
    @ColumnInfo(name = "floor_id") var floorId: Long?,//本楼楼层id
    @ColumnInfo(name = "floor_name") var floorName: String?,//楼层名称
    @ColumnInfo(name = "floor_type") var floorType: Int,//0 地下 1地上
    @ColumnInfo(name = "drawingsList") var drawingsList:List<DrawingV3Bean>? = null,///
    @ColumnInfo(name = "above_number") var aboveNumber:Int? = 0,///
    @ColumnInfo(name = "after_number") var afterNumber:Int? = 0,///
    @ColumnInfo(name = "create_time") var createTime: String?,
    @ColumnInfo(name = "update_time") var updateTime: String?,
    @ColumnInfo(name = "delete_time") var deleteTime: String?,//如果inspectorId==10000,代表多人检测，对应inspectorName应该是JSON String，记录各个id,name
    @ColumnInfo(name = "remote_id") var remoteId: String? = null,
    @ColumnInfo(name = "version") var version: Int? = 1,
    @ColumnInfo(name = "status") var status: Int? = 0,//1,删除；0，正常，与云端is_deleted相同
    @ColumnInfo(name = "isChanged") var isChanged: Boolean? = false
) {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0


}