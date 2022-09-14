package com.sribs.common.bean.net

import com.cbj.sdk.libnet.http.bean.ListBean
import com.google.gson.annotations.SerializedName

/**
 * @date 2021/8/9
 * @author elijah
 * @Description
 */

data class Detail(
    @SerializedName("content") var content:String,
    @SerializedName("resId")   var resId:String,
    @SerializedName("resUrl")  var resUrl:String?
)

data class Item(
    @SerializedName("itemName") var itemName:String,
    @SerializedName("details")  var details:List<Detail>
)

data class DamageReport(
    @SerializedName("items")    var items:List<Item>,
    @SerializedName("feedback") var feedback:String?,
    @SerializedName("signatureResId") var signatureResId:String?,
    @SerializedName("signatureResUrl") var signatureResUrl:String?
)

data class Description(
    @SerializedName("position")  var position:String,
    @SerializedName("detail")    var detail:String,
    @SerializedName("text")      var text:String,
    @SerializedName("graph")     var graph:String,
    @SerializedName("photoResId") var photoResId:String,
    @SerializedName("photoResUrl") var photoResUrl:String?
)


data class DamageDescription(
    @SerializedName("roomName")   var roomName:String,
    @SerializedName("roomNow")    var roomNow:PartNow?,
    @SerializedName("description") var description:List<Description>,
    @SerializedName("isCompleted") var isCompleted: Int
)

data class PartNow(
    @SerializedName("status")      var status:String?,
    @SerializedName("newlyDate")   var newlyDate:String?,
    @SerializedName("actualUse")   var actualUse:String?=null
)

data class Parts(
    @SerializedName("partType")     var partType:String,
    @SerializedName("partNo")       var partNo:String,
    @SerializedName("partNow")      var partNow:PartNow?,
    @SerializedName("inspectorId")  var inspectorId:String,
    @SerializedName("isCompleted")  var isCompleted:Int,
    @SerializedName("damageDescription") var damageDescription: ListBean<DamageDescription>,
    @SerializedName("damageReport") var damageReport:DamageReport,
    @SerializedName("version")      var version:Int
)

data class RecordUnit(
    @SerializedName("unitId")       var unitId:String,
    @SerializedName("unitNo")       var unitNo:String,
    @SerializedName("parts")        var parts:List<Parts>,
    @SerializedName("version")      var version:Int,
    @SerializedName("createTime")   var createTime:String,
    @SerializedName("updateTime")   var updateTime:String
)


data class ProjectRecordDownloadRes(
    @SerializedName("units")        var units:List<RecordUnit>
)
