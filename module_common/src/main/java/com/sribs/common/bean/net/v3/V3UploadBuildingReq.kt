package com.sribs.common.bean.net.v3

/**
 * create time: 2022/10/14
 * author: bruce
 * description:
 */
data class V3UploadBuildingReq (
    var buildingId:String,
    var buildingNo:String,
    var buildingType:String,
    var inspectors:List<String>,
    var isChanged:Boolean,
    var leaderId:String,
    var leaderName:String,
    var moduleIds:List<String>,
    var modules:List<V3UploadModuleReq>,
    var drawings:List<V3UploadDrawingReq>,
    var parentVersion:Long,
    var projectId:String,
    var superiorVersion:Long,
    var version:Long
    )
