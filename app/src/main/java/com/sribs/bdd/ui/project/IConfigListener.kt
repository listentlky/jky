package com.sribs.bdd.ui.project

import com.sribs.bdd.bean.data.ProjectConfigBean

/**
 * @date 2021/7/2
 * @author elijah
 * @Description
 */
interface IConfigListener {
    fun onConfigClick(floorNumber:String,neighborNum:String?,des:String,bean:ProjectConfigBean?)
    fun onCopyClick(des:String,bean:ProjectConfigBean?)
    fun onDrawingsChangeClick(floorId:String)
}