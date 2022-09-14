package com.sribs.common.bean.db

import java.sql.Date

/**
 * @date 2021/7/8
 * @author elijah
 * @Description
 */
data class ConfigBean(
    var projectId:Long?,//project 表主键
    var bldId:Long?,
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
    fun isSame(bean: ConfigBean):Boolean
        = projectId == bean.projectId &&
            unitId  == bean.unitId    &&
            configId == bean.configId &&
            floorIdx == bean.floorIdx &&
            neighborIdx == bean.neighborIdx &&
            configType == bean.configType &&
            floorNum == bean.floorNum &&
            neighborNum == bean.neighborNum &&
            corridorNum == bean.corridorNum &&
            corridorConfig == bean.corridorConfig &&
            platformNum == bean.platformNum &&
            platformConfig == bean.platformConfig &&
            config1 == bean.config1 &&
            config2 == bean.config2 &&
            unitType == bean.unitType

    fun copy():ConfigBean
        = ConfigBean(
            this.projectId,
            this.bldId,
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