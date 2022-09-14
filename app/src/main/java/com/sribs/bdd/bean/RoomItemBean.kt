package com.sribs.bdd.bean

import com.sribs.bdd.bean.data.ProjectConfigBaseDataBean
import com.sribs.bdd.bean.data.ProjectConfigBean

/**
 * @date 2021/7/13
 * @author elijah
 * @Description
 */
data class RoomItemBean(
    var localId:Long=-1,
    var remoteId:String="",
    var updateTime:String,
    var status: Int,
    var unitNum:String,
    var floorNeighborNum:String,
    var floorNeighborNumEx: String,
    var inspector:String
){
    var isCardSel:Boolean = false
    var isMenuChecked:Boolean = false
    var isFinish:Boolean = false
    var dataBean: ProjectConfigBaseDataBean?=null
    var configBean: ProjectConfigBean?=null
    var houseType:Int = 0
    var houseStatusId:Long=-1
    var houseFinishStatus:String?=null //已完成，无人，不让进
    var type = TYPE_CONTENT
    var tag = ""
    var spanCount = 1
    fun copy():RoomItemBean
            = RoomItemBean(
        this.localId,
        this.remoteId,
        this.updateTime,
        this.status,
        this.unitNum,
        this.floorNeighborNum,
        this. floorNeighborNumEx,
        this.inspector
    ).also {
        it.isCardSel = this.isCardSel
        it.isMenuChecked = this.isMenuChecked
        it.isFinish = this.isFinish
        it.dataBean = this.dataBean?.copy()
        it.configBean = this.configBean?.copy()
        it.houseType = this.houseType
        it.houseStatusId = this.houseStatusId
        it.houseFinishStatus = this.houseFinishStatus
        it.type = this.type
    }



    /**
     * @Description 分类标签
     */
    constructor(tag:String):this(
        -1,"","",-1,"","","",""
    ){
        type = TYPE_GROUP
        this.tag = tag
        spanCount = 3
    }

    companion object {
        const val TYPE_GROUP = 0
        const val TYPE_CONTENT = 1
    }
}