package com.sribs.bdd.bean

/**
 * @date 2022/3/4
 * @author leon
 * @Description
 */
data class FloorDrawingsBean(
    /**
     * floorNo，楼层,当type为east,west,south,north中任一个时，floorNo为“”
     */
    var floorNo: String,
    /**
     * type,东西南北四面墙+楼层图纸+总平面图纸, 参考：drawings_type
     */
    var drawingType: String,
    /**
     * 图纸列表
     */
    var drawingList: List<DrawingsBean>?,

)

