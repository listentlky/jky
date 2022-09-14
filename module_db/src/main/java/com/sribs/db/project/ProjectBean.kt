package com.sribs.db.project

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sribs.common.bean.db.ProjectBean
import java.sql.Date

/**
 * @date 2021/7/7
 * @author elijah
 * @Description
 */
@Entity(tableName = "project")
data class ProjectBean(
    @ColumnInfo(name="name")                var name:String?=null, //项目名称
    @ColumnInfo(name="leader")              var leader:String?=null,//项目负责人
    @ColumnInfo(name="building_no")          var builderNo:String?=null,//楼号
    @ColumnInfo(name="inspector")           var inspector:String?=null,//检验员
    @ColumnInfo(name="status")              var status:Int?=0,//状态 //本地，已上传

    @ColumnInfo(name="create_time")         var createTime:Date?=null,
    @ColumnInfo(name="update_time")         var updateTime:Date?=Date(java.util.Date().time),
    @ColumnInfo(name="remote_id")           var remoteId:String?=null,


){
    @ColumnInfo(name="id")
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0

    constructor(id:Long):this("","",""){
        this.id = id
    }

}
