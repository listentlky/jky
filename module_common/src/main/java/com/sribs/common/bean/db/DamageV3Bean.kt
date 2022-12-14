package com.sribs.common.bean.db

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
    var drawingId:String = "" // 图纸ID
    var type:String?="" //损伤类型
    var action:Int?=0//pdf标记动作 1 编辑  2 删除
    var annotRef:Long = -1 //annotRef
    var note:String?=""//备注
    var createTime:Long = -1 // 生成时间戳
    var annotX:Int=0 //标记X坐标 注意需要使用PDF坐标转换
    var annotY:Int=0 //标记Y坐标
    var annotName:String="" //标记名 mark和损伤关系唯一标识

    //梁
    var beamName:String?= ""  //梁名称
    var beamAxisNote:String?="" // 梁-轴线
    var beamAxisNoteList: ArrayList<String>? = ArrayList()//梁-多个轴线
    //左侧
    var beamLeftRealTypeList:ArrayList<String>?=ArrayList() //实测截面类型
    var beamLeftRealParamsList:ArrayList<String>?=ArrayList() //实测截面类型参数
    var beamLeftRealNote:String = "" //实测-备注
    var beamLeftRealPicList:ArrayList<String>?=ArrayList() //实测-手绘草图 (0 名称  1 路径  2 远端resId)

    var beamLeftDesignTypeList:ArrayList<String>?=ArrayList() //设计截面类型
    var beamLeftDesignParamsList:ArrayList<String>?=ArrayList() //设计截面类型参数
    var beamLeftDesignNote:String = "" //设计-备注
    var beamLeftDesignPicList:ArrayList<String>?=ArrayList() //设计-手绘草图 (0 名称  1 路径  2 远端resId)
    //右测
    var beamRightRealSectionType : String?="" //实测纵筋类型   单排钢筋/双排钢筋
    var beamRightRealSectionParamsList : ArrayList<String>?=ArrayList() //实测钢筋参数
    var beamRightRealStirrupsTypeList: ArrayList<String>?=ArrayList() //实测箍筋参数
    var beamRightRealStirrupsTypeEncryptList: ArrayList<String>? = ArrayList() //实测箍筋-加密区
    var beamRightRealStirrupsTypeNonEncryptList: ArrayList<String>? = ArrayList() //实测箍筋-非加密
    var beamRightRealProtectList: ArrayList<String>? = ArrayList() //实测保护层厚度
    var beamRightRealNote:String?="" //实测配筋-备注
    var beamRightRealPic :ArrayList<String>?= ArrayList() //实测图(0 名称  1 路径  2 远端resId)

    var beamRightDesignSectionType : String?="" //设计纵筋类型
    var beamRightDesignSectionTypeParamsList: ArrayList<String>? = ArrayList() //设计纵筋-参数
    var beamRightDesignStirrupsTypeList: ArrayList<String>? = ArrayList() //设计箍筋
    var beamRightDesignNote: String? = ""//设计配筋-备注
    var beamRightDesignPic :ArrayList<String>?= ArrayList() //设计图   //设计内取消图，该字段废弃
    var beamCheckStatus :ArrayList<String>?= ArrayList() //展示状态  客户端是否可输入状态维护


    constructor(
        id: Long,
        drawingId: String,
        type: String?,
        action: Int?,
        annotRef: Long,
        note: String?,
        createTime: Long,
        beamName: String,
        beamAxisNote:String,
        beamAxisNoteList:ArrayList<String>,
        beamLeftRealTypeList:ArrayList<String>,
        beamLeftRealParamsList:ArrayList<String>,
        beamLeftRealNote:String,
        beamLeftRealPicList:ArrayList<String>,
        beamLeftDesignTypeList:ArrayList<String>,
        beamLeftDesignParamsList:ArrayList<String>,
        beamLeftDesignNote:String,
        beamLeftDesignPicList:ArrayList<String>,
        beamRightRealSectionType : String,
        beamRightRealSectionParamsList : ArrayList<String>,
        beamRightRealStirrupsTypeList: ArrayList<String>,
        beamRightRealStirrupsTypeEncryptList: ArrayList<String>,
        beamRightRealStirrupsTypeNonEncryptList: ArrayList<String>,
        beamRightRealProtectList: ArrayList<String>,
        beamRightRealNote:String,
        beamRightRealPic :ArrayList<String>,
        beamRightDesignSectionType:String,
        beamRightDesignSectionTypeParamsList: ArrayList<String>,
        beamRightDesignStirrupsTypeList: ArrayList<String>,
        beamRightDesignNote: String,
        beamRightDesignPic :ArrayList<String>,
        beamCheckStatus :ArrayList<String>

    ) : this() {
        this.id = id
        this.drawingId = drawingId
        this.type = type
        this.action = action
        this.annotRef = annotRef
        this.note = note
        this.createTime = createTime
        this.beamName = beamName
        this.beamAxisNote = beamAxisNote
        this.beamAxisNoteList =beamAxisNoteList
        this.beamLeftRealTypeList = beamLeftRealTypeList
        this.beamLeftRealParamsList = beamLeftRealParamsList
        this.beamLeftRealNote = beamLeftRealNote
        this.beamLeftRealPicList = beamLeftRealPicList
        this.beamLeftDesignTypeList = beamLeftDesignTypeList
        this.beamLeftDesignParamsList = beamLeftDesignParamsList
        this.beamLeftDesignNote = beamLeftDesignNote
        this.beamLeftDesignPicList = beamLeftDesignPicList
        this.beamRightRealSectionType = beamRightRealSectionType
        this.beamRightRealSectionParamsList = beamRightRealSectionParamsList
        this.beamRightRealStirrupsTypeList = beamRightRealStirrupsTypeList
        this.beamRightRealStirrupsTypeEncryptList =beamRightRealStirrupsTypeEncryptList
        this.beamRightRealStirrupsTypeNonEncryptList = beamRightRealStirrupsTypeNonEncryptList
        this.beamRightRealProtectList = beamRightRealProtectList
        this.beamRightRealNote = beamRightRealNote
        this.beamRightRealPic = beamRightRealPic
        this.beamRightDesignSectionType = beamRightDesignSectionType
        this.beamRightDesignSectionTypeParamsList =beamRightDesignSectionTypeParamsList
        this.beamRightDesignStirrupsTypeList = beamRightDesignStirrupsTypeList
        this.beamRightDesignNote =beamRightDesignNote
        this.beamRightDesignPic = beamRightDesignPic
        this.beamCheckStatus = beamCheckStatus
    }



    //柱
    var columnName: String? = "" //柱名称
    var columnAxisNote: String? = "" // 轴线
    var columnAxisNoteList: ArrayList<String>? = ArrayList()//多个轴线
    //左侧
    var leftRealSectionType: String? = "" //实测截面类型
    var leftRealSectionTypeParamsList: ArrayList<String>? = ArrayList() //实测截面类型参数
    var leftRealNote: String? = "" //实测截面尺寸-备注
    var columnLeftRealPicList:ArrayList<String>?=ArrayList() //实测-手绘草图 (0 名称  1 路径  2 远端resId)

    var leftDesignSectionType: String? = "" //设计截面类型
    var leftDesignSectionTypeParamsList: ArrayList<String>? = ArrayList() //设计截面类型-参数
    var leftDesignNote: String? = "" //设计截面尺寸-备注
    var columnLeftDesignPicList:ArrayList<String>?=ArrayList() //设计-手绘草图 (0 名称  1 路径  2 远端resId)
    //右侧界面
    var rightRealSectionTypeList: ArrayList<String>? = ArrayList() //实测纵筋
    var rightRealSectionTypeParamsList: ArrayList<String>? = ArrayList() //实测纵筋-参数
    var rightRealSectionTypeParamsPicList: ArrayList<String>? = ArrayList() //实测纵筋-草图
    var rightRealStirrupsTypeList: ArrayList<String>? = ArrayList() //实测箍筋
    var rightRealStirrupsTypeEncryptList: ArrayList<String>? = ArrayList() //实测箍筋-加密区
    var rightRealStirrupsTypeNonEncryptList: ArrayList<String>? = ArrayList() //实测箍筋-加密区
    var rightRealProtectList: ArrayList<String>? = ArrayList() //实测保护层厚度
    var rightRealNote: String? = ""//实测配筋-备注
    var columnRightRealPic :ArrayList<String>?= ArrayList() //实测图(0 名称  1 路径  2 远端resId)

    var rightDesignSectionTypeList: ArrayList<String>? = ArrayList() //设计纵筋
    var rightDesignSectionTypeParamsList: ArrayList<String>? = ArrayList() //设计纵筋-参数
    var rightDesignSectionTypeParamsPicList: ArrayList<String>? = ArrayList() //实测纵筋-草图 (0 名称  1 路径  2 远端resId)
    var rightDesignStirrupsTypeList: ArrayList<String>? = ArrayList() //设计箍筋
    var rightDesignNote: String? = ""//设计配筋-备注
    var columnRightDesignPic :ArrayList<String>?= ArrayList() //设计图  //设计内取消图，该字段废弃
    var columnCheckStatus :ArrayList<String>?= ArrayList() //勾选展示状态 客户端是否可输入状态维护

    constructor(
        id: Long,
        drawingId: String,
        type: String?,
        action: Int?,
        annotRef: Long,
        note: String?,
        createTime: Long,
        columnName: String,
        columnAxisNote: String,
        columnAxisNoteList: ArrayList<String>,
        leftRealSectionType: String,
        leftRealSectionTypeParamsList: ArrayList<String>,
        leftRealNote: String,
        columnLeftRealPicList:ArrayList<String>,
        leftDesignSectionType: String,
        leftDesignSectionTypeParamsList: ArrayList<String>,
        leftDesignNote: String,
        columnLeftDesignPicList:ArrayList<String>,
        rightRealSectionTypeList: ArrayList<String>,
        rightRealSectionTypeParamsList: ArrayList<String>,
        rightRealSectionTypeParamsPicList: ArrayList<String>,
        rightRealStirrupsTypeList: ArrayList<String>,
        rightRealStirrupsTypeEncryptList: ArrayList<String>,
        rightRealStirrupsTypeNonEncryptList: ArrayList<String>,
        rightRealProtectList: ArrayList<String>,
        rightRealNote: String,
        columnRightRealPic: ArrayList<String>,
        rightDesignSectionTypeList: ArrayList<String>,
        rightDesignSectionTypeParamsList: ArrayList<String>,
        rightDesignSectionTypeParamsPicList: ArrayList<String>,
        rightDesignStirrupsTypeList: ArrayList<String>,
        rightDesignNote: String,
        columnRightDesignPic: ArrayList<String>,
        columnCheckStatus: ArrayList<String>
    ) : this() {
        this.id = id
        this.drawingId = drawingId
        this.type = type
        this.action = action
        this.annotRef = annotRef
        this.note = note
        this.createTime = createTime
        this.columnName = columnName
        this.columnAxisNote = columnAxisNote
        this.columnAxisNoteList = columnAxisNoteList
        this.leftRealSectionType = leftRealSectionType
        this.leftRealSectionTypeParamsList = leftRealSectionTypeParamsList
        this.leftRealNote = leftRealNote
        this.columnLeftRealPicList = columnLeftRealPicList
        this.leftDesignSectionType = leftDesignSectionType
        this.leftDesignSectionTypeParamsList = leftDesignSectionTypeParamsList
        this.leftDesignNote = leftDesignNote
        this.columnLeftDesignPicList =columnLeftDesignPicList
        this.rightRealSectionTypeList = rightRealSectionTypeList
        this.rightRealSectionTypeParamsList = rightRealSectionTypeParamsList
        this.rightRealSectionTypeParamsPicList = rightRealSectionTypeParamsPicList
        this.rightRealStirrupsTypeList = rightRealStirrupsTypeList
        this.rightRealStirrupsTypeEncryptList = rightRealStirrupsTypeEncryptList
        this.rightRealStirrupsTypeNonEncryptList = rightRealStirrupsTypeNonEncryptList
        this.rightRealProtectList = rightRealProtectList
        this.rightRealNote = rightRealNote
        this.columnRightRealPic = columnRightRealPic
        this.rightDesignSectionTypeList = rightDesignSectionTypeList
        this.rightDesignSectionTypeParamsList = rightDesignSectionTypeParamsList
        this.rightDesignSectionTypeParamsPicList = rightDesignSectionTypeParamsPicList
        this.rightDesignStirrupsTypeList = rightDesignStirrupsTypeList
        this.rightDesignNote = rightDesignNote
        this.columnRightDesignPic = columnRightDesignPic
        this.columnCheckStatus = columnCheckStatus

    }

    //构件检测-板 墙 通用
    var plateName: String = "" //板/墙名称
    var axisSingleNote: String? = ""//板/墙轴线
    var axisPlateNoteList: ArrayList<String>? = ArrayList()//板/墙多个轴线
    //左侧
    var realPlateThickness: String? = "" //实测 板/墙厚度
    var designPlateThickness: String? = "" //设计 板/墙厚度
    //右侧
    var realEastWestRebarList: ArrayList<String>? = ArrayList() //  板 实测板底东西向钢筋 / 墙 实测竖向钢筋
    var realNorthSouthRebarList: ArrayList<String>? = ArrayList()// 板 实测板底南北向钢筋 / 墙 实测水平钢筋
    var realProtectThickness: String? = "" // 板/墙 实测-保护层厚度
    var realNote: String? = "" //实测-备注
    var realPicture: ArrayList<String>? = ArrayList() //实测-照片 0名称 1路径 2远端resID
    var designEastWestRebarList: ArrayList<String>? = ArrayList()// 板 设计板底东西向钢筋 / 墙 设计竖向钢筋
    var designNorthSouthRebarList: ArrayList<String>? = ArrayList() // 板 设计板底南北向钢筋 / 墙 设计水平钢筋
    var designNote: String? = "" //设计-备注
    var designPicture:  ArrayList<String>? = ArrayList() //设计内取消图，该字段废弃
    var plateCheckStatus :ArrayList<String>?= ArrayList() //勾选展示状态 客户端是否可输入状态维护

    //构件检测-板/墙
    constructor(
        id: Long,
        drawingId: String,
        type: String?,
        action: Int?,
        annotRef: Long,
        note: String?,
        createTime: Long,
        realPlateThickness: String,
        designPlateThickness: String,
        plateName: String,
        axisSingleNote: String,
        axisPlateNoteList: ArrayList<String>,
        realEastWestRebarList:ArrayList<String>,
        realNorthSouthRebarList:ArrayList<String>,
        realProtectThickness: String,
        realNote: String,
        realPicture: ArrayList<String>,
        designEastWestRebarList:ArrayList<String>,
        designNorthSouthRebarList:ArrayList<String>,
        designNote: String,
        designPicture: ArrayList<String>,
        plateCheckStatus: ArrayList<String>
    ) : this() {
        this.id = id
        this.drawingId = drawingId
        this.type = type
        this.action = action
        this.annotRef = annotRef
        this.note = note
        this.createTime = createTime
        this.realPlateThickness = realPlateThickness
        this.designPlateThickness = designPlateThickness
        this.plateName = plateName
        this.axisSingleNote = axisSingleNote
        this.axisPlateNoteList = axisPlateNoteList
        this.realEastWestRebarList = realEastWestRebarList
        this.realNorthSouthRebarList = realNorthSouthRebarList
        this.realProtectThickness = realProtectThickness
        this.realNote = realNote
        this.realPicture = realPicture
        this.designEastWestRebarList = designEastWestRebarList
        this.designNorthSouthRebarList = designNorthSouthRebarList
        this.designNote = designNote
        this.designPicture = designPicture
        this.plateCheckStatus = plateCheckStatus
    }


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
    constructor(id:Long,drawingId:String,type:String?,action:Int?,annotRef:Long,note:String?,createTime:Long,annotX:Int,annotY:Int,
                axisNote:String?,axisNoteList:ArrayList<String>?,heightType:String?,floorName:String?
    ,floorDesign:String?,floorReal:String?,plateDesign:String,decorateDesign:String?):this(){
        this.id = id
        this.drawingId = drawingId
        this.type = type
        this.action = action
        this.annotRef = annotRef
        this.note = note
        this.createTime = createTime
        this.annotX = annotX
        this.annotY = annotY
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
    constructor(id:Long,drawingId:String,type:String?,action:Int?,annotRef:Long,note:String?,createTime:Long,annotX:Int,annotY:Int,
                axisNote:String?,axisNoteList:ArrayList<String>?,floorName:String?
                , gridDesign:String?,gridReal:String?):this(){
        this.id = id
        this.drawingId = drawingId
        this.type = type
        this.action = action
        this.annotRef = annotRef
        this.note = note
        this.createTime = createTime
        this.annotX = annotX
        this.annotY = annotY
        this.axisNote = axisNote
        this.axisNoteList = axisNoteList
        this.floorName = floorName
        this.gridDesign = gridDesign
        this.gridReal = gridReal
    }



    //倾斜测量点位
    var scalePath:ArrayList<String>?= ArrayList()//放大图
    var pointCount:Int?=1 //单项1 双项2
    var pointName:String?="" //点位名称
    var measure1Height:String?="" //测量高度1
    var measure2Height:String?="" //测量高度2
    var guide:String?=""//方向
    var guideRotate:Int?=0//角度
    var tilt1:String?="" //倾斜量1
    var tilt2:String?="" //倾斜量2
    var tiltRotate1:Float?=0f //倾斜角度1
    var tiltRotate2:Float?=90f //倾斜角度2
    var tiltDirection1:String?=""//倾斜方向1
    var tiltDirection2:String?="" // 倾斜方向2
    var slope1:String?="" //倾斜率1
    var slope2:String?="" // 倾斜率2
    //倾斜测量点位
    constructor(id:Long,drawingId:String,type:String?,action:Int?,annotRef:Long,note:String?,createTime:Long,
                guide:String?,guideRotate:Int?,scalePath:ArrayList<String>,pointCount:Int?,pointName:String?,measure1Height:String?,measure2Height:String?,
                tilt1:String?,tilt2:String?,tiltRotate1:Float?,tiltRotate2:Float?,tiltDirection1:String,tiltDirection2:String,slope1:String,slope2:String):this(){
        this.id = id
        this.drawingId = drawingId
        this.type = type
        this.action = action
        this.annotRef = annotRef
        this.note = note
        this.createTime = createTime
        this.guide = guide
        this.guideRotate = guideRotate
        this.pointCount = pointCount
        this.scalePath = scalePath
        this.pointName = pointName
        this.measure1Height = measure1Height
        this.measure2Height = measure2Height
        this.tilt1 = tilt1
        this.tilt2 = tilt2
        this.tiltRotate1 = tiltRotate1
        this.tiltRotate2 = tiltRotate2
        this.tiltDirection1 = tiltDirection1
        this.tiltDirection2 = tiltDirection2
        this.slope1 = slope1
        this.slope2 = slope2
    }

    //相对高差
    var closeDiff:String?="" //闭合差
    var rhdiffInfo:ArrayList<RelativeHDiffInfoBean>?=ArrayList() //顺序测量转点闭合数据
    //示例
    /**
     * [RelativeHDiffInfoBean(tag=BM1, after=1, before=, abDiff=, height=2, pointDiff=), RelativeHDiffInfoBean(tag=111-222, after=, before=3, abDiff=-2, height=0, pointDiff=-2), RelativeHDiffInfoBean(tag=TP1, after=4, before=5, abDiff=3, height=-2, pointDiff=-4), RelativeHDiffInfoBean(tag=333-444, after=, before=6, abDiff=-2, height=-4, pointDiff=-6), RelativeHDiffInfoBean(tag=闭合点, after=, before=7, abDiff=-3, height=-5, pointDiff=-7)]
     */

    var pointList:List<RelativeHDiffPointBean>?=ArrayList() //添加的测点数据
    //示例
    /**
     * [RelativeHDiffPointBean(name=闭合点, colorBg=, menu=[]), RelativeHDiffPointBean(name=333, colorBg=#ff5fe9c1, menu=[Item(name=444, annotName=1667364954198)]), RelativeHDiffPointBean(name=111, colorBg=#ff4f8692, menu=[Item(name=222, annotName=1667364951032)])]
     */

    constructor(id:Long,drawingId:String,type:String?,action:Int?,annotRef:Long,note:String?,createTime:Long, closeDiff:String,
                rhdiffInfo:ArrayList<RelativeHDiffInfoBean>,pointList:List<RelativeHDiffPointBean>):this(){
        this.id = id
        this.drawingId = drawingId
        this.type = type
        this.action = action
        this.annotRef = annotRef
        this.note = note
        this.createTime = createTime
        this.closeDiff = closeDiff
        this.rhdiffInfo = rhdiffInfo
        this.pointList = pointList

    }

    //非居民类测量
    var noResAxisNote:String?=""//轴线
    var noResNote:String?=""//备注
    var noResDamagePicList :ArrayList<String>?= ArrayList() // 照片
    var noResCrackBox:Boolean?= false //裂缝信息是否勾选
    var noResCrackWidth:String?="" //裂缝宽度
    var noResCrackHeight:String?="" //裂缝长度
    var noResCrackPointBox:Boolean?= false //裂缝监测点信息是否勾选
    var noResCrackPointId:String?="" //裂缝监测点编号
    var noResCrackPointMethod:String?="" //裂缝监测点方法  0/石膏饼 1/刻痕
    var noResCrackPointMethodIndex:Int?=0
    var noResCrackPointNickHeight:String?="" //裂缝监测点刻痕长度
    var noResCrackPointNickWidth:String?="" //裂缝监测点刻痕宽度
    var noResCrackPointPicList :ArrayList<String>?= ArrayList() // 裂缝监测点照片

    constructor(id:Long,drawingId:String,type:String?,createTime:Long,noResAxisNote:String,noResNote:String,noResDamagePicList :ArrayList<String>,noResCrackBox:Boolean,
                noResCrackWidth:String,noResCrackHeight:String,noResCrackPointBox:Boolean,noResCrackPointId:String,noResCrackPointMethod:String,
                noResCrackPointMethodIndex:Int,noResCrackPointNickHeight:String,noResCrackPointNickWidth:String,noResCrackPointPicList:ArrayList<String>):this(){
        this.id = id
        this.drawingId = drawingId
        this.type = type
        this.createTime = createTime
        this.noResAxisNote = noResAxisNote
        this.noResNote = noResNote
        this.noResDamagePicList = noResDamagePicList
        this.noResCrackBox = noResCrackBox
        this.noResCrackWidth = noResCrackWidth
        this.noResCrackHeight = noResCrackHeight
        this.noResCrackPointBox = noResCrackPointBox
        this.noResCrackPointId = noResCrackPointId
        this.noResCrackPointMethod = noResCrackPointMethod
        this.noResCrackPointMethodIndex = noResCrackPointMethodIndex
        this.noResCrackPointNickHeight = noResCrackPointNickHeight
        this.noResCrackPointNickWidth = noResCrackPointNickWidth
        this.noResCrackPointPicList = noResCrackPointPicList
    }

    override fun toString(): String {
        return "DamageV3Bean(id=$id, drawingId='$drawingId', type=$type, action=$action, annotRef=$annotRef, note=$note, createTime=$createTime, annotX=$annotX, annotY=$annotY, annotName='$annotName', beamName=$beamName, beamAxisNote=$beamAxisNote, beamAxisNoteList=$beamAxisNoteList, beamLeftRealTypeList=$beamLeftRealTypeList, beamLeftRealParamsList=$beamLeftRealParamsList, beamLeftRealNote='$beamLeftRealNote', beamLeftRealPicList=$beamLeftRealPicList, beamLeftDesignTypeList=$beamLeftDesignTypeList, beamLeftDesignParamsList=$beamLeftDesignParamsList, beamLeftDesignNote='$beamLeftDesignNote', beamLeftDesignPicList=$beamLeftDesignPicList, beamRightRealSectionType=$beamRightRealSectionType, beamRightRealSectionParamsList=$beamRightRealSectionParamsList, beamRightRealStirrupsTypeList=$beamRightRealStirrupsTypeList, beamRightRealStirrupsTypeEncryptList=$beamRightRealStirrupsTypeEncryptList, beamRightRealStirrupsTypeNonEncryptList=$beamRightRealStirrupsTypeNonEncryptList, beamRightRealProtectList=$beamRightRealProtectList, beamRightRealNote=$beamRightRealNote, beamRightRealPic=$beamRightRealPic, beamRightDesignSectionType=$beamRightDesignSectionType, beamRightDesignSectionTypeParamsList=$beamRightDesignSectionTypeParamsList, beamRightDesignStirrupsTypeList=$beamRightDesignStirrupsTypeList, beamRightDesignNote=$beamRightDesignNote, beamRightDesignPic=$beamRightDesignPic, beamCheckStatus=$beamCheckStatus, columnName=$columnName, columnAxisNote=$columnAxisNote, columnAxisNoteList=$columnAxisNoteList, leftRealSectionType=$leftRealSectionType, leftRealSectionTypeParamsList=$leftRealSectionTypeParamsList, leftRealNote=$leftRealNote, columnLeftRealPicList=$columnLeftRealPicList, leftDesignSectionType=$leftDesignSectionType, leftDesignSectionTypeParamsList=$leftDesignSectionTypeParamsList, leftDesignNote=$leftDesignNote, columnLeftDesignPicList=$columnLeftDesignPicList, rightRealSectionTypeList=$rightRealSectionTypeList, rightRealSectionTypeParamsList=$rightRealSectionTypeParamsList, rightRealSectionTypeParamsPicList=$rightRealSectionTypeParamsPicList, rightRealStirrupsTypeList=$rightRealStirrupsTypeList, rightRealStirrupsTypeEncryptList=$rightRealStirrupsTypeEncryptList, rightRealStirrupsTypeNonEncryptList=$rightRealStirrupsTypeNonEncryptList, rightRealProtectList=$rightRealProtectList, rightRealNote=$rightRealNote, columnRightRealPic=$columnRightRealPic, rightDesignSectionTypeList=$rightDesignSectionTypeList, rightDesignSectionTypeParamsList=$rightDesignSectionTypeParamsList, rightDesignSectionTypeParamsPicList=$rightDesignSectionTypeParamsPicList, rightDesignStirrupsTypeList=$rightDesignStirrupsTypeList, rightDesignNote=$rightDesignNote, columnRightDesignPic=$columnRightDesignPic, columnCheckStatus=$columnCheckStatus, plateName='$plateName', axisSingleNote=$axisSingleNote, axisPlateNoteList=$axisPlateNoteList, realPlateThickness=$realPlateThickness, designPlateThickness=$designPlateThickness, realEastWestRebarList=$realEastWestRebarList, realNorthSouthRebarList=$realNorthSouthRebarList, realProtectThickness=$realProtectThickness, realNote=$realNote, realPicture=$realPicture, designEastWestRebarList=$designEastWestRebarList, designNorthSouthRebarList=$designNorthSouthRebarList, designNote=$designNote, designPicture=$designPicture, plateCheckStatus=$plateCheckStatus, axisNote=$axisNote, axisNoteList=$axisNoteList, heightType=$heightType, floorName=$floorName, floorDesign=$floorDesign, floorReal=$floorReal, plateDesign=$plateDesign, decorateDesign=$decorateDesign, gridDesign=$gridDesign, gridReal=$gridReal, scalePath=$scalePath, pointCount=$pointCount, pointName=$pointName, measure1Height=$measure1Height, measure2Height=$measure2Height, guide=$guide, guideRotate=$guideRotate, tilt1=$tilt1, tilt2=$tilt2, tiltRotate1=$tiltRotate1, tiltRotate2=$tiltRotate2, tiltDirection1=$tiltDirection1, tiltDirection2=$tiltDirection2, slope1=$slope1, slope2=$slope2, closeDiff=$closeDiff, rhdiffInfo=$rhdiffInfo, pointList=$pointList, noResAxisNote=$noResAxisNote, noResNote=$noResNote, noResDamagePicList=$noResDamagePicList, noResCrackBox=$noResCrackBox, noResCrackWidth=$noResCrackWidth, noResCrackHeight=$noResCrackHeight, noResCrackPointBox=$noResCrackPointBox, noResCrackPointId=$noResCrackPointId, noResCrackPointMethod=$noResCrackPointMethod, noResCrackPointMethodIndex=$noResCrackPointMethodIndex, noResCrackPointNickWidth=$noResCrackPointNickWidth, noResCrackPointNickHeight=$noResCrackPointNickHeight, noResCrackPointPicList=$noResCrackPointPicList)"
    }

}
