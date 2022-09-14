package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName

/**
 * @date 2021/8/5
 * @author elijah
 * @Description
 */
data class HistoryListReq(
    @SerializedName("projectId")    var projectId:String,
    @SerializedName("unitNo")       var unitNo:String?,
    @SerializedName("pageNo")       var pageNo:Int?,
    @SerializedName("pageSize")     var pageSize:Int?
){
    constructor(projectId: String,unitNo: String?=null):this(projectId,unitNo,1,1000)
}
