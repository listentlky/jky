package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName

/**
 * @date 2021/8/9
 * @author elijah
 * @Description
 */
data class RoleUserListRes(
    @SerializedName("id")        var id:String,
    @SerializedName("name")      var name:String
)
