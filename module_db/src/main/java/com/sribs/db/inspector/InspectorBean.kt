package com.sribs.db.inspector

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @date 2021/8/9
 * @author elijah
 * @Description
 */
@Entity(tableName = "inspector")
data class InspectorBean(
    @ColumnInfo(name="name")              var name:String?=null,
){
    @ColumnInfo(name="id")
    @PrimaryKey
    var id:String = "0"



    constructor(id:String,name:String):this(name){
        this.id = id
    }
}
