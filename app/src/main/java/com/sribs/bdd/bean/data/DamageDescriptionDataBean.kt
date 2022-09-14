package com.sribs.bdd.bean.data

/**
 * @date 2021/8/18
 * @author elijah
 * @Description
 */
class DamageDescriptionDataBean: com.sribs.common.bean.BaseDataBean() {

    var splitNum:String=""

    var splitWidth:String=""

    var splitLen:String=""

    var splitType:String=""

    var seamNum:String=""

    override fun getThis(): com.sribs.common.bean.BaseDataBean = this

    override fun setData(b: com.sribs.common.bean.BaseDataBean) {
    }
}