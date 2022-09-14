package com.sribs.common.bean.db

import java.sql.Date

/**
 * @date 2021/7/8
 * @author elijah
 * @Description
 */
data class DamageBean(
    var drawingId:Long?,//drawing 表主键
    var dTypeName:String?,//损伤分类名称，如结构构建损伤、耐久性损伤、渗漏损伤等
    var annotRef:Long?,//PDF图纸上该标记的句柄
    var axis:String?,//轴线
    var dmDesc:String?,//损伤详情
    var pohtoPath:String? = "",//照片路径
    var dDetailType:String? = "",//普通损伤：common；裂缝：leak
    var leakLength:String? = "",//裂缝长度
    var leakWidth:String? = "",//裂缝宽度
    var mntId:String? = "",//监测点编号

    var mntWay:String? = "",//监测方法
    var monitorLength:String? = "",//,监测点刻痕长度
    var monitorWidth:String? = "",//监测点刻痕宽度
    var monitorPhotoPath:String? = "",//监测点照片路径
    var createTime: Long?= 0,

    var updateTime: Long?= 0,
    var deleteTime:Long?=null,
    var inspectorName:String?=null,
    var remoteId:String? = "",
    var version:Int? = 1,
    var status:Int?=0,
    var id: Long = -1
){
    fun isSame(drawingId:Long?,annotRef: Long?):Boolean =
        this.drawingId == drawingId && this.annotRef == annotRef

    fun toDescString():String{
        return drawingId.toString().trim()+dTypeName?.trim()+annotRef.toString().trim()+axis?.trim()+dmDesc?.toString()+pohtoPath?.trim()+leakLength?.trim()+leakWidth?.trim()+mntId?.trim()+mntWay?.trim()+monitorLength?.trim()+monitorWidth?.trim()+monitorPhotoPath?.trim()+createTime?.toString()?.trim()+status.toString().trim()
    }
}