package com.sribs.common.bean

/**
 * @date 2021/7/2
 * @author elijah
 * @Description
 */
open class CheckBtnBaseBean {
    companion object{
        const val TYPE_BTN   = 1
        const val TYPE_INPUT = 2
        const val TYPE_ADD   = 3
    }
    open val type:Int?=null
}