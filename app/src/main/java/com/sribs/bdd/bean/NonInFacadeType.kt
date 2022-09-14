package com.sribs.bdd.bean

/**
 * @date 2022/3/4
 * @author leon
 * @Description
 */
enum class NonInFacadeType(var value:Int) {
    NON_IN_DRAWING_TYPE_ERROR(-1),

    NON_IN_DRAWING_TYPE_FACADE_EAST(0x01),//东立面图纸
    NON_IN_DRAWING_TYPE_FACADE_WEST(0x02),//西立面图纸
    NON_IN_DRAWING_TYPE_FACADE_SOUTH(0x03),//南立面图纸
    NON_IN_DRAWING_TYPE_FACADE_NORTH(0x04),//北立面图纸
    NON_IN_DRAWING_TYPE_FACADE_OVERALL(0x05),//总平面图纸
    NON_IN_DRAWING_TYPE_FACADE_FLOOR(0x06);//楼层图纸

    companion object {
        fun fromValue(value:Int) = values().firstOrNull { it.value == value }?:NON_IN_DRAWING_TYPE_ERROR
    }
}