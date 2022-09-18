package com.sribs.common.bean.net.v3

import com.google.gson.annotations.SerializedName

data class V3ProjectListRes (
    @SerializedName("projectId")   var projectId:String,
    @SerializedName("projectName")   var projectName:String,
    @SerializedName("inspectors")   var inspectors:List<String>,
    @SerializedName("leaderId")   var leaderId:String?,
    @SerializedName("leaderName")   var leaderName:String,
    @SerializedName("createTime")   var createTime:String,
    @SerializedName("updateTime")   var updateTime:String,
)