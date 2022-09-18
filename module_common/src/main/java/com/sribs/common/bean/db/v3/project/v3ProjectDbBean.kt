package com.sribs.common.bean.db.v3.project

import java.sql.Date

/**
 * create time: 2022/9/16
 * author: bruce
 * description:
 */
data class v3ProjectDbBean (
    var id:Long?=null,
    var projectName:String?=null,
    var inspectors:List<String>?=null,
    var leaderId:String?=null,
    var leaderName:String?=null,
    var projectId:String?=null,
    var status:Int?=null,
    var createTime:Date?=null,
    var updateTime:Date?=null
        ){

    fun isSame(projectName:String?,leaderName:String?):Boolean =
        this.projectName == projectName && this.leaderName == leaderName

}