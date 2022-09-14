package com.sribs.common.bean

/**
 * @date 2021/7/2
 * @author elijah
 * @Description
 */
class CheckBtnNormalBean(
    var title:String,
    var isChecked:Boolean,
    var enable:Boolean=true
):CheckBtnBaseBean() {
    override val type:Int = TYPE_BTN
}