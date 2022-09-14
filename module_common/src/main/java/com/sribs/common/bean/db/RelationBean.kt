package com.sribs.common.bean.db

import java.sql.Date

/**
 * @date 2021/7/8
 * @author elijah
 * @Description
 */
data class RelationBean(
    var projectId:Long?,//project 表主键
    var bldId:Long?,//building 表主键
    var drawingId:Long?,//drawing 表主键
    var damageId:Long?,//损伤id，damageBean

){
    fun isSame(projectId:Long?,bldId: Long?,drawingId: Long?,damageId: Long?):Boolean =
        this.projectId == projectId && this.bldId == bldId && this.drawingId == drawingId && this.damageId == damageId


}