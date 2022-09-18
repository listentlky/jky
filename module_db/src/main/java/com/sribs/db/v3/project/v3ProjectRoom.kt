package com.sribs.db.v3.project

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

/**
 * create time: 2022/9/16
 * author: bruce
 * description:
 */
@Entity(tableName = "v3project")
data class v3ProjectRoom(
    @ColumnInfo(name = "projectName") var projectName: String? = "",
    @ColumnInfo(name = "inspectors") var inspectors: List<String>? = null,
    @ColumnInfo(name = "leaderId") var leaderId: String? = "",
    @ColumnInfo(name = "leaderName") var leaderName: String? = "",
    @ColumnInfo(name = "create_time") var createTime: Date? = null,
    @ColumnInfo(name = "updateTime") var updateTime: Date? = Date(java.util.Date().time),
    @ColumnInfo(name = "status") var status: Int? = 0,//状态 //本地，已上传
    @ColumnInfo(name = "projectId")  var projectId:String?=null, //远端ID
) {
    @ColumnInfo(name="id")
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0

    constructor(id:Long):this("",ArrayList<String>(),"",""){
        this.id = id
    }
}