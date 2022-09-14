package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName

/**
 * @date 2021/8/19
 * @author elijah
 * @Description
 */
data class UnitListReq(
    @SerializedName("projectId")  var projectId:String
)
