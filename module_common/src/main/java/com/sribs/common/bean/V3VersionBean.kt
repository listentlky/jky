package com.sribs.common.bean

/**
 * create time: 2022/10/13
 * author: bruce
 * description:
 */
data class V3VersionBean (
    var projectId:String?,
    var projectName:String?,
    var leaderName:String,
    var leaderId:String,
    var inspectors:List<String>,
    var parentVersion:String,
    var version:String,
    var createTime:String
        ){
    var isCheck = false
}