package com.sribs.common.bean.db

import java.sql.Date

/**
 * @date 2021/7/8
 * @author elijah
 * @Description
 */
data class UnitBean(
    val projectId:Long?,
    var bldId: Long?,
    var unitId:Long?=null,
    var unitNo:String?=null,
    var floorSize:Int?=null,
    var neighborSize:Int?=null,
    var floorType:Int?=null,
    var createTime:Date?=null,
    var updateTime:Date?=null,
    var remoteId:String?=null,
    var version:Int?=null,
    var status:Int?=null,  //0  本地    1 本地已上传
    var leaderId:String?=null,
    var leaderName:String?=null,
    var inspectors:String?=null
){
    fun isSame( unitNo:String?, floorSize:Int?, neighborSize:Int?,floorType: Int?):Boolean{
        var isSame = true
        if (unitNo!=null && unitNo!=this.unitNo) isSame = false
        if (floorSize!=null && floorSize!=this.floorSize) isSame = false
        if (neighborSize!=null && neighborSize!=this.neighborSize) isSame = false
        if (floorType!=null && floorType!=this.floorType) isSame = false
        return isSame
    }
}