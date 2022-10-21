package com.sribs.bdd.bean

import com.sribs.bdd.bean.RoomItemBean.Companion.TYPE_CONTENT
import com.sribs.common.bean.db.DrawingV3Bean

/**
 * create time: 2022/9/19
 * author: bruce
 * description:
 */
class BuildingMainBean (
    var projectRemoteId:String = "",
    var projectUUID:String = "",
    var projectId:Long = -1,
    var bldUUID:String="",
    var bldId:Long=-1,
    var bldName:String="",
    var bldType:String="",
    var leader:String="",
    var inspectorName:String="",
    var remoteId:String?=null,
    var createTime:String="",
    var updateTime:String="",
    var superiorVersion:Long?=0,
    var parentVersion:Long?=0,
    var version:Long?=System.currentTimeMillis(),
    var status:String="0",  //0 本地
    var aboveGroundNumber:Int=0,
    var underGroundNumber:Int=0,
    var drawingList:List<DrawingV3Bean>?=ArrayList(),
    var isChanged:Int?= 0
){
    var isMenuChecked:Boolean = false
    var isCardSel:Boolean = false
    var hasNewer:Boolean = false
    var type = TYPE_CONTENT
    var houseType:Int = 0
    var tag = ""
    var spanCount = 1

    /**
     * @Description 分类标签
     */
    constructor(tag:String):this("","",-1,"",-1,"","","","","","",
        "",0,0,0,"0",0,0){
        type = RoomItemBean.TYPE_GROUP
        this.tag = tag
        spanCount = 3
    }

    override fun toString(): String {
        return "BuildingMainBean(projectRemoteId='$projectRemoteId', projectUUID='$projectUUID', projectId=$projectId, bldUUID='$bldUUID', bldId=$bldId, bldName='$bldName', bldType='$bldType', leader='$leader', inspectorName='$inspectorName', remoteId=$remoteId, createTime='$createTime', updateTime='$updateTime', superiorVersion=$superiorVersion, parentVersion=$parentVersion, version=$version, status='$status', aboveGroundNumber=$aboveGroundNumber, underGroundNumber=$underGroundNumber, drawingList=$drawingList, isChanged=$isChanged, isMenuChecked=$isMenuChecked, isCardSel=$isCardSel, hasNewer=$hasNewer, type=$type, houseType=$houseType, tag='$tag', spanCount=$spanCount)"
    }


}


