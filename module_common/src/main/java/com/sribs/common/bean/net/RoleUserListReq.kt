package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName

/**
 * @date 2021/8/9
 * @author elijah
 * @Description
 */
data class RoleUserListReq(
    @SerializedName("roleType")       var roleType:String?,
    @SerializedName("name")           var name:String?,
    @SerializedName("pageNo")         var pageNo:Int?,
    @SerializedName("pageSize")       var pageSize:Int?
){
    constructor():this("leader",null,1,1000)
    constructor(roleType:String):this(roleType,null,1,1000)
}
