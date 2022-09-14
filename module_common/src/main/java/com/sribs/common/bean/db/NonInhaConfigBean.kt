package com.sribs.common.bean.db

import java.sql.Date

/**
 * @date 2021/7/8
 * @author elijah
 * @Description
 */
data class NonInhaConfigBean(
    var projectId:Long?,//project 表主键
    var unitId:Long?,
    var configId:Long?=null,
    var floorIdx:Int?=null,//层号
    var neighborIdx:Int?=null,
    var configType:Int?=null,// 底层，标准层
    var floorNum:String?=null,
    var neighborNum:String?=null,
    var corridorNum:String?=null,
    var platformNum:String?=null,
    var corridorConfig:String?=null,
    var platformConfig:String?=null,
    var config1:String?=null,
    var config2:String?=null,
    var unitType:Int?=null,//户型
    var createTime: Date?=null,
    var updateTime: Date?=null,

){
    fun isSame(beanInha: NonInhaConfigBean):Boolean
        = projectId == beanInha.projectId &&
            unitId  == beanInha.unitId    &&
            configId == beanInha.configId &&
            floorIdx == beanInha.floorIdx &&
            neighborIdx == beanInha.neighborIdx &&
            configType == beanInha.configType &&
            floorNum == beanInha.floorNum &&
            neighborNum == beanInha.neighborNum &&
            corridorNum == beanInha.corridorNum &&
            corridorConfig == beanInha.corridorConfig &&
            platformNum == beanInha.platformNum &&
            platformConfig == beanInha.platformConfig &&
            config1 == beanInha.config1 &&
            config2 == beanInha.config2 &&
            unitType == beanInha.unitType

    fun copy():NonInhaConfigBean
        = NonInhaConfigBean(
            this.projectId,
            this.unitId,
            this.configId,
            this.floorIdx,
            this.neighborIdx,
            this.configType,
            this.floorNum,
            this.neighborNum,
            this.corridorNum,
            this.platformNum,
            this.corridorConfig,
            this.platformConfig,
            this.config1,
            this.config2,
            this.unitType,
            this.createTime,
            this.updateTime
        )

}