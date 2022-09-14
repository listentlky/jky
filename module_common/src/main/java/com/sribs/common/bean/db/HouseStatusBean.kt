package com.sribs.common.bean.db

import java.sql.Date

/**
 * @date 2021/7/30
 * @author elijah
 * @Description
 */
data class HouseStatusBean(
    var projectId:Long?=null,
    var unitId:Long?=null,
    var configId:Long?=null,
    var houseStatusId:Long?=null,
    var name:String?=null,
    var houseType:Int?=null,
    var status:Int?=null,
    var isFinish:Int?=null,
    var inspector:String?=null,
    var houseStatus:String?=null,
    var houseFurnishTime:Date?=null,
    var createTime:Date?=null,
    var updateTime:Date?=null,
    var version:Int?=1
):BaseDbBean(){
    override fun getThis(): BaseDbBean = this
}
