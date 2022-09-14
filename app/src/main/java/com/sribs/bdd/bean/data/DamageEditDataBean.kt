package com.sribs.bdd.bean.data

import kotlin.properties.Delegates

/**
 * @date 2021/7/22
 * @author elijah
 * @Description
 */
class DamageEditDataBean: com.sribs.common.bean.BaseDataBean() {

    var damagePic:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    var damageDes:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    var splitNum:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    var splitWidth:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    var splitLen:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    var splitType:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    var seamNum:String by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    override fun getThis(): com.sribs.common.bean.BaseDataBean = this

    override fun setData(b: com.sribs.common.bean.BaseDataBean) {
        if (b !is DamageEditDataBean)return
        this.damageDes = b.damageDes
        this.damagePic = b.damagePic
        this.splitNum = b.splitNum
        this.splitWidth = b.splitWidth
        this.splitLen = b.splitLen
        this.splitType = b.splitType
        this.seamNum = b.seamNum
    }

    fun clean(){
        damageDes = ""
        damagePic = ""
        splitNum ="1条"
        splitWidth = ""
        splitLen = ""
        splitType = ""
        seamNum = "1条"
    }

    override fun toString(): String {
        return "DamageEditDataBean(damagePic='$damagePic', damageDes='$damageDes', splitNum='$splitNum', splitWidth='$splitWidth', splitLen='$splitLen', splitType='$splitType', seamNum='$seamNum')"
    }


}