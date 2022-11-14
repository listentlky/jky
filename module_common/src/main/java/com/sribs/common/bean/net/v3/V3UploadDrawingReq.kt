package com.sribs.common.bean.net.v3

/**
 * create time: 2022/10/14
 * author: bruce
 * description:
 */
data class V3UploadDrawingReq (
        var buildingId:String,
        var drawingId:String,
        var drawingName:String,
        var fileType:String,
        var floorId:String,
        var floorNo:String,
        var floorIndex:Int,
        var direction:Int,
        var sort:Int,
        var inspectors:List<String>,
        var moduleId:String,
        var resId:String,
        var url:String,
        var damageMixes: List<V3UploadDamageReq>
        )
