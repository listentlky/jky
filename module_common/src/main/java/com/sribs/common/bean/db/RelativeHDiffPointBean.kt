package com.sribs.common.bean.db


/**
 * create time: 2022/9/8
 * author: bruce
 * description:
 */
class RelativeHDiffPointBean(){

    var name: String? = null
    var colorBg:String=""
    var menu: ArrayList<RelativeHDiffPointBean.Item> = ArrayList()

    class Item(){
        var name: String? = null
        var annotName:String = ""

        override fun toString(): String {
            return "Item(name=$name, annotName=$annotName)"
        }

    }

    override fun toString(): String {
        return "RelativeHDiffPointBean(name=$name, colorBg=$colorBg, menu=$menu)"
    }


}