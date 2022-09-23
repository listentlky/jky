package com.sribs.db.v3.project

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

/**
 * create time: 2022/9/16
 * author:
 * description: 对应 /api/v3/app/building/module/list
 */


@Entity(tableName = "v3_building_module")
data class v3BuildingModuleRoom(
    @ColumnInfo(name = "building_id") var buildingId: String? = "",  //楼id
    @ColumnInfo(name = "project_id") var projectId: Long?, //模块id
    @ColumnInfo(name = "module_name") var moduleName: String? = null, //模块name
    @ColumnInfo(name = "drawings") var drawings: List<String>? = null, //
    @ColumnInfo(name = "inspectors") var inspectors: List<String>? = null, //
    @ColumnInfo(name = "leader_id") var leaderId: String? = null, //
    @ColumnInfo(name = "leader_name") var leaderName: String? = null, //
    @ColumnInfo(name = "aboveground_number") var aboveGroundNumber: Int? = 0,  //
    @ColumnInfo(name = "underground_number") var underGroundNumber: Int? = 0,  //
    @ColumnInfo(name = "is_deleted") var isDeleted: Int? = 0,  //
    @ColumnInfo(name = "status") var status: Int? = 0,  //


    @ColumnInfo(name = "create_time") var createTime: String? = null,
    @ColumnInfo(name = "delete_time") var deleteTime: String? = null,
    @ColumnInfo(name = "update_time") var updateTime: String? = null,
    @ColumnInfo(name = "remote_id") var remoteId: String? = null, //远端ID
) {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

}