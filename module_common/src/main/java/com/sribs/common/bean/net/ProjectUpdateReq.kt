package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName

data class ProjectUpdateReq(
    @SerializedName("projectId")   var projectId:String?,
    @SerializedName("projectName") var projectName:String,
    @SerializedName("leaderId")    var leaderId:String,
    @SerializedName("buildingNo")  var buildingNo:String
)
