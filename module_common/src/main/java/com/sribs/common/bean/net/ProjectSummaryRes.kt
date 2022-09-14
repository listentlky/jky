package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName

/**
 * @date 2021/8/19
 * @author elijah
 * @Description
 */
data class ProjectSummaryRes(
    @SerializedName("roomCount")  var roomCount:Int,
    @SerializedName("finishedCount") var finishedCount:Int,
    @SerializedName("notAllowedCount") var notAllowedCount:Int,
    @SerializedName("noPersonCount") var noPersonCount:Int,
    @SerializedName("entryRatio") var entryRatio:Float
)
