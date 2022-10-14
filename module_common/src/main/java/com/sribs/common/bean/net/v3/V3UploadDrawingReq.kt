package com.sribs.common.bean.net.v3

import com.sribs.common.bean.db.DamageV3Bean

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
        var inspectors:List<String>,
        var moduleId:String,
        var resId:String,
        var url:String,
/*        var damage:List<DamageV3Bean>*/
        )
