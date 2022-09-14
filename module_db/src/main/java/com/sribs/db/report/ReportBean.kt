package com.sribs.db.report

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

/**
 * @date 2021/8/3
 * @author elijah
 * @Description
 */
@Entity(tableName = "house_report")
data class ReportBean(
    @ColumnInfo(name="project_id")           var projectId:Long?=null,
    @ColumnInfo(name="unit_id")              var unitId:Long?=null,
    @ColumnInfo(name="config_id")            var configId:Long?=null,
    @ColumnInfo(name="report")               var report:String?=null,
    @ColumnInfo(name="is_save")               var isSave:Int?=0,
    @ColumnInfo(name="sign_path")             var signPath:String?=null,
    @ColumnInfo(name="sign_res_id")            var signResId:String?=null,
    @ColumnInfo(name="sign_res_rl")           var signResUrl:String?=null,

    @ColumnInfo(name="create_time")         var createTime: Date?=null,
    @ColumnInfo(name="update_time")         var updateTime: Date?= Date(java.util.Date().time),
){
    @ColumnInfo(name="id")
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0
}
