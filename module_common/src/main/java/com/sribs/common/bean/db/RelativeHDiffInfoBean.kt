package com.sribs.common.bean.db

/**
 * create time: 2022/9/27
 * author: bruce
 * description: 相对高差itembean
 */
class RelativeHDiffInfoBean (
    var tag :String?="", //点号类型
    var after:String?="", //后视
    var before:String?="", //前视
    var abDiff:String?="", //前后视高差
    var height:String?="", //高程
    var pointDiff:String?="",//测点相对高差
) {
    override fun toString(): String {
        return "RelativeHDiffInfoBean(tag=$tag, after=$after, before=$before, abDiff=$abDiff, height=$height, pointDiff=$pointDiff)"
    }
}
