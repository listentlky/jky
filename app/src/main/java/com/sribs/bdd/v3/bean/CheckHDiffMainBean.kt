package com.sribs.bdd.v3.bean

import com.sribs.common.bean.db.DrawingV3Bean

/**
 * create time: 2022/9/26
 * author: bruce
 * description:
 */
class CheckHDiffMainBean (
    var projectId:Long?,
    var bldId:Long?,
    var moduleId:Long?,
    var moduleName:String?="",
    var drawing:List<DrawingV3Bean>?=ArrayList(),
    var inspectorName:String?="",
    var leaderNamr:String?="",
    var createTime:String?="",
    var updateTime:String?="",
    var deleteTime:String?="",
    var version:Long?=System.currentTimeMillis(),
    var status:Int?=0,
    var isChanged:Boolean?=false
        ){

    override fun toString(): String {
        return "CheckHDiffMainBran(projectId=$projectId, bldId=$bldId, moduleId=$moduleId, moduleName=$moduleName, drawing=$drawing, inspectorName=$inspectorName, leaderNamr=$leaderNamr, createTime=$createTime, updateTime=$updateTime, deleteTime=$deleteTime, version=$version, status=$status)"
    }
}