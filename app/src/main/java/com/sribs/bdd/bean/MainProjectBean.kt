package com.sribs.bdd.bean

import org.apache.commons.lang3.math.NumberUtils
import com.sribs.common.bean.net.ProjectListRes

/**
 * @date 2021/6/23
 * @author elijah
 * @Description
 */
data class MainProjectBean(
    var localId:Long=-1,
    var remoteId:String="",
    var updateTimeYMD:String,
    var status: String,
    var address:String,// name+buildNo
    var leader:String,
    var inspector:String
){
    var isCardSel:Boolean = false
    var isMenuChecked:Boolean = false
    var hasNewer:Boolean = false
    var updateTime:String = ""
    var createTime:String = ""
    var name:String? = ""
    var buildNo:String? = ""
    var remoteData:ProjectListRes?=null

    var sortedBuildNo:Int?=0
        get() {
        var no = buildNo?.replace("号楼","")
        return if (NumberUtils.isNumber(no)){
            no?.toInt()
        }else{
            Integer.valueOf((no?.get(0)?:"0").toString())
        }
    }
}
