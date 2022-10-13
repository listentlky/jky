package com.sribs.common.bean.net.v3

import com.google.gson.annotations.SerializedName

/**
 * create time: 2022/10/13
 * author: bruce
 * description:
 */
data class V3ProjectDownloadReq (
    @SerializedName("projectId") var projectId:String,
    @SerializedName("version") var version:Int,
)
