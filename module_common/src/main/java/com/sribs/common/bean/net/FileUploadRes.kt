package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName

/**
 * @date 2021/3/15
 * @author elijah
 * @Description
 */
data class FileUploadRes(
    @SerializedName("resId")       var resId:String,
    @SerializedName("resUrl")      var resUrl:String
)
