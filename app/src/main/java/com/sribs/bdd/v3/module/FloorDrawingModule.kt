package com.sribs.bdd.v3.module

import java.io.Serializable

/**
 * create time: 2022/9/20
 * author: bruce
 * description:
 */
class FloorDrawingModule : Serializable{

    var mMenuName:String?=null
    var mNameList:ArrayList<Item>?=ArrayList()

    class Item : Serializable {
        var name:String?=null
        var path:String?=null
    }

}