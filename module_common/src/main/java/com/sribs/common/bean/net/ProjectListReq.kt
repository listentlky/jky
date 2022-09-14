package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName

/**
 * @date 2021/8/4
 * @author elijah
 * @Description
 */
data class ProjectListReq(
    @SerializedName("keyword")   var keyword:String?,
    @SerializedName("dateFrom")  var dateFrom:String?,
    @SerializedName("dateTo")    var dateTo:String?,
    @SerializedName("pageNo")    var pageNo:Int?,
    @SerializedName("pageSize")  var pageSize:Int?
){
    constructor():this(null,null,null,1,1000)
}