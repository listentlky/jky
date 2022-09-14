package com.sribs.bdd.bean

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Parcel
import android.os.Parcelable
//import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.sql.Date

/**
* Created by leon on 2022/03/11.
* desc: 损伤描述基类
*/

data class DamageBean(
    var drawingId:Long?,//drawing 表主键
    var dTypeName:String?,//损伤分类名称，如结构构建损伤、耐久性损伤、渗漏损伤等
    var annotRef:Long?,//PDF图纸上该标记的句柄
    var axis:String?,//轴线
    var dmDesc:String?,//损伤详情

    var pohtoPath:String?,//照片路径
    var dDetailType:String?,//普通损伤：common；裂缝：leak
    var leakLength:String?,//裂缝长度
    var leakWidth:String?,//裂缝宽度
    var mntId:String?,//监测点编号

    var mntWay:String?,//监测方法
    var monitorLength:String?,//监测点刻痕长度
    var monitorWidth:String?,//监测点刻痕宽度
    var monitorPhotoPath:String?,//监测点照片路径
    var createTime: Date?= Date(java.util.Date().time),

    var updateTime: Date?= Date(java.util.Date().time),
    var remoteId:String?=null,
    var version:Int?=1,
    var status:Int?=0){

    var id: Int = -1
}