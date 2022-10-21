package com.sribs.common.bean.db


/**
 * @date 2021/7/8
 * @author elijah
 * @Description
 */
data class ProjectBean(
    var id:Long?=null,
    var uuid:String?="",
    var name:String?=null,
    var leader:String?=null,
    var inspector:String?=null,
    var buildNo:String?=null,
    var status:Int?=null,
    var parentVersion:Long?=0,
    var version:Long?=System.currentTimeMillis(),

    var createTime:String?=null,
    var updateTime:String?=null,

    var remoteId:String?=null,
    var flag:Int?=null,
    var isChanged: Int? = 0
){
    fun isSame(name:String?,leader:String?,buildNo: String?):Boolean =
        this.name == name && this.leader == leader && this.buildNo == buildNo
}