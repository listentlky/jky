package com.sribs.bdd.bean.data

import kotlin.properties.Delegates

/**
 * @date 2021/7/22
 * @author elijah
 * @Description
 */
class DamageDrawingDataBean: com.sribs.common.bean.BaseDataBean(
//    var axis:String,
//    var damageDes:String?,
//    var damagePic:String?,
) {

    //damagePic，拍照图片路径
    var damagePic:String? by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    var damageDes:String? by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    var axis:String? by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    //damageCrackMonitorPic，裂缝监测点拍照图片路径
    var damageCrackMonitorPic:String? by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }
    //裂缝宽度
    var crackWidth:String? by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    //裂缝长度
    var crackLength:String? by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    //监测点编号
    var monitorPointNo: String? by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    //监测方法
    var monitorWay: String? by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    //刻痕长度
    var nickLength: String? by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    //刻痕宽度
    var nickWidth: String? by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    //损伤类型，普通：common；裂缝监测点：crack
    var damageType: String? by Delegates.observable("") {
            d, old, new -> onDataChange(d,old,new)
    }

    override fun getThis(): com.sribs.common.bean.BaseDataBean = this

    override fun setData(b: com.sribs.common.bean.BaseDataBean) {
        if (b !is DamageDrawingDataBean)return
        this.damageDes = b.damageDes
        this.damagePic = b.damagePic
        this.axis = b.axis
        this.damageCrackMonitorPic = b.damageCrackMonitorPic
        this.crackLength = b.crackLength
        this.crackWidth = b.crackWidth
        this.monitorPointNo = b.monitorPointNo
        this.monitorWay = b.monitorWay
        this.nickLength = b.nickLength
        this.nickWidth = b.nickWidth
        this.damageType = b.damageType
    }

    fun clean(){
        damageDes = ""
        damagePic = ""
        axis = ""
        damageCrackMonitorPic = ""
        crackLength = ""
        crackWidth = ""
        monitorPointNo = ""
        monitorWay = ""
        nickLength = ""
        nickWidth = ""
        damageType = ""
    }

    override fun toString(): String {
        return "DamageEditDataBean(damagePic='$damagePic', damageDes='$damageDes', axis='$axis', damageCrackMonitorPic='$damageCrackMonitorPic', crackLength='$crackLength', crackWidth='$crackWidth', monitorPointNo='$monitorPointNo', monitorWay='$monitorWay', nickLength='$nickLength', nickWidth='$nickWidth', )"
    }


}