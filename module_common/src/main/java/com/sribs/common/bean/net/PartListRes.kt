package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName

/**
 * @date 2021/8/19
 * @author elijah
 * @Description
 */
data class PartListRes(
    @SerializedName("unitId")  var unitId:String,
    @SerializedName("unitNo")  var unitNo:String,
    @SerializedName("partType") var partType:String,
    @SerializedName("partNo")  var partNo:String,
    @SerializedName("partNow") var partNow:PartNow,
    @SerializedName("inspectorId") var inspectorId:String,
    @SerializedName("inspectorName") var inspectorName:String,
    @SerializedName("isCompleted") var isCompleted:Int,
    @SerializedName("version") var version:Int,
    @SerializedName("createTime") var createTime:String,
    @SerializedName("updateTime") var updateTime:String
)