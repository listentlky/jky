package com.sribs.common.bean

/**
 * @date 2021/8/5
 * @author elijah
 * @Description
 */
data class HistoryBean(
    var remoteConfigHistoryId :String ?=null,
    var remoteRecordHistoryId: String ?=null,
    var unitId:String?=null,
    var createTime:String? = null,
    var userName:String?=null
){
    var isCheck = false
}
