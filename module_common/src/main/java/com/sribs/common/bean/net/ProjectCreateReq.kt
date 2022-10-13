package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName
import java.util.*

data class ProjectCreateReq (
    @SerializedName("inspectors")   var inspectors:List<String>?,
    @SerializedName("leaderId") var leaderId:String,
    @SerializedName("leaderName")    var leaderName:String,
    @SerializedName("projectId")  var projectId:String,
    @SerializedName("projectName")    var projectName:String,
/*    @SerializedName("updateTime")  var updateTime:Date*/
    ){
    constructor():this(null,"","","","")
}
