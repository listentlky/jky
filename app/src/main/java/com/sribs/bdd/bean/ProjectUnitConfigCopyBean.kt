package com.sribs.bdd.bean

import com.sribs.bdd.bean.data.ProjectConfigBean

/**
 * @date 2021/7/6
 * @author elijah
 * @Description
 */
data class ProjectUnitConfigCopyBean(
    var id:String,
    var name:String,
    var isChecked:Boolean
){
    var bean: ProjectConfigBean?=null

    init {
        bean = ProjectConfigBean.parseJsonStr(id)
    }
}
