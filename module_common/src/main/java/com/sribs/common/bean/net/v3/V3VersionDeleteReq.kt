package com.sribs.common.bean.net.v3

import com.google.gson.annotations.SerializedName

/**
 * create time: 2022/10/13
 * author: bruce
 * description:
 */
data class V3VersionDeleteReq(
    @SerializedName("id") var projectId: String,
    @SerializedName("superiorVersion") var superiorVersion: Long,
    @SerializedName("version") var version: Long,
)
