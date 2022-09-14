package com.sribs.bdd.bean.data

import android.widget.CheckBox
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * @date 2021/8/3
 * @author elijah
 * @Description
 */
class ReportDataBean: com.sribs.common.bean.BaseDataBean() {

    var report:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }
    var isSave:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
        if (old!=new) onCheckBoxChange(d,new)
    }

    var signPath:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    private fun onCheckBoxChange(d: KProperty<*>,new:String){
        var cb = fieldMap[d.name]
        if (cb is CheckBox){
            cb.isChecked = new=="1"
        }
    }


    override fun getThis(): com.sribs.common.bean.BaseDataBean = this

    override fun setData(b: com.sribs.common.bean.BaseDataBean) {
        if ( b !is ReportDataBean){
            return
        }
        report = b.report
        isSave = b.isSave
        signPath = b.signPath
    }
}