package com.sribs.bdd.v3.module

import com.sribs.common.bean.db.DrawingV3Bean
import java.io.Serializable

/**
 * create time: 2022/9/20
 * author: bruce
 * description:
 */
class FloorDrawingModule : Serializable{

    var mMenuName:String?=""
    var mNameList:ArrayList<DrawingV3Bean>?=ArrayList()

}