package com.sribs.common.bean

/**
 * @date 2021/7/2
 * @author elijah
 * @Description
 */
class CheckBtnAddBean():CheckBtnBaseBean() {
    override val type:Int = TYPE_ADD
    var mTip:String?=null
    constructor(tip:String?):this(){
        mTip = tip
    }
}