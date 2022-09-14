package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName

/**
 * @date 2021/8/4
 * @author elijah
 * @Description
 */
data class UserInfoRes(
    @SerializedName("id")         var id:String,
    @SerializedName("name")       var name:String?,
    @SerializedName("account")    var account:String?,
    @SerializedName("password")   var password:String?,
    @SerializedName("mobile")     var mobile:String?,
    @SerializedName("salt")       var salt:String?
)
