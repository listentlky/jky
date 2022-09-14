package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName

/**
 * @date 2021/8/4
 * @author elijah
 * @Description
 */
data class LoginReq(
    @SerializedName("account")           var account:String,
    @SerializedName("password")          var password:String
)
