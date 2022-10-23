package com.sribs.common.bean.net.v3

/**
 * create time: 2022/10/16
 * author: bruce
 * description:
 */
data class V3UploadDamageReq (
    var annotRef:Long,
    var damageType:String,
    var desc:String,
    var resId:String,
    var drawingId:String,
    var drawingName:String,
    var floorNo:String,
    var inspectors:List<String>,
    var moduleId:String,
    var moduleName:String,
    var moduleVersion:Long
        )
