package com.sribs.bdd.bean

/**
 * @date 2021/7/14
 * @author elijah
 * @Description
 */
data class HouseConfigItemBean(

    var name:String,
    var isFinish:Boolean
){
    var projectId:Long? = -1
    var unitId:Long? = -1
    var configId:Long? = -1
    var roomStatusId:Long?=-1
}
