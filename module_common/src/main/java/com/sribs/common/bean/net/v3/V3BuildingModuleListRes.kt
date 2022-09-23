package com.sribs.common.bean.net.v3

import com.google.gson.annotations.SerializedName

data class V3BuildingModuleListRes(
    @SerializedName("buildingId") var buildingId: String,
    @SerializedName("moduleId") var moduleId: Int,
    @SerializedName("moduleName") var moduleName: String,
    @SerializedName("inspectors") var inspectors: List<String>,
    @SerializedName("leaderId") var leaderId: String?,
    @SerializedName("leaderName") var leaderName: String,
    @SerializedName("aboveGroundNumber") var aboveGroundNumber: Int,
    @SerializedName("underGroundNumber") var underGroundNumber: Int,
    @SerializedName("updateTime") var updateTime: String,
    @SerializedName("createTime") var createTime: String,
    @SerializedName("drawings") var drawings: List<String>,
    @SerializedName("floorDrawings") var floorDrawings: List<String>,
)