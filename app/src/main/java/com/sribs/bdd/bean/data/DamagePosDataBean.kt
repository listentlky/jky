package com.sribs.bdd.bean.data

import androidx.recyclerview.widget.RecyclerView
import com.sribs.common.bean.CommonBtnBean
import com.sribs.common.ui.adapter.CommonBtnAdapter
import kotlin.properties.Delegates
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties

/**
 * @date 2021/7/19
 * @author elijah
 * @Description
 */
class DamagePosDataBean: com.sribs.common.bean.BaseDataBean() {

    var pos1:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    var pos2:String  by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    var pos3:String  by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    fun bindPos(prop: KProperty<*>, view: RecyclerView): com.sribs.common.bean.BaseDataBean {
        fieldMap[prop.name] = view
        var adapter = view.adapter
        if (adapter is CommonBtnAdapter){
            adapter.mCb = object :CommonBtnAdapter.OnClickListener{
                override fun onClick(b: CommonBtnBean) {
                    val s = if (b.isSel) b.title else ""
                    val reflectProp  = getThis()::class.memberProperties.find { it.name == prop.name}
                    if (reflectProp is KMutableProperty<*>){
                        reflectProp.setter.call(getThis(),s)
                    }
                }
            }
        }
        return this
    }

    override fun getThis(): com.sribs.common.bean.BaseDataBean = this

    override fun setData(b: com.sribs.common.bean.BaseDataBean) {
        if (b !is DamagePosDataBean){
            return
        }

    }
}