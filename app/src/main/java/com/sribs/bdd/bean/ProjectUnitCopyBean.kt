package com.sribs.bdd.bean

import com.sribs.bdd.bean.data.ProjectConfigBaseDataBean
import com.sribs.bdd.bean.data.ProjectUnitDetailDataBean

/**
 * @date 2021/7/7
 * @author elijah
 * @Description
 */
class ProjectUnitCopyBean {
    var unitData: ProjectUnitDetailDataBean?=null
    var unitConfigData:ArrayList<FloorConfig?>?=null


    class FloorConfig{
        var floorData: ProjectConfigBaseDataBean?=null
        var neighborConfig:ArrayList<ProjectConfigBaseDataBean?>?=null
        constructor(data:ProjectConfigBaseDataBean?,config:ArrayList<ProjectConfigBaseDataBean?>?){
            floorData = data
            neighborConfig = config
        }
    }
}