package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName

/**
 * @date 2021/8/17
 * @author elijah
 * @Description
 */
data class UnitDownloadReq(
    @SerializedName("unitId")    var unitId:String
)