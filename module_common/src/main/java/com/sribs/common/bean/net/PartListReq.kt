package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName

/**
 * @date 2021/8/19
 * @author elijah
 * @Description
 */
data class PartListReq(
    @SerializedName("unitId")  var unitId:String
)
