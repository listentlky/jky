package com.sribs.bdd.bean.data

import kotlin.properties.Delegates

/**
 * @date 2021/6/23
 * @author elijah
 * @Description
 */
class LoginDataBean : com.sribs.common.bean.BaseDataBean() {
    var account:String by Delegates.observable("") {
        d, old, new -> onDataChange(d,old,new)
    }

    var pwd:String by Delegates.observable(""){
        d,old,new -> onDataChange(d,old,new)
    }

    override fun getThis(): com.sribs.common.bean.BaseDataBean = this

    override fun setData(b: com.sribs.common.bean.BaseDataBean) {
        if (b !is LoginDataBean)return
        this.account = b.account
        this.pwd = b.pwd
    }

    override fun toString(): String {
        return "LoginDataBean(phone='$account', pwd='$pwd')"
    }

}