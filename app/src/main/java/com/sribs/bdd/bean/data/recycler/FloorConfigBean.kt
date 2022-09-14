package com.sribs.bdd.bean.data.recycler

import com.google.gson.Gson

/**
 * @date 2021/7/6
 * @author elijah
 * @Description
 */
class FloorConfigBean(){

    var projectId:Long = -1

    var bldId:Long = -1

    var unitId:Long = -1

    var configId:Long = -1

    var floorIdx = -1

    var floorNum:String?=null

    var drawingsList: ArrayList<String>? = null

    fun toJsonStr(): String = Gson().toJson(this)

    override fun toString(): String {
        return "ProjectConfigBean(projectId=$projectId, bldId=$bldId, unitId=$unitId, configId=$configId, floorIdx=$floorIdx, drawingsList=$drawingsList, floorNum=$floorNum)"
    }

    companion object{
        fun parseJsonStr(s:String):FloorConfigBean =  Gson().fromJson(s,FloorConfigBean::class.java)
    }

    fun copy():FloorConfigBean
        =FloorConfigBean().also {
            it.projectId = this.projectId
            it.bldId = this.bldId
            it.unitId       = this.unitId
            it.configId     = this.configId
            it.floorIdx     = this.floorIdx
            it.drawingsList  = this.drawingsList
            it.floorNum     = this.floorNum
    }

}