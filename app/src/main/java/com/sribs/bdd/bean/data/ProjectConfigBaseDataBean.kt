package com.sribs.bdd.bean.data

import com.sribs.bdd.bean.UnitConfigType

/**
 * @date 2021/7/6
 * @author elijah
 * @Description
 */
abstract class ProjectConfigBaseDataBean: com.sribs.common.bean.BaseDataBean(){
    open val type:UnitConfigType?=null

    var configId = -1L
    var errorMsg:String=""

    open fun setFloor(s:String){}
    open fun setPlatformNumVal(s:String){}
    open fun setCorridorNumVal(s:String){}
    open fun getFloor():String?                = null
    open fun getCorridorNumVal():String?       = null
    open fun getCorridorConfigVal():String?    = null
    open fun getPlatformNumVal():String?       = null
    open fun getPlatformConfigVal():String?    = null
    open fun getConfig1Val():String?           = null
    open fun getConfig2Val():String?           = null
    open fun getNeighborTypeVal():String?      = null

    override fun copy():ProjectConfigBaseDataBean? = null

    open fun hasCorridor():Boolean =
        !getCorridorConfigVal().isNullOrEmpty() || !getCorridorNumVal().isNullOrEmpty()

    open fun hasPlatform():Boolean =
        !getPlatformConfigVal().isNullOrEmpty() || !getPlatformNumVal().isNullOrEmpty()
}