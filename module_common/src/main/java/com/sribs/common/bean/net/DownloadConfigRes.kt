package com.sribs.common.bean.net

import com.google.gson.annotations.SerializedName

/**
 * @date 2021/8/6
 * @author elijah
 * @Description
 */
data class Config(
    @SerializedName("name")             var name:String,
    @SerializedName("isCustom")         var isCustom:Int,
)

data class ConfigRoom(
    @SerializedName("roomIdx")         var roomIdx:Int?,
    @SerializedName("roomNo")          var roomNo:String,
    @SerializedName("roomType")        var roomType:String?,
    @SerializedName("roomConfigs")     var roomConfigs:List<Config>?,
    @SerializedName("duplexRoomConfigs") var duplexRoomConfigs:List<Config>?
)

data class ConfigPlatform(
    @SerializedName("platformNo")      var platformNo:String?,
    @SerializedName("platformConfigs") var platformConfigs:List<Config>?
)

data class ConfigCorridor(
    @SerializedName("corridorNo")       var corridorNo:String?,
    @SerializedName("corridorConfigs")  var corridorConfigs:List<Config>?
)

data class ConfigFloor(
    @SerializedName("floorIdx")          var floorIdx:Int?,
    @SerializedName("floorNo")           var floorNo:String,
    @SerializedName("floorType")         var floorType:String,
    @SerializedName("corridor")          var configCorridor:ConfigCorridor?,
    @SerializedName("platform")          var configPlatform:ConfigPlatform?,
    @SerializedName("rooms")             var configRooms:List<ConfigRoom>?
)

data class ConfigUnit(
    @SerializedName("unitId")            var unitId:String,
    @SerializedName("unitNo")            var unitNo:String,
    @SerializedName("floorCount")        var floorCount:Int,
    @SerializedName("roomCount")         var roomCount:Int,
    @SerializedName("staircaseType")     var staircaseType:String,
    @SerializedName("floors")            var configFloors:List<ConfigFloor>,
    @SerializedName("version")           var version:Int,
    @SerializedName("createTime")        var createTime:String?,
    @SerializedName("updateTime")        var updateTime:String?
){

}



data class ProjectConfigDownloadRes(
    @SerializedName("units")             var configUnits:List<ConfigUnit>
)


