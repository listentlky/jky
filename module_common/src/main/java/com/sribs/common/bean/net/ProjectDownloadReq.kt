package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName

/**
 * @date 2021/8/6
 * @author elijah
 * @Description
 */
data class ProjectDownloadReq(
    @SerializedName("historyId")       var historyId:String
)
