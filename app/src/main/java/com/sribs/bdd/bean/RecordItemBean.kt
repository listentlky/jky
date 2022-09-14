package com.sribs.bdd.bean

import com.sribs.bdd.bean.data.DamageEditDataBean
import java.sql.Date

/**
 * @date 2021/7/23
 * @author elijah
 * @Description
 */
data class RecordItemBean(
    var imageUrl:String,
    var description:String,
    var updateTime:Date?=null
){
    var posList:ArrayList<String>?=null
    var picList:ArrayList<String>?=null
    var data:DamageEditDataBean?=null
    override fun toString(): String {
        return "RecordItemBean(imageUrl='$imageUrl', description='$description', posList=$posList, picList=$picList, data=$data)"
    }


}