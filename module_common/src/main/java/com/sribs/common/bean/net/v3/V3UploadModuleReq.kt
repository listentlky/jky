package com.sribs.common.bean.net.v3

/**
 * create time: 2022/10/14
 * author: bruce
 * description:
 */
data class V3UploadModuleReq(
    val buildingId:String,
    var drawings: List<V3UploadDrawingReq>,
    var inspectors: List<String>,
    var isChanged: Boolean,
    var moduleId: String,
    var moduleName: String,
    var aboveGroundNumber:Int,
    var underGroundNumber:Int,
    var parentVersion: Long,
    var superiorVersion: Long,
    var version: Long,
    var createTime:Long
)
