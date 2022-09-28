package com.sribs.bdd.v3.bean

/**
 * create time: 2022/9/27
 * author: bruce
 * description:
 */
class CheckRHDChoosePointBean (){

    var annotRef: Long = -1
    var name: String? = null
    var menu: ArrayList<CheckRHDChoosePointBean.Item> = ArrayList()

    class Item(){
        var name: String? = null

        override fun toString(): String {
            return "Item(name=$name)"
        }
    }

    override fun toString(): String {
        return "CheckRHDChoosePointBean(name=$name, menu=$menu)"
    }


}