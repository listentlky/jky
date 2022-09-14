package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName


data class UploadUnitRecordReq(
    @SerializedName("projectId")      var projectId:String,
    @SerializedName("unitId")         var unitId:String?,
    @SerializedName("unitNo")         var unitNo:String,
    @SerializedName("parts")          var parts:List<Parts>,
    @SerializedName("version")        var version:Int,//unitVersion
    @SerializedName("updateTime")     var updateTime:String,
    @SerializedName("willCover")      var willCover:Int
)

