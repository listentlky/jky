package com.sribs.common.bean.db

import java.util.*
import kotlin.collections.ArrayList

/**
 * create time: 2022/9/21
 * author: bruce
 * description:
 */
/**
 *
 */
class DamageV3Bean(){

    var id:Long = -1 //本地ID，楼层损伤就是模块楼层表的id，楼损伤就是模块楼表的id
    var drawingId:Long = -1 // 图纸ID
    var type:String?="" //损伤类型
    var action:Int?=0//pdf标记动作 1 编辑  2 删除
    var annotRef:Long = -1 //annotRef
    var note:String?=""//备注
    var createTime:Long = -1 // 生成时间戳

    //轴网层高
    var axisNote:String?=""//轴线
    var axisNoteList:ArrayList<String>?=ArrayList()//多个轴线
    var heightType:String?=""//净高或总高
    var floorName:String?=""//当前层
    var floorDesign:String?=""//层高设计值
    var floorReal:String?=""//层高实测值
    var plateDesign:String?=""//设计板厚
    var decorateDesign:String?=""//装饰面层厚度
    var gridDesign:String?="" //轴网设计值
    var gridReal:String?="" //轴网实测值

    // 层高构造
    constructor(id:Long,drawingId:Long,type:String?,action:Int?,annotRef:Long,note:String?,createTime:Long,
                axisNote:String?,axisNoteList:ArrayList<String>?,heightType:String?,floorName:String?
    ,floorDesign:String?,floorReal:String?,plateDesign:String,decorateDesign:String?):this(){
        this.id = id
        this.drawingId = drawingId
        this.type = type
        this.action = action
        this.annotRef = annotRef
        this.note = note
        this.createTime = createTime
        this.axisNote = axisNote
        this.axisNoteList = axisNoteList
        this.heightType = heightType
        this.floorName = floorName
        this.floorDesign = floorDesign
        this.floorReal = floorReal
        this.plateDesign = plateDesign
        this.decorateDesign = decorateDesign
    }

    // 轴网构造
    constructor(id:Long,drawingId:Long,type:String?,action:Int?,annotRef:Long,note:String?,createTime:Long,
                axisNote:String?,axisNoteList:ArrayList<String>?,floorName:String?
                , gridDesign:String?,gridReal:String?):this(){
        this.id = id
        this.drawingId = drawingId
        this.type = type
        this.action = action
        this.annotRef = annotRef
        this.note = note
        this.createTime = createTime
        this.axisNote = axisNote
        this.axisNoteList = axisNoteList
        this.floorName = floorName
        this.gridDesign = gridDesign
        this.gridReal = gridReal
    }



    //倾斜测量点位
    var direction:String?="" //方向
    var rotate:Int?=0//角度
    var pointName:String?="" //点位名称
    var measure1Height:String?="" //测量高度1
    var measure2Height:String?="" //测量高度2
    var tilt1:String?="" //倾斜量1
    var tilt2:String?="" //倾斜量2
    //倾斜测量点位
    constructor(id:Long,drawingId:Long,type:String?,action:Int?,annotRef:Long,note:String?,createTime:Long,
                direction:String?,rotate:Int?,pointName:String?,measure1Height:String?,measure2Height:String?,
                tilt1:String?,tilt2:String?):this(){
        this.id = id
        this.drawingId = drawingId
        this.type = type
        this.action = action
        this.annotRef = annotRef
        this.note = note
        this.createTime = createTime
        this.direction = direction
        this.rotate = rotate
        this.pointName = pointName
        this.measure1Height = measure1Height
        this.measure2Height = measure2Height
        this.tilt1 = tilt1
        this.tilt2 = tilt2

    }

    //相对高差
    var rhdiffInfo:ArrayList<RelativeHDiffInfoBean>?=ArrayList() //单条数据
    var pointList:List<RelativeHDiffPointBean>?=ArrayList() //测点数据

    constructor(id:Long,drawingId:Long,type:String?,action:Int?,annotRef:Long,note:String?,createTime:Long,
                rhdiffInfo:ArrayList<RelativeHDiffInfoBean>,pointList:List<RelativeHDiffPointBean>):this(){
        this.id = id
        this.drawingId = drawingId
        this.type = type
        this.action = action
        this.annotRef = annotRef
        this.note = note
        this.createTime = createTime
        this.rhdiffInfo = rhdiffInfo
        this.pointList = pointList

    }

    override fun toString(): String {
        return "DamageV3Bean(id=$id, drawingId=$drawingId, type=$type, action=$action, annotRef=$annotRef, note=$note, createTime=$createTime, axisNote=$axisNote, axisNoteList=$axisNoteList, heightType=$heightType, floorName=$floorName, floorDesign=$floorDesign, floorReal=$floorReal, plateDesign=$plateDesign, decorateDesign=$decorateDesign, gridDesign=$gridDesign, gridReal=$gridReal, direction=$direction, rotate=$rotate, pointName=$pointName, measure1Height=$measure1Height, measure2Height=$measure2Height, tilt1=$tilt1, tilt2=$tilt2, rhdiffInfo=$rhdiffInfo, pointList=$pointList)"
    }


}
