package com.sribs.common.bean.db.v3.project

import com.sribs.common.bean.db.DrawingV3Bean
import kotlin.collections.ArrayList

/**
 * create time: 2022/9/17
 * author:
 * description:
 */
data class v3BuildingModuleDbBean(
    var id: Long? = null,
    var uuid:String?="",
    var buildingRemoteId:String?="",
    var buildingUUID:String?="",
    var buildingId: Long? = null,
    var projectUUID: String?="",
    var projectId: Long? =null,
    var moduleName: String? = null,
    var leaderId: String? ="",
    var leaderName: String? = "",
    var isDeleted: Int? = 0,
    var aboveGroundNumber: Int? = 0,
    var underGroundNumber: Int? = 0,
    var drawings: List<DrawingV3Bean>?= ArrayList(),
    var inspectors: String?= "",
    var deleteTime: String? = "",
    var createTime: String? = "",
    var updateTime: String? = null,
    var remoteId: String? = null,
    var superiorVersion: Long? = 0,
    var parentVersion: Long? = 0,
    var version: Long? = System.currentTimeMillis(),
    var status: Int? = 0,
    var isChanged: Boolean? = false
) {

    fun isSame(buildingId: Long?, projectId: Long?): Boolean =
        this.buildingId == buildingId && this.projectId == projectId

}