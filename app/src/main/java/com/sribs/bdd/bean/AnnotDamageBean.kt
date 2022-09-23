package com.sribs.bdd.bean

/**
* Created by leon on 2022/03/11.
* desc: 损伤描述基类
*/

data class AnnotDamageBean(val act:Int, val re:Long, val tp:String){
    var action:Int? = -1//编辑（新增、修改）/删除
    var ref:Long? = -1 //annot ref
    var type:String? = "" //损伤分类名称，如结构构建损伤、耐久性损伤、渗漏损伤等
    var axis:String? = "" //轴线
    var content:String? = "" //损伤内容
    var annot:String?="" //标记对象
}
