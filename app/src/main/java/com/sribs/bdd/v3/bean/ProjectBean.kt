package com.sribs.bdd.v3.bean

/**
 * create time: 2022/9/16
 * author: bruce
 * description:
 */
class ProjectBean{
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
