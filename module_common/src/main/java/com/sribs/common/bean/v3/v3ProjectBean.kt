package com.sribs.common.bean.v3

/**
 * create time: 2022/9/16
 * author: bruce
 * description:
 */
class v3ProjectBean{
    var code:String?=null
    var data:List<Project>?=null
    var msg:String?=null

}
class Project{
    var projectId:String?=null
    var projectName:String?=null
    var inspectors:List<String>?=null
    var leaderId:String?=null
    var leaderName:String?=null
    var createTime:String?=null
    var updateTime:String?=null

}
