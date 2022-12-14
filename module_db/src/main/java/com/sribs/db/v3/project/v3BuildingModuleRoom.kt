package com.sribs.db.v3.project

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sribs.common.bean.db.DrawingV3Bean
import java.sql.Date



@Entity(tableName = "v3_building_module")
data class v3BuildingModuleRoom(
    @ColumnInfo(name = "uuid") var uuid: String?,  //模块唯一ID 与云端同步
    @ColumnInfo(name = "building_remoteId") var buildingRemoteId: String?,  //楼唯一ID 与云端同步
    @ColumnInfo(name = "building_uuid") var buildingUUID: String?,  //楼唯一ID 与云端同步
    @ColumnInfo(name = "building_id") var buildingId: Long?,  //楼id
    @ColumnInfo(name = "project_uuid") var projectUUID: String?, //项目唯一ID 与云端同步
    @ColumnInfo(name = "project_id") var projectId: Long?, //项目id
    @ColumnInfo(name = "module_name") var moduleName: String? = null, //模块name
    @ColumnInfo(name = "drawings") var drawings: List<DrawingV3Bean>? = null, //
    @ColumnInfo(name = "inspectors") var inspectors: String? = null, //
    @ColumnInfo(name = "leader_id") var leaderId: String? = null, //
    @ColumnInfo(name = "leader_name") var leaderName: String? = null, //
    @ColumnInfo(name = "aboveground_number") var aboveGroundNumber: Int? = 0,  //
    @ColumnInfo(name = "underground_number") var underGroundNumber: Int? = 0,  //
    @ColumnInfo(name = "isDeleted") var isDeleted: Int? = 0,  //
    @ColumnInfo(name = "superiorVersion")       var superiorVersion:Long?=0,
    @ColumnInfo(name = "parentVersion") var parentVersion: Long? = 1,
    @ColumnInfo(name = "version") var version: Long? = 1,
    @ColumnInfo(name = "status") var status: Int? = 0,  //


    @ColumnInfo(name = "create_time") var createTime: String? = null,
    @ColumnInfo(name = "delete_time") var deleteTime: String? = null,
    @ColumnInfo(name = "update_time") var updateTime: String? = null,
    @ColumnInfo(name = "remote_id") var remoteId: String? = null, //远端ID
    @ColumnInfo(name = "isChanged") var isChanged: Int? = 0,
) {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

}