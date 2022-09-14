package com.sribs.db.leader

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @date 2021/8/9
 * @author elijah
 * @Description
 */
@Entity(tableName = "leader")
data class LeaderBean(
    @ColumnInfo(name="name")              var name:String?=null,
){
    @ColumnInfo(name="id")
    @PrimaryKey
    var id:String = "0"



    constructor(id:String,name:String):this(name){
        this.id = id
    }
}
