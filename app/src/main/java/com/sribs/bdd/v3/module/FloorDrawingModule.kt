package com.sribs.bdd.v3.module

import com.sribs.common.bean.db.DrawingV3Bean
import java.io.Serializable

/**
 * create time: 2022/9/20
 * author: bruce
 * description:
 */
class FloorDrawingModule : Serializable{

    var mId:Long?= -1L
    var mFloorName:String?=""
    var mNameList:ArrayList<DrawingV3Bean>?=ArrayList()

    override fun toString(): String {
        return "FloorDrawingModule(mId=$mId, mFloorName=$mFloorName, mNameList=$mNameList)"
    }
}