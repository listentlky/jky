package com.sribs.common.bean.db

import java.sql.Date

/**
 * @date 2021/7/30
 * @author elijah
 * @Description
 */
data class RoomDetailBean(
    var projectId:Long?=null,
    var unitId:Long?=null,
    var configId:Long?=null,
    var roomDetailId:Long?=null,
    var name:String?=null,
    var damagePath:String?=null,
    var damageIdx:Int?=null,
    var splitNum:String?=null,
    var splitWidth: String?=null,
    var splitLen:String?=null,
    var splitType:Int?=null,
    var seamNum:String?=null,
    var description:String?=null,
    var picPath:String?=null,
    var picId:String?=null,
    var picUrl:String?=null,
    var createTime: Date?=null,
    var updateTime:Date?=null
):BaseDbBean() {
    override fun getThis(): BaseDbBean = this
}
