package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName

/**
 * @date 2021/8/6
 * @author elijah
 * @Description
 */
data class UploadUnitConfigReq(
    @SerializedName("projectId")         var projectId:String,
    @SerializedName("unitId")            var unitId:String?,
    @SerializedName("unitNo")            var unitNo:String,
    @SerializedName("floorCount")        var floorCount:Int,
    @SerializedName("roomCount")         var roomCount:Int,
    @SerializedName("staircaseType")     var staircaseType:String,
    @SerializedName("floors")            var configFloors:List<ConfigFloor>,
    @SerializedName("version")           var version:Int,
    @SerializedName("willCover")         var willCover:Int,
    @SerializedName("updateTime")        var updateTime:String
)
