package com.sribs.bdd.v3.module

import com.sribs.common.bean.db.DamageV3Bean
import java.io.Serializable

/**
 * create time: 2022/9/5
 * author: bruce
 * description:
 */
class CheckMenuModule : Serializable {

    var name: String? = null
    var menu: ArrayList<Item> = ArrayList()

    class Item : Serializable {
        var name: String? = null
        var item: ArrayList<Mark> = ArrayList()

        class Mark : Serializable {
            var name: String? = null
            var damage: DamageV3Bean?=null

            override fun toString(): String {
                return "Mark(name=$name, damage=$damage)"
            }

        }

        override fun toString(): String {
            return "Item(name=$name, item=$item)"
        }
    }

    override fun toString(): String {
        return "CheckMenuModule(name=$name, menu=$menu)"
    }

}