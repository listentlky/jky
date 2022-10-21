package com.sribs.common.bean.net.v3

import com.google.gson.annotations.SerializedName

/**
 * create time: 2022/10/13
 * author: bruce
 * description:
 */
data class V3VersionListRes(
    @SerializedName("projectId") var projectId: String?,
    @SerializedName("projectName") var projectName: String?,
    @SerializedName("leaderName") var leaderName: String?,
    @SerializedName("leaderId") var leaderId: String?,
    @SerializedName("parentVersion") var parentVersion: String,
    @SerializedName("version") var version: String,
    @SerializedName("inspectors") var inspectors: List<String>,
    @SerializedName("createTime") var createTime: String
)
