package com.sribs.common.bean.net.v3

import com.google.gson.annotations.SerializedName

/**
 * create time: 2022/10/12
 * author: bruce
 * description:
 */
data class V3BuildingSaveReq(
    @SerializedName("projectId") var projectId:String,
    @SerializedName("buildingId") var buildingId:String,
    @SerializedName("buildName") var buildName:String,
    @SerializedName("buildingType") var buildingType:String,
    @SerializedName("leaderId") var leaderId:String,
    @SerializedName("leaderName") var leaderName:String,
    @SerializedName("aboveGroundNumber") var aboveGroundNumber:String,
    @SerializedName("underGroundNumber") var underGroundNumber:String,
    @SerializedName("drawings") var drawings:List<String>,
    @SerializedName("floorDrawings") var floorDrawings:HashMap<String?, Any?>,
    @SerializedName("inspectors") var inspectors:List<String>
    /*    @SerializedName("createTime") var createTime:String,
    @SerializedName("updateTime") var updateTime:String,*/
){
    constructor():this("","","","","","","","", ArrayList(),HashMap<String?, Any?>(), ArrayList())

}