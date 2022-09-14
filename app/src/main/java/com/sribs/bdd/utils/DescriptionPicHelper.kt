package com.sribs.bdd.utils

import com.cbj.sdk.libbase.utils.LOG

/**
 * @date 2021/7/20
 * @author elijah
 * @Description
 */
object DescriptionPicHelper {
    val directionHelp = arrayOf("东","南","西","北")

    val detailList = listOf(
        "吊顶破损",//ok
        "水渍",//ok
        "渗水",//ok
        "裂缝",//ok
        "接缝",
        "气窗缝",
        "发霉",
        "霉渍",
        "地砖",
        "龟裂",
        "无窗",//ok
        "有窗",//ok
        "有门",//ok
        "找平层龟裂普遍",
    )

    fun hasDetail(s:String):Boolean{
        if (s.contains("大面积"))return false
        if (s.contains("普遍"))return false
        if (s == "潮湿发霉")return false
        var res = false
        detailList.forEach {
            if (s.contains(it)){
                res = true
                return@forEach
            }
        }
        return res
    }

    val fixList = listOf(
        "顶板",
        "地坪"
    )

    fun isFix(s:String):Boolean = fixList.contains(s)

    val directionFix1 = mapOf(
        0 to "西北角",
        1 to "北侧",
        2 to "东北侧",
        3 to "西侧",
        4 to "中部",
        5 to "东侧",
        6 to "西南侧",
        7 to "南侧",
        8 to "东南侧",
    )

    val directionFixRoof1 = mapOf(
        0 to "西北角",
        1 to "东北角",
        2 to "西南角",
        3 to "东南角",
        4 to "有",
        5 to "有",
        6 to "有",
        7 to "有",
    )

    val directionFixRoof2 = mapOf(
        0 to "斜裂缝",
        1 to "斜裂缝",
        2 to "斜裂缝",
        3 to "斜裂缝",
        4 to "偏西斜裂缝",
        5 to "南北向裂缝",
        6 to "偏东斜裂缝",
        7 to "东西向裂缝",
    )

    val directionFixFloor2 = mapOf(
        0 to "斜裂缝",
        1 to "南北向裂缝",
        2 to "斜裂缝",
        3 to "东西向裂缝"
    )


    val directionVer1 = mapOf(
        0 to "左上角",
        1 to "上部",
        2 to "右上角",
        3 to "左侧",
        4 to "中部",
        5 to "右侧",
        6 to "左下角",
        7 to "下部",
        8 to "右下角",
    )

    val splitVer2 = mapOf(
        0 to "倾斜裂缝",
        1 to "竖向裂缝",
        2 to "倾斜裂缝",
        3 to "水平裂缝"
    )

    val seamFix1 = mapOf(
        0 to "东侧板底与墙",
        1 to "南侧板底与墙",
        2 to "西侧板底与墙",
        3 to "北侧板底与墙",
        4 to "顶板板底",
        5 to "顶板板底",

    )
    val seamFix2 = mapOf(
        0 to "水平接缝",
        1 to "水平接缝",
        2 to "水平接缝",
        3 to "水平接缝",
        4 to "东西向预制板接缝",
        5 to "南北向预制板接缝",
    )

    val seamFix3 = mapOf(
        4 to "东西向接缝",
        5 to "南北向接缝",
    )


    val seamVer1 = mapOf(
        0 to "墙面",
        1 to "下方墙面",
        2 to "墙面",
        3 to "上方墙面",
        4 to "墙面",
        5 to "墙面",
    )

    val seamVer2 = mapOf(
        0 to "竖向接缝",
        1 to "水平梁墙接缝",
        2 to "竖向接缝",
        3 to "水平梁墙接缝",
        4 to "水平梁墙接缝",
        5 to "竖向接缝",
    )


    fun getSeamDirection(myDirection:String,myPos:String,index:Int):String{
        var directionIndex = directionHelp.indexOf(myDirection)
        return when(index){
            0 ->  {
                directionIndex += 1
                if (directionIndex<0)directionIndex = 3
                if (directionIndex>3)directionIndex = 0
                "${directionHelp[directionIndex]}侧"
            }
            2 -> {
                directionIndex -= 1
                if (directionIndex<0)directionIndex = 3
                if (directionIndex>3)directionIndex = 0
                "${directionHelp[directionIndex]}侧"
            }
            else -> ""
        }
    }

    fun getWallDirection(pos1:String,index:Int):String {
        var myDirection = ""
        directionHelp.forEach {
            if (pos1.contains(it)){
                myDirection = it
                return@forEach
            }
        }

        var directionIndex = directionHelp.indexOf(myDirection)
        var sec = when(index){
            0,1,2->"顶部"
            3,4,5->"中部"
            6,7,8->"底部"
            else->""
        }

        return when(index) {
            0,3,6-> {
                directionIndex -= 1
                if (directionIndex<0)directionIndex = 3
                if (directionIndex>3)directionIndex = 0
                "${directionHelp[directionIndex]}侧$sec"
            }
            1,4,7->{
                "中部"
            }
            2,5,8->{
                directionIndex += 1
                if (directionIndex<0)directionIndex = 3
                if (directionIndex>3)directionIndex = 0
                "${directionHelp[directionIndex]}侧$sec"
            }
            else -> ""
        }
    }


    fun getSplitDirection(myDirection:String,index:Int):String{
        var directionIndex = directionHelp.indexOf(myDirection)
        if (directionIndex<0 || directionIndex==null)return ""
        var i = index % 4
        when (i) {
            0 ->  directionIndex -= 1
            2 -> directionIndex += 1
            else -> return ""
        }
        if (directionIndex<0)directionIndex = 3
        if (directionIndex>3)directionIndex = 0
        return directionHelp[directionIndex]
    }


    fun parseDescription(pos:ArrayList<String>?,pic:ArrayList<String>?):ArrayList<String> {
        var size = pos?.size ?: 0
        if (size < 1) return ArrayList()
        var arr:ArrayList<String> = ArrayList()
        if (!hasDetail(pos!!.last())) {
            arr = parseNoDetail(pos, pic)
            return arr
        }
        if (pic.isNullOrEmpty()) return ArrayList()
        var l1 = parseFix(pos, pic)
        if (!l1.isNullOrEmpty()) arr = l1
        var l2 = parseSplit(pos, pic)
        if (!l2.isNullOrEmpty()) arr = l2
        var l3 = parseFloorBrick(pos, pic)
        if (!l3.isNullOrEmpty()) arr = l3
        var l4 = parseSeam(pos, pic)
        if (!l4.isNullOrEmpty()) arr = l4
        return arr
    }

    fun parseNoDetail(pos:ArrayList<String>?,pic:ArrayList<String>?):ArrayList<String>{
        var tmp = ArrayList<String>()
        var resultArr = ArrayList<String>()
        tmp.addAll(pos!!)
        if (tmp.size == 3){
            var i = 0
            var size = tmp[1].length
            while (i < tmp[1].length){
                if (tmp[2].contains(tmp[1][i])){
                    if (size>2) {
                        tmp[1] = tmp[1].removeRange(i,i+1)
                        continue
                    }else{
                        tmp[1] = ""
                        break
                    }
                }
                i++
            }
        }
        resultArr.addAll(tmp)
        LOG.I("123","$resultArr")
        return resultArr
    }

    /**
     * @Description 固定描述
     */
    fun parseFix(pos:ArrayList<String>?,pic:ArrayList<String>?):ArrayList<String>{
        var tmp = ArrayList<String>()
        var resultArr = ArrayList<String>()
        tmp.addAll(pos!!)

        var direct = if (pos[0].contains("墙")){
            getWallDirection(pos[0],pic!![0].toInt())
        }else{
            directionFix1[pic!![0].toIntOrNull()]
        }


        LOG.I("123","direct=$direct")

        if (tmp.size>2){
            if (tmp[2].contains(tmp[1])){
                resultArr.apply {
                    add(tmp[0])
                    add(direct?:"")
                    add(tmp[2])
                }
            }else{
                resultArr.apply {
                    add(tmp[0])
                    add(direct?:"")
                    add(tmp[1])
                    add(tmp[2])
                }
            }

        }else if(tmp.size>1){
            if(tmp[1].length>2){
                var subject = tmp[1].substring(0,2)
                var verb = tmp[1].substring(2)
                LOG.I("123","subject=$subject   verb=$verb")
                tmp[0] = subject
                tmp[1] = verb
            }
            resultArr.apply {
                add(tmp[0])
                add(direct?:"")
                add("有")
                add(tmp[1])
            }
        }

        LOG.I("123","$resultArr")
        return resultArr
    }

    /**
     * @Description 裂缝描述
     */
    fun parseSplit(pos:ArrayList<String>?,pic:ArrayList<String>?):ArrayList<String>{
        if(pos?.contains("裂缝")!=true)return ArrayList()
        var pos1 = pos[0]
        return when(pos1){
            "顶板"->{
                parseRoofSplit(pos,pic)
            }
            "地坪"->{
                parseFloorSplit(pos,pic)
            }
            else->{
                parseWallSplit(pos,pic)
            }
        }
    }
    fun parseFloorSplit(pos:ArrayList<String>?,pic:ArrayList<String>?):ArrayList<String>{
        var resultArr = ArrayList<String>()
        resultArr.add(pos!![0])
        var i = pic!![0].toInt()
        when(i){
            0->  resultArr.add(directionFix1[0]!!)
            1->  resultArr.add(directionFix1[2]!!)
            2->  resultArr.add(directionFix1[6]!!)
            3->  resultArr.add(directionFix1[8]!!)
            else-> resultArr.add(directionFix1[4]!!)
        }
        resultArr.add(directionFixFloor2[i%4]!!)
        if (pos.size>2 &&  pos!![2] == "找平层龟裂普遍"){
            resultArr.add(",${pos!![2]}")
        }
        LOG.I("123","$resultArr")
        return resultArr
    }

    fun parseRoofSplit(pos:ArrayList<String>?,pic:ArrayList<String>?):ArrayList<String>{
        var resultArr = ArrayList<String>()
        resultArr.add(pos!![0])
        var i = pic!![0].toInt()
        resultArr.add(directionFixRoof1[i]!!)
        resultArr.add(directionFixRoof2[i]!!)
        LOG.I("123","$resultArr")
        return resultArr
    }

    fun parseWallSplit(pos:ArrayList<String>?,pic:ArrayList<String>?):ArrayList<String>{
        var resultArr = ArrayList<String>()
        var pos1 = pos!![0]
        var i = pic!![0].toInt()
        var myDir = ""
        directionHelp.forEach {
            if (pos1.contains(it)){
                myDir = it
                return@forEach
            }
        }
        when {
            pos.contains("有窗") -> {
                resultArr.add(pos1+"窗")
            }
            pos.contains("有门") -> {
                resultArr.add(pos1+"门")
            }
            else -> {
                resultArr.add(pos1)
            }
        }
        resultArr.add(directionVer1[(i/4)%9]!!)
        resultArr.add(getSplitDirection(myDir,i)+splitVer2[i%4])
        LOG.I("123","$resultArr")
        return resultArr
    }

    /**
     * @Description 地砖描述
     */
    private fun parseFloorBrick(pos:ArrayList<String>?,pic:ArrayList<String>?):ArrayList<String>{
        if(pos?.contains("地砖")!=true)return ArrayList()
        var resultArr = ArrayList<String>()
        resultArr.add(pos[0])
        var i = pic!![0].toInt()
        resultArr.add(directionFix1[i]!!)
        resultArr.add(pos[pos.size-1])
        LOG.I("123","$resultArr")
        return resultArr
    }

    /**
     * @Description 接缝描述
     */
    private fun parseSeam(pos:ArrayList<String>?,pic:ArrayList<String>?):ArrayList<String>{
        if(pos?.contains("接缝")!=true)return ArrayList()
        var pos1 = pos[0]
        return when(pos1){
            "顶板"->{
                parseRoofSeam(pos,pic)
            }
            "地坪"->{
                parseFloorSeam(pos,pic)
            }
            else->{
                parseWallSeam(pos,pic)
            }
        }
    }

    private fun parseRoofSeam(pos:ArrayList<String>,pic:ArrayList<String>?):ArrayList<String>{
        var resultArr = ArrayList<String>()
        resultArr.add(pos[0])
        var i = pic!![0].toInt()
        resultArr.add(seamFix1[i]!!)
        resultArr.add(seamFix2[i]!!)
        LOG.I("123","$resultArr")
        return resultArr
    }

    private fun parseFloorSeam(pos:ArrayList<String>,pic:ArrayList<String>?):ArrayList<String>{
        var resultArr = ArrayList<String>()
        resultArr.add(pos[0])
        var i = pic!![0].toInt()
        resultArr.add(seamFix3[i]?:"")
        LOG.I("123","$resultArr")
        return resultArr
    }

    private fun parseWallSeam(pos:ArrayList<String>,pic:ArrayList<String>?):ArrayList<String>{
        var resultArr = ArrayList<String>()
        var pos1 = pos!![0]

        if (pos.contains("门洞封堵接缝")){
            resultArr.add("${pos1}原门洞封堵，洞口周边有")
            resultArr.add("接缝")
        } else if(pos.contains("窗洞封堵接缝")){
            resultArr.add("${pos1}原窗洞封堵，洞口周边有")
            resultArr.add("接缝")
        } else if(pos.contains("气窗缝")){
            resultArr.add("${pos1}门上方")
            resultArr.add("气窗缝")
        } else{
            resultArr.add(pos1)
            var i = pic!![0].toInt()
            var myDir = ""
            var pos = ""
            directionHelp.forEach {
                if (pos1.contains(it)){
                    myDir = it
                    pos = pos1.replace(it,"")
                    return@forEach
                }
            }

            resultArr.add(getSeamDirection(myDir,pos,i))
            resultArr.add(seamVer1[i]!!)
            resultArr.add(seamVer2[i]!!)
            LOG.I("123","else parseWallSeam： $resultArr")

        }
        LOG.I("123","parseWallSeam  $resultArr")
        return resultArr

    }
}