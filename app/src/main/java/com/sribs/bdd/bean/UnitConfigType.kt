package com.sribs.bdd.bean

/**
 * @date 2021/7/6
 * @author elijah
 * @Description
 */
enum class UnitConfigType(var value:Int) {
    CONFIG_TYPE_ERROR(-1),

    CONFIG_TYPE_FLOOR_BOTTOM(0x10),
    CONFIG_TYPE_FLOOR_NORMAL(0x20),
    CONFIG_TYPE_FLOOR_TOP(0x30),

    CONFIG_TYPE_UNIT_BOTTOM(0x01),
    CONFIG_TYPE_UNIT_NORMAL(0x02),
    CONFIG_TYPE_UNIT_TOP(0x03);

    companion object {
        fun fromValue(value:Int) = values().firstOrNull { it.value == value }?:CONFIG_TYPE_ERROR
    }
}