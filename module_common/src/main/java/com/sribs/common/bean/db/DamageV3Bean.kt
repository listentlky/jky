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
    var annotName:String="" //标记名


    //梁
    var beamName:String?= ""  //梁名称
    var beamAxisNote:String?="" // 梁-轴线
    var beamAxisNoteList: ArrayList<String>? = ArrayList()//梁-多个轴线
    var beamLeftRealTypeList:ArrayList<String>?=ArrayList() //实测截面类型
    var beamLeftRealParamsList:ArrayList<String>?=ArrayList() //实测截面类型参数
    var beamLeftRealNote:String = "" //实测-备注
    var beamLeftRealPicList:ArrayList<String>?=ArrayList() //实测-手绘草图

    var beamLeftDesignTypeList:ArrayList<String>?=ArrayList() //实测截面类型
    var beamLeftDesignParamsList:ArrayList<String>?=ArrayList() //设计截面类型参数
    var beamLeftDesignNote:String = "" //设计-备注
    var beamLeftDesignPicList:ArrayList<String>?=ArrayList() //设计-手绘草图

    var beamRightRealSectionType : String?="" //实测配筋类型
    var beamRightRealSectionParamsList : ArrayList<String>?=ArrayList() //实测配筋参数
    var beamRightRealStirrupsTypeList: ArrayList<String>?=ArrayList() //实测箍筋参数
    var beamRightRealStirrupsTypeEncryptList: ArrayList<String>? = ArrayList() //实测箍筋-加密区
    var beamRightRealStirrupsTypeNonEncryptList: ArrayList<String>? = ArrayList() //实测箍筋-非加密区
    var beamRightRealProtectList: ArrayList<String>? = ArrayList() //实测保护层厚度
    var beamRightRealNote:String?="" //实测配筋-备注
    var beamRightRealPic :ArrayList<String>?= ArrayList() //实测图

    var beamRightDesignSectionType : String?="" //设计纵筋
    var beamRightDesignSectionTypeParamsList: ArrayList<String>? = ArrayList() //设计纵筋-矩形
    var beamRightDesignStirrupsTypeList: ArrayList<String>? = ArrayList() //设计箍筋
    var beamRightDesignNote: String? = ""//设计配筋-备注
    var beamRightDesignPic :ArrayList<String>?= ArrayList() //设计图
    var beamCheckStatus :ArrayList<String>?= ArrayList() //展示状态


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
    var leftRealSectionType: String? = "" //实测截面类型
    var leftRealSectionTypeParamsList: ArrayList<String>? = ArrayList() //实测截面类型-矩形

    var leftRealNote: String? = "" //实测截面类型-备注
    var columnLeftRealPicList:ArrayList<String>?=ArrayList() //实测-手绘草图

    var leftDesignSectionType: String? = "" //设计截面类型
    var leftDesignSectionTypeParamsList: ArrayList<String>? = ArrayList() //设计截面类型-矩形
    var leftDesignNote: String? = "" //设计截面类型-备注
    var columnLeftDesignPicList:ArrayList<String>?=ArrayList() //设计-手绘草图

    //右侧界面
    var rightRealSectionTypeList: ArrayList<String>? = ArrayList() //实测纵筋
    var rightRealSectionTypeParamsList: ArrayList<String>? = ArrayList() //实测纵筋-矩形
    var rightRealSectionTypeParamsPicList: ArrayList<String>? = ArrayList() //实测纵筋-草图

    var rightRealStirrupsTypeList: ArrayList<String>? = ArrayList() //实测箍筋
    var rightRealStirrupsTypeEncryptList: ArrayList<String>? = ArrayList() //实测箍筋-加密区
    var rightRealStirrupsTypeNonEncryptList: ArrayList<String>? = ArrayList() //实测箍筋-加密区
    var rightRealProtectList: ArrayList<String>? = ArrayList() //实测保护层厚度
    var rightRealNote: String? = ""//实测配筋-备注
    var columnRightRealPic :ArrayList<String>?= ArrayList() //实测图(0 名称  1 路径  2 远端resId)

    var rightDesignSectionTypeList: ArrayList<String>? = ArrayList() //设计纵筋
    var rightDesignSectionTypeParamsList: ArrayList<String>? = ArrayList() //设计纵筋-矩形
    var rightDesignSectionTypeParamsPicList: ArrayList<String>? = ArrayList() //实测纵筋-草图
    var rightDesignStirrupsTypeList: ArrayList<String>? = ArrayList() //设计箍筋
    var rightDesignNote: String? = ""//设计配筋-备注
    var columnRightDesignPic :ArrayList<String>?= ArrayList() //设计图(0 名称  1 路径  2 远端resId)
    var columnCheckStatus :ArrayList<String>?= ArrayList() //勾选展示状态

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
    var realPlateThickness: String? = "" //实测 板厚度
    var designPlateThickness: String? = "" //设计 板厚度
    var plateName: String = "" //板墙名称
    var axisSingleNote: String? = ""//轴线
    var axisPlateNoteList: ArrayList<String>? = ArrayList()//多个轴线
    var realEastWestRebarList: ArrayList<String>? = ArrayList() // 0 实测-东西向钢筋-类型 // 1 实测-东西向钢筋-长度 // 2 实测-东西向钢筋-档数
    var realNorthSouthRebarList: ArrayList<String>? = ArrayList()//0 实测-南北向钢筋-类型 // 1 实测-南北向钢筋-长度 // 2 实测-南北向钢筋-档数
    var realProtectThickness: String? = "" //实测-保护层厚度
    var realNote: String? = "" //实测-备注
    var realPicture: ArrayList<String>? = ArrayList() //实测-照片 0名称 1路径 2远端resID
    var designEastWestRebarList: ArrayList<String>? = ArrayList()//0 设计-东西向钢筋-类型 //1 设计-东西向钢筋-长度
    var designNorthSouthRebarList: ArrayList<String>? = ArrayList() //0 设计-南北向钢筋-类型 // 1 设计-南北向钢筋-长度
    var designNote: String? = "" //设计-备注
    var designPicture:  ArrayList<String>? = ArrayList() //设计-照片 0名称 1路径 2远端resID
    var plateCheckStatus :ArrayList<String>?= ArrayList() //展示状态


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
    //倾斜测量点位
    constructor(id:Long,drawingId:String,type:String?,action:Int?,annotRef:Long,note:String?,createTime:Long,
                guide:String?,guideRotate:Int?,scalePath:ArrayList<String>,pointCount:Int?,pointName:String?,measure1Height:String?,measure2Height:String?,
                tilt1:String?,tilt2:String?,tiltRotate1:Float?,tiltRotate2:Float?):this(){
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

    }

    //相对高差
    var closeDiff:String?="" //闭合差
    var rhdiffInfo:ArrayList<RelativeHDiffInfoBean>?=ArrayList() //顺序测量转点闭合数据
    var pointList:List<RelativeHDiffPointBean>?=ArrayList() //添加的测点数据

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

    override fun toString(): String {
        return "DamageV3Bean(id=$id, drawingId='$drawingId', type=$type, action=$action, annotRef=$annotRef, note=$note, createTime=$createTime, annotX=$annotX, annotY=$annotY, annotName='$annotName', beamName=$beamName, beamAxisNote=$beamAxisNote, beamAxisNoteList=$beamAxisNoteList, beamLeftRealTypeList=$beamLeftRealTypeList, beamLeftRealParamsList=$beamLeftRealParamsList, beamLeftRealNote='$beamLeftRealNote', beamLeftRealPicList=$beamLeftRealPicList, beamLeftDesignTypeList=$beamLeftDesignTypeList, beamLeftDesignParamsList=$beamLeftDesignParamsList, beamLeftDesignNote='$beamLeftDesignNote', beamLeftDesignPicList=$beamLeftDesignPicList, beamRightRealSectionType=$beamRightRealSectionType, beamRightRealSectionParamsList=$beamRightRealSectionParamsList, beamRightRealStirrupsTypeList=$beamRightRealStirrupsTypeList, beamRightRealStirrupsTypeEncryptList=$beamRightRealStirrupsTypeEncryptList, beamRightRealStirrupsTypeNonEncryptList=$beamRightRealStirrupsTypeNonEncryptList, beamRightRealProtectList=$beamRightRealProtectList, beamRightRealNote=$beamRightRealNote, beamRightRealPic=$beamRightRealPic, beamRightDesignSectionType=$beamRightDesignSectionType, beamRightDesignSectionTypeParamsList=$beamRightDesignSectionTypeParamsList, beamRightDesignStirrupsTypeList=$beamRightDesignStirrupsTypeList, beamRightDesignNote=$beamRightDesignNote, beamRightDesignPic=$beamRightDesignPic, , beamCheckStatus=$beamCheckStatus,columnName=$columnName, columnAxisNote=$columnAxisNote, columnAxisNoteList=$columnAxisNoteList, leftRealSectionType=$leftRealSectionType, leftRealSectionTypeParamsList=$leftRealSectionTypeParamsList, leftRealNote=$leftRealNote, columnLeftRealPicList=$columnLeftRealPicList, leftDesignSectionType=$leftDesignSectionType, leftDesignSectionTypeParamsList=$leftDesignSectionTypeParamsList, leftDesignNote=$leftDesignNote, columnLeftDesignPicList=$columnLeftDesignPicList, rightRealSectionTypeList=$rightRealSectionTypeList, rightRealSectionTypeParamsList=$rightRealSectionTypeParamsList, rightRealSectionTypeParamsPicList=$rightRealSectionTypeParamsPicList, rightRealStirrupsTypeList=$rightRealStirrupsTypeList, rightRealStirrupsTypeEncryptList=$rightRealStirrupsTypeEncryptList, rightRealStirrupsTypeNonEncryptList=$rightRealStirrupsTypeNonEncryptList, rightRealProtectList=$rightRealProtectList, rightRealNote=$rightRealNote, columnRightRealPic=$columnRightRealPic, rightDesignSectionTypeList=$rightDesignSectionTypeList, rightDesignSectionTypeParamsList=$rightDesignSectionTypeParamsList, rightDesignSectionTypeParamsPicList=$rightDesignSectionTypeParamsPicList, rightDesignStirrupsTypeList=$rightDesignStirrupsTypeList, rightDesignNote=$rightDesignNote, columnRightDesignPic=$columnRightDesignPic,, columnCheckStatus=$columnCheckStatus,  realPlateThickness=$realPlateThickness, designPlateThickness=$designPlateThickness, plateName='$plateName', axisSingleNote=$axisSingleNote, axisPlateNoteList=$axisPlateNoteList, realEastWestRebarList=$realEastWestRebarList, realNorthSouthRebarList=$realNorthSouthRebarList, realProtectThickness=$realProtectThickness, realNote=$realNote, realPicture=$realPicture, designEastWestRebarList=$designEastWestRebarList, designNorthSouthRebarList=$designNorthSouthRebarList, designNote=$designNote, designPicture=$designPicture, plateCheckStatus=$plateCheckStatus, axisNote=$axisNote, axisNoteList=$axisNoteList, heightType=$heightType, floorName=$floorName, floorDesign=$floorDesign, floorReal=$floorReal, plateDesign=$plateDesign, decorateDesign=$decorateDesign, gridDesign=$gridDesign, gridReal=$gridReal, scalePath=$scalePath, pointName=$pointName, measure1Height=$measure1Height, measure2Height=$measure2Height, guide=$guide, guideRotate=$guideRotate, tilt1=$tilt1, tilt2=$tilt2, tiltRotate1=$tiltRotate1, tiltRotate2=$tiltRotate2, closeDiff=$closeDiff, rhdiffInfo=$rhdiffInfo, pointList=$pointList)"
    }


}
