package com.sribs.common.bean.net.v3

/**
 * create time: 2022/10/14
 * author: bruce
 * description: 上传/保存项目
 */
data class V3UploadProjectReq (
    var buildingIds:List<String>,
    var buildings:List<V3UploadBuildingReq>,
    var createTime:String,
    var inspectors:List<String>,
    var isChanged:Boolean,
    var leaderId:String,
    var leaderName:String,
    var parentVersion:Long,
    var projectId:String,
    var projectName:String,
    var version:Long
    )
