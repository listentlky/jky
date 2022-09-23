package com.sribs.common.bean.db.v3.project

import com.sribs.common.bean.db.DrawingV3Bean

/**
 * create time: 2022/9/17
 * author:
 * description:
 */
data class v3BuildingModuleDbBean(
    var id: Long? = null,
    var buildingId: String? = null,
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
    var remoteId: String? = null
) {

    fun isSame(buildingId: String?, projectId: Long?): Boolean =
        this.buildingId == buildingId && this.projectId == projectId

}