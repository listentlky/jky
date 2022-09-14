package com.sribs.bdd.bean

import com.sribs.bdd.bean.data.ProjectConfigBaseDataBean

/**
 * @date 2021/7/7
 * @author elijah
 * @Description
 */
class MsgConfig {
    companion object{
        const val CMD_EDIT = 0x001
        const val CMD_COPY = 0x002
        const val CMD_COPY_FLOOR = 0x003
    }

    var cmd:Int?=null
    var data: ProjectConfigBaseDataBean?=null
    var desCode:ArrayList<String>?=null
    var srcCode:String?=null
}