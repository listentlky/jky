package com.sribs.common.bean.db

import java.sql.Date

/**
 * @date 2021/7/8
 * @author elijah
 * @Description
 */
data class ProjectBean(
    var id:Long?=null,
    var name:String?=null,
    var leader:String?=null,
    var inspector:String?=null,
    var buildNo:String?=null,
    var status:Int?=null,

    var createTime:Date?=null,
    var updateTime:Date?=null,

    var remoteId:String?=null,
    var flag:Int?=null
){
    fun isSame(name:String?,leader:String?,buildNo: String?):Boolean =
        this.name == name && this.leader == leader && this.buildNo == buildNo
}