package com.sribs.common.bean.v3

import java.sql.Date

/**
 * create time: 2022/9/17
 * author:
 * description:
 */
data class v3BuildingModuleDbBean(
    var id: Long? = null,
    var buildingId: String? = null,
    var moduleId: Int? = 0,
    var status: Int? = 0,
    var moduleName: String? = null,
    var leaderId: String? = null,
    var leaderName: String? = null,
    var isDeleted: Int? = 0,
    var aboveGroundNumber: Int? = 0,
    var underGroundNumber: Int? = 0,
    var drawings: List<String>? = null,
    var floorDrawings: List<String>? = null,
    var inspectors: List<String>? = null,
    var deleteTime: String? = null,
    var createTime: String? = null,
    var updateTime: String? = null,
    var remoteId: String? = null
) {

    fun isSame(buildingId: String?, moduleId: Int?): Boolean =
        this.buildingId == buildingId && this.moduleId == moduleId

}