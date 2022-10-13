package com.sribs.common.bean.net.v3

import com.google.gson.annotations.SerializedName

/**
 * create time: 2022/10/13
 * author: bruce
 * description:
 */
data class V3VersionReq(
    @SerializedName("superiorId") var superiorId: String,
    @SerializedName("searchId") var searchId: String,
)
