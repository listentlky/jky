package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName

/**
 * @date 2021/8/4
 * @author elijah
 * @Description
 */
data class ProjectListRes(
    @SerializedName("projectId")         var projectId:String,
    @SerializedName("projectName")       var projectName:String,
    @SerializedName("leaderId")          var leaderId:String,
    @SerializedName("leaderName")        var leaderName:String,
    @SerializedName("buildingNo")        var buildingNo:String,
    @SerializedName("inspectorList")     var inspectorList:List<String>?,
    @SerializedName("createTime")        var createTime:String,
    @SerializedName("updateTime")        var updateTime:String
)
