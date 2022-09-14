package com.sribs.bdd.bean

/**
 * @date 2022/3/4
 * @author leon
 * @Description
 */
data class DrawingsBean(
    /**
     * 图纸名称
     */
    var drawingName: String,
    /**
     * 图纸类型
     */
    var drawingType: String,
    /**
     * 移动端存储位置
     */
    var localPath: String,
    /**
     * 云端存储位置
     */
    var remotePath: String,
)

