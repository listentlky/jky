package com.sribs.common.bean.db

import java.sql.Date

/**
 * @date 2021/7/30
 * @author elijah
 * @Description
 */
data class RoomStatusBean(
    var projectId:Long?=null,
    var unitId:Long?=null,
    var configId:Long?=null,
    var roomId:Long?=null,
    var name:String?=null,
    var isFinish:Int?=0,
    var roomStatus:String?=null,
    var roomFurnishTime: Date?=null,
    var roomNote:String?=null,
    var createTime:Date?=null,
    var updateTime:Date?=null
):BaseDbBean() {
    override fun getThis(): BaseDbBean = this
}
