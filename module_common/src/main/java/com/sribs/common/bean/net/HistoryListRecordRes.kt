package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName

/**
 * @date 2021/8/9
 * @author elijah
 * @Description
 */
data class HistoryListRecordRes(
    @SerializedName("configHistoryId")        var configHistoryId:String?,
    @SerializedName("recordHistoryId")        var recordHistoryId:String?,
    @SerializedName("unitId")                 var unitId:String?,
    @SerializedName("createTime")             var createTime:String,
    @SerializedName("updateTime")             var updateTime:String,
    @SerializedName("userName")               var userName:String
)
