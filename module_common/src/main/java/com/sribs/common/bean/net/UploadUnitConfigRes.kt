package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName

/**
 * @date 2021/8/6
 * @author elijah
 * @Description
 */
data class UploadUnitConfigRes(
    @SerializedName("unitId")            var unitId:String,
    @SerializedName("version")           var version:Int,
)
