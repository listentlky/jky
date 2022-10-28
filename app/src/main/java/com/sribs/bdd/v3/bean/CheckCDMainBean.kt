package com.sribs.bdd.v3.bean

import com.sribs.common.bean.db.DrawingV3Bean

/**
 * create time: 2022/9/26
 * author: bruce
 * description:
 */
data class CheckCDMainBean (
    var id : Long?,
    var projectId:Long?,
    var bldId:Long?,
    var moduleId:Long?,
    var floorId:String?,
    var floorName:String?,
    var remoteId:String?=null,
    var inspectorName:String?="",
    var drawing:List<DrawingV3Bean>?=ArrayList(),
    var createTime:String?="",
    var updateTime:String?="",
    var deleteTime:String?="",
    var version:Int?=1,
    var status:Int?=0
){
    override fun toString(): String {
        return "CheckCDFMainBean(projectId=$projectId, bldId=$bldId, moduleId=$moduleId, floorId=$floorId, floorName=$floorName, remoteId=$remoteId, inspectorName=$inspectorName, drawing=$drawing, createTime=$createTime, updateTime=$updateTime, deleteTime=$deleteTime, version=$version, status=$status)"
    }
}
