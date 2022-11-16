package com.sribs.bdd.v3.bean

import com.sribs.common.bean.db.DrawingV3Bean

/**
 * create time: 2022/11/16
 * author: bruce
 * description:
 */
data class CheckNResMainBean (
    var id : Long?,
    var projectId:Long?,
    var bldId:Long?,
    var moduleId:Long?,
    var floorId:String?=null,
    var floorName:String?=null,
    var remoteId:String?=null,
    var inspectorName:String?="",
    var drawing:List<DrawingV3Bean>?=ArrayList(),
    var createTime:String?="",
    var updateTime:String?="",
    var deleteTime:String?="",
    var version:Long?=System.currentTimeMillis(),
    var status:Int?=0,
    var isChanged:Int?=0
){
    override fun toString(): String {
        return "CheckNResMainBean(id=$id, projectId=$projectId, bldId=$bldId, moduleId=$moduleId, floorId=$floorId, floorName=$floorName, remoteId=$remoteId, inspectorName=$inspectorName, drawing=$drawing, createTime=$createTime, updateTime=$updateTime, deleteTime=$deleteTime, version=$version, status=$status, isChanged=$isChanged)"
    }
}