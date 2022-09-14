package com.sribs.bdd.utils

/**
 * @date 2021/7/16
 * @author elijah
 * @Description
 */
object DescriptionPositionHelper {


    val roomSortMap = mapOf<String,Int?>(
        "厨房" to 0,
        "卫生间" to 1,
        "主卫" to 2,
        "客卫" to 3,
        "盥洗室" to 4,
        "餐厅" to 5,
        "客厅" to 6,
        "起居室" to 7,
        "储物间" to 8,
        "玄关" to 9,
        "过道" to 10,
        "卧室" to 11,
        "东卧室" to 12,
        "南卧室" to 13,
        "西卧室" to 14,
        "北卧室" to 15,
        "东南卧室" to 16,
        "西南卧室" to 17,
        "西北卧室" to 18,
        "阳台" to 19,
        "南阳台" to 20,
        "北阳台" to 21,
        "露台" to 22,
        "南庭院" to 23,
        "北庭院" to 24,
    )






    /**
     * @Description 顶板
     */
    val roof = mapOf<String,Any?>(
        "裂缝" to null,
        "接缝" to null,
        "渗水" to listOf("渗水","大面积渗水","有渗水痕迹","有大面积渗水痕迹","管道连接处渗水","管道连接处有渗水痕迹"),
        "水渍" to listOf("有水渍","有大面积水渍"),
        "发霉" to listOf("发霉","大面积发霉"),
        "霉渍" to listOf("有霉渍","有大面积霉渍"),
        "粉刷" to listOf("局部龟裂","大面积龟裂","局部空鼓","大面积空鼓","局部脱落","大面积脱落"),
        "涂料" to listOf("局部起皮","大面积起皮","局部起皮、脱落","大面积起皮、脱落","局部起皮、脱落，有渗水痕迹","大面积起皮、脱落，有渗水痕迹"),
        "吊顶破损" to null,
    )

    val roof2 =  mapOf<String,Any?>(
        "已吊顶" to null,
        "吊顶破损" to null,
        "贴墙纸" to null,
        "水渍" to listOf("有水渍","有大面积水渍"),
        "渗水" to listOf("渗水","大面积渗水","有渗水痕迹","有大面积渗水痕迹","管道连接处渗水","管道连接处有渗水痕迹"),
        "发霉" to listOf("发霉","大面积发霉"),
        "霉渍" to listOf("有霉渍","有大面积霉渍"),
        "裂缝" to null,
        "接缝" to null,
        "涂料" to listOf("局部起皮","大面积起皮","局部起皮、脱落","大面积起皮、脱落","局部起皮、脱落，有渗水痕迹","大面积起皮、脱落，有渗水痕迹"),
        "粉刷" to listOf("局部龟裂","大面积龟裂","局部空鼓","大面积空鼓","局部脱落","大面积脱落"),
    )

    val roof3 = mapOf<String,Any?>(
        "已吊顶" to null,
        "吊顶破损" to null,
        "水渍" to listOf("有水渍","有大面积水渍"),
        "渗水" to listOf("渗水","大面积渗水","有渗水痕迹","有大面积渗水痕迹","管道连接处渗水","管道连接处有渗水痕迹"),
        "发霉" to listOf("发霉","大面积发霉"),
        "霉渍" to listOf("有霉渍","有大面积霉渍"),
        "裂缝" to null,
        "接缝" to null,
        "涂料" to listOf("局部起皮","大面积起皮","局部起皮、脱落","大面积起皮、脱落","局部起皮、脱落，有渗水痕迹","大面积起皮、脱落，有渗水痕迹"),
        "粉刷" to listOf("局部龟裂","大面积龟裂","局部空鼓","大面积空鼓","局部脱落","大面积脱落"),
    )

    val roof4 = mapOf<String,Any?>(
        "裂缝" to null,
        "接缝" to null,
        "涂料" to listOf("局部起皮","大面积起皮","局部起皮、脱落","大面积起皮、脱落","局部起皮、脱落，有渗水痕迹","大面积起皮、脱落，有渗水痕迹"),
        "粉刷" to listOf("局部龟裂","大面积龟裂","局部空鼓","大面积空鼓","局部脱落","大面积脱落"),
    )



    /**
     * @Description 地坪
     */
    val floor = mapOf<String,Any?>(

        "裂缝" to null,
        "龟裂" to listOf("找平层龟裂","找平层龟裂普遍"),
        "接缝" to null,
        "地砖" to listOf("地砖开裂","地砖开裂普遍"),
        "木地板" to listOf("局部翘曲","不平整","局部缺失","局部松动","潮湿霉变"),
    )

    val floor2 = mapOf<String,Any?>(
        "地砖" to listOf("瓷砖开裂普遍"),
        "裂缝" to listOf("找平层龟裂普遍"),
        "木地板" to listOf("局部翘曲","不平整","局部缺失","局部松动","潮湿霉变"),
        "接缝" to null,
    )

    val floor3 = mapOf<String,Any?>(
        "裂缝" to null,
        "龟裂" to listOf("找平层龟裂","找平层龟裂普遍"),
        "接缝" to null,
        "地砖" to listOf("地砖开裂","地砖开裂普遍"),
        "木地板" to listOf("局部翘曲","不平整","局部缺失","局部松动","潮湿霉变"),
        "下沉" to listOf("与房屋主体脱开","与围墙脱开")
    )

    /**
     * @Description 墙
     */
    val wall = mapOf<String,List<String>?>(
        "裂缝" to listOf("无窗","有窗","有门"),
        "接缝" to listOf("水平接缝","竖向接缝","门洞封堵接缝","窗洞封堵接缝","气窗缝"),
        "渗水" to listOf("渗水","大面积渗水","有渗水痕迹","有大面积渗水痕迹"),
        "水渍" to listOf("有水渍","墙面有大面积水渍"),
        "发霉" to listOf("发霉","大面积发霉"),
        "霉渍" to listOf("有霉渍","有大面积霉渍"),
        "粉刷" to listOf("局部龟裂","大面积龟裂","局部空鼓","大面积空鼓","局部脱落","大面积脱落"),

        "涂料" to listOf("局部起皮","大面积起皮","局部起皮、脱落","大面积起皮、脱落","局部起皮、脱落，有渗水痕迹","大面积起皮、脱落，有渗水痕迹"),

        "瓷砖" to listOf("开裂","局部/大面积空鼓","局部/大面积脱落"),
        "墙纸" to listOf("潮湿发霉","局部破损"),
        "护墙板" to listOf("潮湿发霉","局部破损"),
        "顶角线" to listOf("开裂","与墙面脱开"),
        "踢脚线" to  listOf("开裂","与墙面脱开"),
    )
    val wall1 = mapOf<String,List<String>?>(
        "裂缝" to listOf("无窗","有窗","有门"),
        "瓷砖" to listOf("墙面瓷砖开裂","局部/大面积空鼓","局部/大面积脱落"),
        "涂料" to listOf("局部起皮","大面积起皮","局部起皮、脱落","大面积起皮、脱落","局部起皮、脱落，有渗水痕迹","大面积起皮、脱落，有渗水痕迹"),
        "粉刷" to listOf("局部龟裂","大面积龟裂","局部空鼓","大面积空鼓","局部脱落","大面积脱落"),
        "水渍" to listOf("有水渍","墙面有大面积水渍"),
        "渗水" to listOf("渗水","有渗水痕迹","大面积渗水"),
        "发霉" to listOf("发霉","大面积发霉"),
        "霉渍" to listOf("有霉渍","有大面积霉渍")
    )


    val wall2 = mapOf<String,List<String>?>(
        "接缝" to listOf("水平接缝","竖向接缝","门洞封堵接缝","窗洞封堵接缝","气窗缝"),
        "裂缝" to listOf("无窗","有窗","有门"),
        "瓷砖" to listOf("墙面瓷砖开裂","局部/大面积空鼓","局部/大面积脱落"),
        "涂料" to listOf("局部起皮","大面积起皮","局部起皮、脱落","大面积起皮、脱落","局部起皮、脱落，有渗水痕迹","大面积起皮、脱落，有渗水痕迹"),
        "粉刷" to listOf("局部龟裂","大面积龟裂","局部空鼓","大面积空鼓","局部脱落","大面积脱落"),
        "顶角线" to listOf("开裂","与墙面脱开","其他损伤"),
        "踢脚线" to  listOf("开裂","与墙面脱开","其他损伤"),
        "水渍" to listOf("有水渍","墙面有大面积水渍"),
        "渗水" to listOf("渗水","有渗水痕迹","大面积渗水"),
        "发霉" to listOf("发霉","大面积发霉"),
        "霉渍" to listOf("有霉渍","有大面积霉渍")
    )

    val wall3 =  mapOf<String,List<String>?>(
        "裂缝" to listOf("墙面竖向裂缝","墙面水平裂缝","斜裂缝"),
        "瓷砖" to listOf("墙面瓷砖开裂","局部/大面积空鼓","局部/大面积脱落"),
        "涂料" to listOf("局部起皮","大面积起皮","局部起皮、脱落","大面积起皮、脱落","局部起皮、脱落，有渗水痕迹","大面积起皮、脱落，有渗水痕迹"),
        "粉刷" to listOf("局部龟裂","大面积龟裂","局部空鼓","大面积空鼓","局部脱落","大面积脱落"),
        "水渍" to listOf("有水渍","墙面有大面积水渍"),
        "渗水" to listOf("渗水","有渗水痕迹","大面积渗水"),
        "发霉" to listOf("发霉","大面积发霉"),
        "霉渍" to listOf("有霉渍","有大面积霉渍")
    )

    val wall4 =  mapOf<String,List<String>?>(
        "接缝" to listOf("水平接缝","竖向接缝","门洞封堵接缝","窗洞封堵接缝","气窗缝"),
        "裂缝" to null,
        "顶角线" to listOf("开裂","与墙面脱开","其他损伤"),
        "踢脚线" to  listOf("开裂","与墙面脱开","其他损伤"),
        "瓷砖" to listOf("墙面瓷砖开裂","局部/大面积空鼓","局部/大面积脱落"),
        "涂料" to listOf("局部起皮","大面积起皮","局部起皮、脱落","大面积起皮、脱落","局部起皮、脱落，有渗水痕迹","大面积起皮、脱落，有渗水痕迹"),
        "粉刷" to listOf("局部龟裂","大面积龟裂","局部空鼓","大面积空鼓","局部脱落","大面积脱落"),
        "水渍" to listOf("有水渍","墙面有大面积水渍"),
        "渗水" to listOf("渗水","有渗水痕迹","大面积渗水"),
        "发霉" to listOf("发霉","大面积发霉"),
        "霉渍" to listOf("有霉渍","有大面积霉渍")
    )

    val wall5 =  mapOf<String,List<String>?>(
        "裂缝" to null,
        "接缝" to listOf("水平接缝","竖向接缝","门洞封堵接缝","窗洞封堵接缝","气窗缝"),
        "渗水" to listOf("渗水","有渗水痕迹","大面积渗水"),
        "水渍" to listOf("有水渍","有大面积水渍"),
        "发霉" to listOf("发霉","大面积发霉"),
        "霉渍" to listOf("有霉渍","有大面积霉渍"),
        "粉刷" to listOf("局部龟裂","大面积龟裂","局部空鼓","大面积空鼓","局部脱落","大面积脱落"),
        "涂料" to listOf("局部起皮","大面积起皮","局部起皮、脱落","大面积起皮、脱落","局部起皮、脱落，有渗水痕迹","大面积起皮、脱落，有渗水痕迹"),
        "瓷砖" to listOf("墙面瓷砖开裂","局部/大面积空鼓","局部/大面积脱落")
    )

    /**
     * @Description 橱柜
     */
    val cupboard = mapOf<String,List<String>?>(
        "变形" to null,
        "局部破损" to null,
    )
    /**
     * @Description 灶台
     */
    val cooktop = mapOf<String,List<String>?>(
        "台面开裂" to null,
        "与墙面脱开" to null
    )
    /**
     * @Description 洁具
     */
    val clean = mapOf<String,List<String>?>(
        "洗漱台裂开" to null,
        "洗漱台与墙面脱开" to null,
        "马桶破损" to null,
        "淋浴房玻璃破损" to null,
    )
    /**
     * @Description 护墙板
     */
    val weatherboard = mapOf<String,List<String>?>(
        "护墙板潮湿/腐烂" to null,
        "护墙板与墙面脱开" to null
    )
    /**
     * @Description 墙纸
     */
    val wallpaper = mapOf<String,List<String>?>(
        "墙纸潮湿发霉" to null,
        "墙纸局部破损" to null
    )

    /**
     * @Description  栏板
     */
    val fence = mapOf<String,List<String>?>(
        "与窗连接处渗水" to null,
        "表面瓷砖开裂" to null,
        "表面瓷砖破损" to null,
        "表面瓷砖空鼓" to null,
        "表面瓷砖脱落" to null,
        "与南北墙连接处开裂" to null,
    )

    /**
     * @Description 墙面贴瓷砖
     */
    val wallTile = mapOf<String,List<String>?>(
        "瓷砖开裂普遍" to null,
        "釉面开裂普遍" to null
    )


    /**
     * @Description 厨房
     */
    val kitchen = mapOf<String,Any>(
        "顶板" to roof,
        "地坪" to floor,
        "东墙" to wall,
        "南墙" to wall,
        "西墙" to wall,
        "北墙" to wall,
//        "墙面贴瓷砖" to wallTile,
        "橱柜" to cupboard,
        "灶台" to cooktop,
    )




    /**
     * @Description 卫生间
     */
    val bathroom = mapOf<String,Any?>(
        "顶板" to roof,
        "地坪" to floor,
        "东墙" to wall,
        "南墙" to wall,
        "西墙" to wall,
        "北墙" to wall,
//        "墙面贴瓷砖" to wallTile,
        "洁具" to clean
    )
    /**
     * @Description 客厅
     */
    val livingRoom = mapOf<String,Any?>(
        "顶板" to roof,
        "地坪" to floor,
        "东墙" to wall,
        "南墙" to wall,
        "西墙" to wall,
        "北墙" to wall,
//        "墙面有护墙板" to weatherboard,
//        "墙面贴墙纸" to wallpaper
    )

    /**
     * @Description 卧室
     */
    val bedroom = mapOf<String,Any?>(
        "顶板" to roof,
        "地坪" to floor,
        "东墙" to wall,
        "南墙" to wall,
        "西墙" to wall,
        "北墙" to wall,
//        "墙面有护墙板" to weatherboard,
//        "墙面贴墙纸" to wallpaper
    )
    /**
     * @Description 庭院
     */
    val outdoors = mapOf<String,Any?>(
        "地坪" to floor3,
        "东围墙" to wall,
        "南围墙" to wall,
        "西围墙" to wall,
        "北围墙" to wall,
//        "墙面贴瓷砖" to wallTile,
    )
    /**
     * @Description 阳台
     */
    val portal = mapOf<String,Any?>(
        "顶板" to roof,
        "地坪" to floor,
        "东墙" to wall,
        "南墙" to wall,
        "西墙" to wall,
        "北墙" to wall,
//        "墙面贴瓷砖" to wallTile,
        "栏板" to fence
    )

    /**
     * @Description 露台
     */
    val terrace = mapOf<String,Any?>(
        "地坪" to floor,
        "东墙" to wall,
        "南墙" to wall,
        "西墙" to wall,
        "北墙" to wall,
//        "墙面贴瓷砖" to wallTile,
    )

    /**
     * @Description  盥洗室
     */
    val washroom = mapOf<String,Any?>(
        "顶板" to roof,
        "地坪" to floor,
        "东墙" to wall,
        "南墙" to wall,
        "西墙" to wall,
        "北墙" to wall,
//        "墙面贴瓷砖" to wallTile,
//        "墙面有护墙板" to weatherboard,
//        "墙面贴墙纸" to wallpaper
    )

    /**
     * @Description 楼层位置
     */
    val floorPos = mapOf<String,Any?>(
        "顶板" to roof4,
        "地坪" to floor,
        "东墙" to wall5,
        "南墙" to wall5,
        "西墙" to wall5,
        "北墙" to wall5,
//        "墙面贴瓷砖" to wallTile,
    )
    /**
     * @Description 休息平台
     */
    val platform = mapOf<String,Any?>(
        "顶板" to roof4,
        "地坪" to floor,
        "东墙" to wall5,
        "南墙" to wall5,
        "西墙" to wall5,
        "北墙" to wall5,
//        "墙面贴瓷砖" to wallTile,
    )

    /**
     * @Description 损伤描述
     */
    val damageMap = mapOf(
        "厨房"        to kitchen,
        "卫生间"      to bathroom,
        "主卫" to bathroom,
        "客卫" to bathroom,
        "盥洗室" to washroom,
        "餐厅" to livingRoom,
        "客厅" to livingRoom,
        "起居室" to livingRoom,
        "储物间" to livingRoom,
        "玄关" to livingRoom,
        "过道" to livingRoom,
        "卧室" to bedroom,
        "东卧室" to bedroom,
        "南卧室" to bedroom,
        "西卧室" to bedroom,
        "北卧室" to bedroom,
        "东南卧室" to bedroom,
        "东北卧室" to bedroom,
        "西南卧室" to bedroom,
        "西北卧室" to bedroom,
        "阳台" to portal,
        "南阳台" to portal,
        "北阳台" to portal,
        "露台" to terrace,
        "南庭院" to outdoors,
        "北庭院" to outdoors,
        "楼梯间" to floorPos,
        "休息平台" to platform
    )

    fun getDetailType(pos:ArrayList<String>):Int{

        return when{
            pos.contains("裂缝")-> 2
            pos.contains("接缝")-> 3
            else -> 1
        }
    }

}