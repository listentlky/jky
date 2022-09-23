package com.sribs.bdd.v3.bean

import com.sribs.common.bean.db.DrawingV3Bean

/**
 * create time: 2022/9/19
 * author: bruce
 * description:
 */
class CheckBSMainBean (
    var id:Long?,
    var projectId:Long?,
    var bldId:Long?,
    var floorName:String?,
    var remoteId:String?=null,
    var inspectorName:String?="",
    var drawing:List<DrawingV3Bean>?=ArrayList(),
    var createTime:Long?=0,
    var updateTime:Long?=0,
    var deleteTime:Long?=0,
    var version:Int?=1,
    var status:Int?=0
){
    override fun toString(): String {
        return "CheckBSMainBean(projectId=$projectId, bldId=$bldId, floorName=$floorName, remoteId=$remoteId, inspectorName=$inspectorName, drawing=$drawing, createTime=$createTime, updateTime=$updateTime, deleteTime=$deleteTime, version=$version, status=$status)"
    }
}
