package com.sribs.db.project

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sribs.common.bean.db.ProjectBean
import com.sribs.common.utils.TimeUtil
import java.sql.Date

/**
 * @date 2021/7/7
 * @author elijah
 * @Description
 */
@Entity(tableName = "project")
data class ProjectBean(
    @ColumnInfo(name="uuid")                var uuid:String?=null, //项目唯一标识 与云端同步
    @ColumnInfo(name="name")                var name:String?=null, //项目名称
    @ColumnInfo(name="leader")              var leader:String?=null,//项目负责人
    @ColumnInfo(name="building_no")          var builderNo:String?=null,//楼号
    @ColumnInfo(name="inspector")           var inspector:String?=null,//检验员
    @ColumnInfo(name="status")              var status:Int?=0,//状态 //本地，已上传
    @ColumnInfo(name="parentVersion")       var parentVersion:Long?=0,
    @ColumnInfo(name="version")              var version:Long?=System.currentTimeMillis(),//版本

    @ColumnInfo(name="create_time")         var createTime:String?=null,
    @ColumnInfo(name="update_time")         var updateTime:String?= TimeUtil.stampToDate("" + System.currentTimeMillis()),
    @ColumnInfo(name="remote_id")           var remoteId:String?=null,
    @ColumnInfo(name = "isChanged") var isChanged: Int? = 0 // 0无变化 1变化
){
    @ColumnInfo(name="id")
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0

    constructor(id:Long):this("","",""){
        this.id = id
    }

}
