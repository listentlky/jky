package com.sribs.common.bean.db


/**
 * create time: 2022/9/8
 * author: bruce
 * description:
 */
class RelativeHDiffPointBean(){

    var name: String? = null
    var menu: ArrayList<RelativeHDiffPointBean.Item> = ArrayList()

    class Item(){
        var name: String? = null
        var annotRef:Long = -1

        override fun toString(): String {
            return "Item(name=$name, annotRef=$annotRef)"
        }

    }

    override fun toString(): String {
        return "CheckRHDChoosePointBean(name=$name, menu=$menu)"
    }
}