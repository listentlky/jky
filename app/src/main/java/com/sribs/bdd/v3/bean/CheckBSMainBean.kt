package com.sribs.bdd.v3.bean

/**
 * create time: 2022/9/19
 * author: bruce
 * description:
 */
class CheckBSMainBean (
    var projectId:Long?,
    var bldId:Long?,
    var floorName:String?,
    var remoteId:String?=null,
    var fileName: String?,
    var drawingType: String?,
    var fileType: String?,
    var localAbsPath: String?,
    var remoteAbsPath: String?,
    var createTime:Long?=0,
    var updateTime:Long?=0,
    var deleteTime:Long?=0,
    var version:Int?=1,
    var status:Int?=0
){
    override fun toString(): String {
        return "CheckBSMainBean(projectId=$projectId, bldId=$bldId, floorName=$floorName, remoteId=$remoteId, fileName=$fileName, drawingType=$drawingType, fileType=$fileType, localAbsPath=$localAbsPath, remoteAbsPath=$remoteAbsPath, createTime=$createTime, updateTime=$updateTime, deleteTime=$deleteTime, version=$version, status=$status)"
    }
}
