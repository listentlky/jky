package com.sribs.common.bean.db

import java.sql.Date

/**
 * @date 2021/8/3
 * @author elijah
 * @Description
 */
data class ReportBean(
    var projectId:Long?=null,
    var unitId:Long?=null,
    var configId:Long?=null,
    var reportId:Long?=null,
    var report:String?=null,
    var isSave:Int?=0,
    var signPath:String?=null,
    var signResId:String?=null,
    var signResUrl:String?=null,
    var createTime: Date?=null,
    var updateTime: Date?=null
):BaseDbBean() {
    override fun getThis(): BaseDbBean = this
}
