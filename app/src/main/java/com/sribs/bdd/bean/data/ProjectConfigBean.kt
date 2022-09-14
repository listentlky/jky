package com.sribs.bdd.bean.data

import com.google.gson.Gson

/**
 * @date 2021/7/6
 * @author elijah
 * @Description
 */
class ProjectConfigBean(var configType:Int){

    var projectId:Long = -1

    var bldId:Long = -1

    var unitId:Long = -1

    var configId:Long = -1

    var floorIdx = -1

    var neighborIdx = -1

    var floorNum:String?=null

    var neighborNum:String?=null

    fun toJsonStr(): String = Gson().toJson(this)

    override fun toString(): String {
        return "ProjectConfigBean(configType=$configType, projectId=$projectId, unitId=$unitId, configId=$configId, floorIdx=$floorIdx, neighborIdx=$neighborIdx, floorNum=$floorNum, neighborNum=$neighborNum)"
    }

    companion object{
        fun parseJsonStr(s:String):ProjectConfigBean =  Gson().fromJson(s,ProjectConfigBean::class.java)
    }

    fun copy():ProjectConfigBean
        =ProjectConfigBean(configType).also {
            it.projectId = this.projectId
        it.unitId       = this.unitId
        it.configId     = this.configId
        it.floorIdx     = this.floorIdx
        it.neighborIdx  = this.neighborIdx
        it.floorNum     = this.floorNum
        it.neighborNum  = this.neighborNum
    }

}