package com.sribs.bdd.bean

import com.sribs.bdd.bean.RoomItemBean.Companion.TYPE_CONTENT

/**
 * create time: 2022/9/19
 * author: bruce
 * description:
 */
class BuildingMainBean (
    var projectUUID:String?,
    var projectId:Long?,
    var bldUUID:String?,
    var bldId:Long?,
    var bldName:String?,
    var bldType:String?,
    var leader:String?,
    var inspectorName:String?="",
    var remoteId:String?=null,
    var createTime:String?="",
    var updateTime:String?="",
    var version:Int?=1,
    var status:String?="0",  //0 本地
    var aboveGroundNumber:Int?,
    var underGroundNumber:Int?
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
    constructor(tag:String):this("",-1,"",-1,"","","","","","",
        "",1,"0",0,0){
        type = RoomItemBean.TYPE_GROUP
        this.tag = tag
        spanCount = 3
    }

    override fun toString(): String {
        return "BuildingMainBean(projectId=$projectId, bldUUID=$bldUUID, bldId=$bldId, bldName=$bldName, bldType=$bldType, leader=$leader, inspectorName=$inspectorName, remoteId=$remoteId, createTime=$createTime, updateTime=$updateTime, version=$version, status=$status, aboveGroundNumber=$aboveGroundNumber, underGroundNumber=$underGroundNumber, isMenuChecked=$isMenuChecked, isCardSel=$isCardSel, hasNewer=$hasNewer, type=$type, houseType=$houseType, tag='$tag', spanCount=$spanCount)"
    }


}


