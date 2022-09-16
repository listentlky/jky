package com.sribs.bdd.v3.bean

/**
 * create time: 2022/9/16
 * author: bruce
 * description:
 */
data class ProjectBean(

    var projectId:String,
    var projectName:String,
    var inspectors:List<String>,
    var leaderId:String,
    var leaderName:String,
    var createTime:String,
    var updateTime:String

)