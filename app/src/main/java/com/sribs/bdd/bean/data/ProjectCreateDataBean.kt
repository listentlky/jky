package com.sribs.bdd.bean.data

import kotlin.properties.Delegates

/**
 * @date 2021/6/30
 * @author elijah
 * @Description
 */
class ProjectCreateDataBean: com.sribs.common.bean.BaseDataBean() {

    var name by Delegates.observable(""){
        d,old,new -> onDataChange(d,old,new)
    }

    var leader by Delegates.observable(""){
        d,old,new -> onDataChange(d,old,new)
    }

    var number by Delegates.observable(""){
        d,old,new-> onDataChange(d,old,new)
    }
    //质检员
    var inspector by Delegates.observable(""){
            d,old,new-> onDataChange(d,old,new)
    }

    override fun getThis(): com.sribs.common.bean.BaseDataBean = this
    override fun setData(b: com.sribs.common.bean.BaseDataBean) {
        if (b !is ProjectCreateDataBean)return
        name = b.name
        leader = b.leader
        number = b.number
        inspector = b.inspector
    }

    override fun toString(): String {
        return "ProjectCreateDataBean(name='$name', mgr='$leader', number='$number',inspector='$inspector')"
    }

    override fun copy(): com.sribs.common.bean.BaseDataBean? =
        ProjectCreateDataBean().also { it.setData(this) }
}