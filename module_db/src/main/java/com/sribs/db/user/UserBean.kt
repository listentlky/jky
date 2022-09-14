package com.sribs.db.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @date 2021/8/4
 * @author elijah
 * @Description
 */
@Entity(tableName = "user")
data class UserBean(
    @ColumnInfo(name="name")              var name:String?=null,
    @ColumnInfo(name="account")           var account:String?=null,
    @ColumnInfo(name="password")          var password:String?=null,
    @ColumnInfo(name="mobile")            var mobile:String?=null,
    @ColumnInfo(name="salt")              var salt:String?=null
) {
    @ColumnInfo(name="id")
    @PrimaryKey
    var id:String = "0"
}