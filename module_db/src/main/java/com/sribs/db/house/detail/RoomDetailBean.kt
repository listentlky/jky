package com.sribs.db.house.detail

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

/**
 * @date 2021/7/30
 * @author elijah
 * @Description
 */
@Entity(tableName = "room_detail")
data class RoomDetailBean(
    @ColumnInfo(name="project_id")           var projectId:Long?=null,
    @ColumnInfo(name="unit_id")              var unitId:Long?=null,
    @ColumnInfo(name="config_id")            var configId:Long?=null,
    @ColumnInfo(name="name")                 var name:String?=null, //厨房
    @ColumnInfo(name="damage_path")          var damagePath:String?=null,//顶板-接缝
    @ColumnInfo(name="damage_idx")           var damageIdx:Int?=null,//裂缝：0-35  接缝：0：6  其他：0：8
    @ColumnInfo(name="split_num")            var splitNum:String?=null,
    @ColumnInfo(name="split_width")          var splitWidth:String?=null,
    @ColumnInfo(name="split_len")            var splitLen:String?=null,
    @ColumnInfo(name="split_type")           var splitType:Int?=0,//否，是
    @ColumnInfo(name="seam_num")             var seamNum:String?=null,
    @ColumnInfo(name="description")          var description:String?=null,
    @ColumnInfo(name="pic_path")            var picPath:String?=null,//本地路径
    @ColumnInfo(name="pic_id")              var picId:String?=null,
    @ColumnInfo(name="pic_url")             var picUrl:String?=null,
    @ColumnInfo(name="create_time")         var createTime: Date?=null,
    @ColumnInfo(name="update_time")         var updateTime: Date?= Date(java.util.Date().time),
) {
    @ColumnInfo(name="id")
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0

    constructor(id:Long):this(){
        this.id =id
    }

}