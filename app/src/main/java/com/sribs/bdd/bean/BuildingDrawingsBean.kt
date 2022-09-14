package com.sribs.bdd.bean

/**
 * @date 2022/3/4
 * @author leon
 * @Description
 */
data class BuildingDrawingsBean(
    /**
     * ProjectId,项目id
     */
    var projectId: String,
    /**
     * buildingNo,项目内建筑编号，每栋楼一个编号
     */
    var buildingNo: String,
    /**
     * floorNum,楼层
     */
    var floorNo: String,
    /**
     * 图纸列表
     */
    var drawingsList: Array<FloorDrawingsBean>,

)

