package com.sribs.bdd.bean.data

import kotlin.properties.Delegates

/**
 * @date 2021/7/16
 * @author elijah
 * @Description
 */
class HouseStatusDataBean: com.sribs.common.bean.BaseDataBean() {
    override fun getThis(): com.sribs.common.bean.BaseDataBean = this

    override fun setData(b: com.sribs.common.bean.BaseDataBean) {
        if (b !is HouseStatusDataBean )return

    }

    var status by Delegates.observable(""){
            d,old,new -> onDataChange(d,old,new)
        if (old!=new) checkStatus(new)
    }

    var date by Delegates.observable(""){
            d,old,new -> onDataChange(d,old,new)
    }

    var note by Delegates.observable(""){
            d,old,new -> onDataChange(d,old,new)
    }

    private fun checkStatus(s:String){
        callbackMap[::checkStatus.name]?.invoke(arrayOf(s))
    }

    fun bindStatus(cb:(Array<out String>)->Unit):HouseStatusDataBean{
        bindCallback(::checkStatus.name,cb)
        return this
    }
}