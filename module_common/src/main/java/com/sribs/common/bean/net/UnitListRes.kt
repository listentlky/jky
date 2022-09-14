package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName

/**
 * @date 2021/8/19
 * @author elijah
 * @Description
 */
data class UnitListRes(
    @SerializedName("unitId")  var unitId:String,
    @SerializedName("unitNo")  var unitNo:String,
    @SerializedName("version") var version:Int,
    @SerializedName("createTime") var createTime:String,
    @SerializedName("updateTime") var updateTime:String
)
