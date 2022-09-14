package com.sribs.common.bean.db

/**
 * @date 2021/8/4
 * @author elijah
 * @Description
 */
data class UserBean(
    var id:String?=null,
    var name:String?=null,
    var account:String?=null,
    var password:String?=null,
    var mobile:String?=null,
    var salt:String?=null
)
