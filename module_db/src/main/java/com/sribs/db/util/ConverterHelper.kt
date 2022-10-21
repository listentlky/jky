package com.sribs.db.util

import com.sribs.common.bean.db.v3.project.v3BuildingModuleDbBean
import com.sribs.common.bean.v3.v3ModuleFloorDbBean
import com.sribs.common.utils.TimeUtil
import com.sribs.common.utils.Util
import com.sribs.db.house.HouseStatusBean
import com.sribs.db.house.RoomStatusBean
import com.sribs.db.house.detail.RoomDetailBean
import com.sribs.db.inspector.InspectorBean
import com.sribs.db.leader.LeaderBean
import com.sribs.db.project.ProjectBean
import com.sribs.db.project.building.BuildingBean
import com.sribs.db.project.damage.DamageBean
import com.sribs.db.project.drawing.DrawingBean
import com.sribs.db.project.floor.FloorBean
import com.sribs.db.project.unit.UnitBean
import com.sribs.db.project.unit.config.ConfigBean
import com.sribs.db.report.ReportBean
import com.sribs.db.user.UserBean
import com.sribs.db.v3.project.v3BuildingModuleRoom
import com.sribs.db.v3.project.v3ModuleFloorRoom
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import java.security.MessageDigest
import java.sql.Date
import java.util.*

/**
 * @date 2021/7/27
 * @author elijah
 * @Description
 */
object ConverterHelper {

    fun convertProjectBean(l: Flowable<List<ProjectBean>>): Flowable<List<com.sribs.common.bean.db.ProjectBean>> =
        l.map {
            it.map { b ->
                com.sribs.common.bean.db.ProjectBean(
                    b.id,
                    b.uuid,
                    b.name,
                    b.leader,
                    b.inspector,
                    b.builderNo,
                    b.status,
                    b.parentVersion,
                    b.version,
                    b.createTime,
                    b.updateTime,
                    b.remoteId,
                    0,
                    b.isChanged
                    )
            }
        }

    fun convertProjectBeanOnce(l: Single<List<ProjectBean>>): Single<List<com.sribs.common.bean.db.ProjectBean>> =
        l.map {
            it.map { b ->
                com.sribs.common.bean.db.ProjectBean(
                    b.id,
                    b.uuid,
                    b.name,
                    b.leader,
                    b.inspector,
                    b.builderNo,
                    b.status,
                    b.parentVersion,
                    b.version,
                    b.createTime,
                    b.updateTime,
                    b.remoteId,
                    0,
                    b.isChanged
                    )
            }
        }


    fun covertProjectBean(b: com.sribs.common.bean.db.ProjectBean): ProjectBean = ProjectBean(
        uuid = b.uuid,
        name = b.name,
        leader = b.leader,
        builderNo = b.buildNo,
        inspector = b.inspector,
        remoteId = b.remoteId,
        isChanged = b.isChanged
    ).also {
        if (b.id ?: -1 > 0) {
            it.id = b.id!!
        }
        if (b.status != null) {
            it.status = b.status
        }
        if (b.createTime != null) {
            it.createTime = b.createTime
        } else {
            it.createTime = TimeUtil.stampToDate(""+System.currentTimeMillis())
        }
    }

    /**
     * 3期项目 start
     */

    fun convertv3BuildingModuleBean(l: Flowable<List<v3BuildingModuleRoom>>): Flowable<List<v3BuildingModuleDbBean>> =
        l.map {
            it.map { b ->
                v3BuildingModuleDbBean(
                    b.id,
                    b.uuid,
                    b.buildingRemoteId,
                    b.buildingUUID,
                    b.buildingId,
                    b.projectUUID,
                    b.projectId,
                    b.moduleName,
                    b.leaderId,
                    b.leaderName,
                    b.isDeleted,
                    b.aboveGroundNumber,
                    b.underGroundNumber,
                    b.drawings,
                    b.inspectors,
                    b.deleteTime,
                    b.createTime,
                    b.updateTime,
                    b.remoteId,
                    b.superiorVersion,
                    b.parentVersion,
                    b.version,
                    b.status,
                    b.isChanged
                )
            }
        }

    fun convertOnlyv3BuildingModuleBean(l: Single<List<v3BuildingModuleRoom>>): Single<List<v3BuildingModuleDbBean>> =
        l.map {
            it.map { b ->
                v3BuildingModuleDbBean(
                    b.id,
                    b.uuid,
                    b.buildingRemoteId,
                    b.buildingUUID,
                    b.buildingId,
                    b.projectUUID,
                    b.projectId,
                    b.moduleName,
                    b.leaderId,
                    b.leaderName,
                    b.isDeleted,
                    b.aboveGroundNumber,
                    b.underGroundNumber,
                    b.drawings,
                    b.inspectors,
                    b.deleteTime,
                    b.createTime,
                    b.updateTime,
                    b.remoteId,
                    b.superiorVersion,
                    b.parentVersion,
                    b.version,
                    b.status,
                    b.isChanged
                )
            }
        }


    fun convertOnlyv3BuildingModuleBeanOnce(l: Single<v3BuildingModuleRoom>): Single<v3BuildingModuleDbBean> =
        l.map {
             b ->
                v3BuildingModuleDbBean(
                    b.id,
                    b.uuid,
                    b.buildingRemoteId,
                    b.buildingUUID,
                    b.buildingId,
                    b.projectUUID,
                    b.projectId,
                    b.moduleName,
                    b.leaderId,
                    b.leaderName,
                    b.isDeleted,
                    b.aboveGroundNumber,
                    b.underGroundNumber,
                    b.drawings,
                    b.inspectors,
                    b.deleteTime,
                    b.createTime,
                    b.updateTime,
                    b.remoteId,
                    b.superiorVersion,
                    b.parentVersion,
                    b.version,
                    b.status,
                    b.isChanged
                )

        }


    fun convertv3BuildingModuleRoom(b: v3BuildingModuleDbBean): v3BuildingModuleRoom =
        v3BuildingModuleRoom(
            uuid = b.uuid,
            buildingRemoteId = b.buildingRemoteId,
            buildingUUID = b.buildingUUID,
            buildingId = b.buildingId,
            projectUUID = b.projectUUID,
            projectId = b.projectId,
            leaderId = b.leaderId,
            leaderName = b.leaderName,
            isDeleted = b.isDeleted,
            moduleName = b.moduleName,
            aboveGroundNumber = b.aboveGroundNumber,
            underGroundNumber = b.underGroundNumber,
            drawings = b.drawings,
            inspectors = b.inspectors,
            remoteId = b.remoteId,
            superiorVersion = b.superiorVersion,
            parentVersion = b.parentVersion,
            version = b.version,
            status = b.status,
            isChanged = b.isChanged

        ).also {
            if (b.id ?: -1 > 0) {
                it.id = b.id!!
            }
            if (b.createTime != null) {
                it.createTime = b.createTime
            } else {
                it.createTime = TimeUtil.YMD_HMS.format(Date())
            }
            if (b.updateTime != null) {
                it.updateTime = b.updateTime
            } else {
                it.updateTime = TimeUtil.YMD_HMS.format(Date())
            }
        }


    fun convertv3ModuleFloorBean(l: Flowable<List<v3ModuleFloorRoom>>): Flowable<List<v3ModuleFloorDbBean>> =
        l.map {
            it.map { b ->
                v3ModuleFloorDbBean(
                    b.id,
                    b.bldId,
                    b.projectId,
                    b.moduleId,
                    b.floorId,
                    b.floorName,
                    b.floorType,
                    b.drawingsList,
                    b.aboveNumber,
                    b.afterNumber,
                    b.createTime,
                    b.updateTime,
                    b.deleteTime,
                    b.version,
                    b.remoteId,
                    b.status,
                )
            }
        }


    fun convertv3ModuleFloorRoom(b: v3ModuleFloorDbBean): v3ModuleFloorRoom =
        v3ModuleFloorRoom(
            projectId = b.projectId,
            bldId = b.bldId,
            moduleId = b.moduleId,
            floorId = b.floorId,
            floorName = b.floorName,
            floorType = b.floorType,
            drawingsList = b.drawingsList,
            status = b.status,
            aboveNumber = b.aboveNumber,
            afterNumber = b.afterNumber,
            createTime = b.createTime.toString(),
            updateTime = b.updateTime.toString(),
            deleteTime = b.deleteTime.toString(),

        ).also {
            if (b.id ?: -1 > 0) {
                it.id = b.id!!
            }
            if (b.createTime != null) {
                it.createTime = b.createTime.toString()
            } else {
                it.createTime = TimeUtil.YMD_HMS.format(Date())
            }
            if (b.updateTime != null) {
                it.updateTime = b.updateTime.toString()
            } else {
                it.updateTime = TimeUtil.YMD_HMS.format(Date())
            }
        }


    /**
     * 3期项目 end
     */

    fun convertUnitBean(l: Flowable<List<UnitBean>>): Flowable<List<com.sribs.common.bean.db.UnitBean>> =
        l.map {
            it.map { b ->
                com.sribs.common.bean.db.UnitBean(
                    b.projectId,
                    b.bldId,
                    b.id,
                    b.unitNo,
                    b.floorSize,
                    b.neighborSize,
                    b.floor_type,
                    b.createTime,
                    b.updateTime,
                    b.remoteId,
                    b.version,
                    b.status,
                    b.leaderId,
                    b.leaderName,
                    b.inspectors
                )
            }
        }

    fun convertUnitBeanOnce(l: Single<List<UnitBean>>): Single<List<com.sribs.common.bean.db.UnitBean>> =
        l.map {
            it.map { b ->
                com.sribs.common.bean.db.UnitBean(
                    b.projectId,
                    b.bldId,
                    b.id,
                    b.unitNo,
                    b.floorSize,
                    b.neighborSize,
                    b.floor_type,
                    b.createTime,
                    b.updateTime,
                    b.remoteId,
                    b.version,
                    b.status,
                    b.leaderId,
                    b.leaderName,
                    b.inspectors

                )
            }
        }

    fun convertUnitBean(b: com.sribs.common.bean.db.UnitBean): UnitBean = UnitBean(
        projectId = b.projectId,
        bldId = b.bldId,
        unitNo = b.unitNo,
        floorSize = b.floorSize,
        neighborSize = b.neighborSize
    ).also {
        if (b.unitId ?: -1 > 0) {
            it.id = b.unitId!!
        }
        if (b.floorType ?: -1 >= 0) {
            it.floor_type = b.floorType
        }
        if (b.createTime != null) {
            it.createTime = b.createTime
        } else {
            it.createTime = Date(Date().time)
        }
        if (!b.remoteId.isNullOrEmpty()) {
            it.remoteId = b.remoteId
        }
        if (b.version != null) {
            it.version = b.version
        }
        if (b.status != null) {
            it.status = b.status
        }
        if (b.leaderId != null) {
            it.leaderId = b.leaderId
        }
        if (b.leaderName != null) {
            it.leaderName = b.leaderName
        }
        if (b.inspectors != null) {
            it.inspectors = b.inspectors
        }
    }

    fun convertConfigBean(l: Flowable<List<ConfigBean>>): Flowable<List<com.sribs.common.bean.db.ConfigBean>> =
        l.map {
            it.map { b ->
                com.sribs.common.bean.db.ConfigBean(
                    b.projectId,
                    b.bldId,
                    b.unitId,
                    b.id,
                    b.floorIdx,
                    b.neighborIdx,
                    b.configType,
                    b.floorNum,
                    b.neighborNum,
                    b.corridorNum,
                    b.platformNum,
                    b.corridorConfig,
                    b.platformConfig,
                    b.config1,
                    b.config2,
                    b.unitType,
                    b.createTime,
                    b.updateTime
                )
            }
        }

    fun convertConfigBeanOnce(l: Single<List<ConfigBean>>): Single<List<com.sribs.common.bean.db.ConfigBean>> =
        l.map {
            it.map { b ->
                com.sribs.common.bean.db.ConfigBean(
                    b.projectId,
                    b.bldId,
                    b.unitId,
                    b.id,
                    b.floorIdx,
                    b.neighborIdx,
                    b.configType,
                    b.floorNum,
                    b.neighborNum,
                    b.corridorNum,
                    b.platformNum,
                    b.corridorConfig,
                    b.platformConfig,
                    b.config1,
                    b.config2,
                    b.unitType,
                    b.createTime,
                    b.updateTime
                )
            }
        }

    fun convertConfigBean(b: com.sribs.common.bean.db.ConfigBean): ConfigBean = ConfigBean(
        projectId = b.projectId,
        bldId = b.bldId,
        unitId = b.unitId,
        floorIdx = b.floorIdx
    ).also {
        if (b.bldId ?: -1 > 0) {
            it.bldId = b.bldId!!
        }
        if (b.configId ?: -1 > 0) {
            it.id = b.configId!!
        }
        if (b.neighborIdx ?: -1 >= 0) {
            it.neighborIdx = b.neighborIdx
        }
        if (b.configType ?: -1 >= 0) {
            it.configType = b.configType
        }
        if (!b.floorNum.isNullOrEmpty()) {
//            it.floorNum = Util.formatZeroNum(b.floorNum)
            it.floorNum = Util.formatNoZeroNum(b.floorNum)
        }
        if (!b.neighborNum.isNullOrEmpty()) {
            it.neighborNum = b.neighborNum
        }
        if (!b.corridorNum.isNullOrEmpty()) {
            it.corridorNum = Util.formatNoZeroNum(b.corridorNum)
        }
        if (!b.corridorConfig.isNullOrEmpty()) {
            it.corridorConfig = b.corridorConfig
        }
        if (!b.platformNum.isNullOrEmpty()) {
            it.platformNum = Util.formatNoZeroNum(b.platformNum)
        }
        if (!b.platformConfig.isNullOrEmpty()) {
            it.platformConfig = b.platformConfig
        }
        if (!b.config1.isNullOrEmpty()) {
            it.config1 = b.config1
        }
        if (!b.config2.isNullOrEmpty()) {
            it.config2 = b.config2
        }
        if (b.unitType ?: -1 >= 0) {
            it.unitType = b.unitType
        }

        if (b.createTime != null) {
            it.createTime = b.createTime
        } else {
            it.createTime = Date(Date().time)
        }
    }

    fun convertHouseStatusBean(l: Flowable<List<HouseStatusBean>>): Flowable<List<com.sribs.common.bean.db.HouseStatusBean>> =
        l.map {
            it.map { b ->
                com.sribs.common.bean.db.HouseStatusBean(
                    b.projectId,
                    b.unitId,
                    b.configId,
                    b.id,
                    b.name,
                    b.houseType,
                    b.status,
                    b.isFinish,
                    b.inspector,
                    b.houseStatus,
                    b.houseFurnishTime,
                    b.createTime,
                    b.updateTime
                )
            }
        }

    fun convertHouseStatusBeanOnce(l: Single<List<HouseStatusBean>>): Single<List<com.sribs.common.bean.db.HouseStatusBean>> =
        l.map {
            it.map { b ->
                com.sribs.common.bean.db.HouseStatusBean(
                    b.projectId,
                    b.unitId,
                    b.configId,
                    b.id,
                    b.name,
                    b.houseType,
                    b.status,
                    b.isFinish,
                    b.inspector,
                    b.houseStatus,
                    b.houseFurnishTime,
                    b.createTime,
                    b.updateTime,
                    b.version
                )
            }
        }


    fun convertHouseStatusBean(b: com.sribs.common.bean.db.HouseStatusBean): HouseStatusBean =
        HouseStatusBean(
            projectId = b.projectId,
            unitId = b.unitId,
            configId = b.configId
        ).also {
            if (b.houseStatusId ?: -1 > 0) {
                it.id = b.houseStatusId!!
            }
            if (!b.name.isNullOrEmpty()) {
                it.name = b.name
            }
            if (b.houseType ?: -1 >= 0) {
                it.houseType = b.houseType
            }
            if (b.status ?: -1 >= 0) {
                it.status = b.status
            }
            if (b.isFinish ?: -1 >= 0) {
                it.isFinish = b.isFinish
            }
            if (!b.inspector.isNullOrEmpty()) {
                it.inspector = b.inspector
            }
            if (!b.houseStatus.isNullOrEmpty()) {
                it.houseStatus = b.houseStatus
            }
            if (b.houseFurnishTime != null) {
                it.houseFurnishTime = b.houseFurnishTime
            }
            if (b.version != null) {
                it.version = b.version
            }
            if (b.createTime != null) {
                it.createTime = b.createTime
            } else {
                it.createTime = Date(Date().time)
            }
        }

    fun convertRoomStatusBean(l: Flowable<List<RoomStatusBean>>): Flowable<List<com.sribs.common.bean.db.RoomStatusBean>> =
        l.map {
            it.map { b ->
                com.sribs.common.bean.db.RoomStatusBean(
                    b.projectId,
                    b.unitId,
                    b.configId,
                    b.id,
                    b.name,
                    b.isFinish,
                    b.roomStatus,
                    b.roomFurnishTime,
                    b.roomNote,
                    b.createTime,
                    b.updateTime
                )
            }
        }


    fun convertRoomStatusOnce(l: Single<List<RoomStatusBean>>): Single<List<com.sribs.common.bean.db.RoomStatusBean>> =
        l.map {
            it.map { b ->
                com.sribs.common.bean.db.RoomStatusBean(
                    b.projectId,
                    b.unitId,
                    b.configId,
                    b.id,
                    b.name,
                    b.isFinish,
                    b.roomStatus,
                    b.roomFurnishTime,
                    b.roomNote,
                    b.createTime,
                    b.updateTime
                )
            }
        }

    fun convertRoomStatusBean(b: com.sribs.common.bean.db.RoomStatusBean): RoomStatusBean =
        RoomStatusBean(
            projectId = b.projectId,
            unitId = b.unitId,
            configId = b.configId
        ).also {
            if (b.roomId ?: -1 > 0) {
                it.id = b.roomId!!
            }
            if (!b.name.isNullOrEmpty()) {
                it.name = b.name
            }
            if (b.isFinish ?: -1 >= 0) {
                it.isFinish = b.isFinish
            }

            if (!b.roomStatus.isNullOrEmpty()) {
                it.roomStatus = b.roomStatus
            }
            if (b.roomFurnishTime != null) {
                it.roomFurnishTime = b.roomFurnishTime
            }
            if (!b.roomNote.isNullOrEmpty()) {
                it.roomNote = b.roomNote
            }

            if (b.createTime != null) {
                it.createTime = b.createTime
            } else {
                it.createTime = Date(Date().time)
            }
        }

    fun convertRoomDetailBean(l: Flowable<List<RoomDetailBean>>): Flowable<List<com.sribs.common.bean.db.RoomDetailBean>> =
        l.map {
            it.map { b ->
                com.sribs.common.bean.db.RoomDetailBean(
                    b.projectId,
                    b.unitId,
                    b.configId,
                    b.id,
                    b.name,
                    b.damagePath,
                    b.damageIdx,
                    b.splitNum,
                    b.splitWidth,
                    b.splitLen,
                    b.splitType,
                    b.seamNum,
                    b.description,
                    b.picPath,
                    b.picId,
                    b.picUrl,
                    b.createTime,
                    b.updateTime
                )
            }
        }

    fun convertRoomDetailBeanOnce(l: Single<List<RoomDetailBean>>): Single<List<com.sribs.common.bean.db.RoomDetailBean>> =
        l.map {
            it.map { b ->
                com.sribs.common.bean.db.RoomDetailBean(
                    b.projectId,
                    b.unitId,
                    b.configId,
                    b.id,
                    b.name,
                    b.damagePath,
                    b.damageIdx,
                    b.splitNum,
                    b.splitWidth,
                    b.splitLen,
                    b.splitType,
                    b.seamNum,
                    b.description,
                    b.picPath,
                    b.picId,
                    b.picUrl,
                    b.createTime,
                    b.updateTime
                )
            }
        }

    fun convertRoomDetailBean(b: com.sribs.common.bean.db.RoomDetailBean): RoomDetailBean =
        RoomDetailBean(
            projectId = b.projectId,
            unitId = b.unitId,
            configId = b.configId,
            name = b.name,
            damagePath = b.damagePath
        ).also {
            if (b.roomDetailId ?: -1 > 0) {
                it.id = b.roomDetailId!!
            }
            if (b.damageIdx ?: -1 >= 0) {
                it.damageIdx = b.damageIdx
            }
            if (!b.splitNum.isNullOrEmpty()) {
                it.splitNum = b.splitNum
            }
            if (!b.splitWidth.isNullOrEmpty()) {
                it.splitWidth = b.splitWidth
            }
            if (!b.splitLen.isNullOrEmpty()) {
                it.splitLen = b.splitLen
            }
            if (b.splitType ?: -1 >= 0) {
                it.splitType = b.splitType
            }
            if (!b.seamNum.isNullOrEmpty()) {
                it.seamNum = b.seamNum
            }
            if (!b.description.isNullOrEmpty()) {
                it.description = b.description
            }
            if (!b.picPath.isNullOrEmpty()) {
                it.picPath = b.picPath
            }
            if (!b.picId.isNullOrEmpty()) {
                it.picId = b.picId
            }
            if (!b.picUrl.isNullOrEmpty()) {
                it.picUrl = b.picUrl
            }

            if (b.createTime != null) {
                it.createTime = b.createTime
            } else {
                it.createTime = Date(Date().time)
            }
        }


    fun convertReportBean(l: Flowable<List<ReportBean>>): Flowable<List<com.sribs.common.bean.db.ReportBean>> =
        l.map {
            it.map { b ->
                com.sribs.common.bean.db.ReportBean(
                    b.projectId,
                    b.unitId,
                    b.configId,
                    b.id,
                    b.report,
                    b.isSave,
                    b.signPath,
                    b.signResId,
                    b.signResUrl,
                    b.createTime,
                    b.updateTime
                )
            }
        }

    fun convertReportBeanOnce(l: Single<List<ReportBean>>): Single<List<com.sribs.common.bean.db.ReportBean>> =
        l.map {
            it.map { b ->
                com.sribs.common.bean.db.ReportBean(
                    b.projectId,
                    b.unitId,
                    b.configId,
                    b.id,
                    b.report,
                    b.isSave,
                    b.signPath,
                    b.signResId,
                    b.signResUrl,
                    b.createTime,
                    b.updateTime
                )
            }
        }

    fun convertReportBean(b: com.sribs.common.bean.db.ReportBean): ReportBean = ReportBean(
        projectId = b.projectId,
        unitId = b.unitId,
        configId = b.configId,
    ).also {
        if (b.reportId ?: -1 > 0) {
            it.id = b.reportId!!
        }
        if (!b.report.isNullOrEmpty()) {
            it.report = b.report
        }
        if (b.isSave ?: -1 >= 0) {
            it.isSave = b.isSave
        }
        if (!b.signPath.isNullOrEmpty()) {
            it.signPath = b.signPath
        }
        if (!b.signResId.isNullOrEmpty()) {
            it.signResId = b.signResId
        }
        if (!b.signResUrl.isNullOrEmpty()) {
            it.signResUrl = b.signResUrl
        }
        if (b.createTime != null) {
            it.createTime = b.createTime
        } else {
            it.createTime = Date(Date().time)
        }
    }

    fun convertUserBean(l: Maybe<List<UserBean>>): Maybe<List<com.sribs.common.bean.db.UserBean>> =
        l.map {
            it.map { b ->
                com.sribs.common.bean.db.UserBean(
                    b.id,
                    b.name,
                    b.account,
                    b.password,
                    b.mobile,
                    b.salt
                )
            }
        }

    fun convertUserBean(b: com.sribs.common.bean.db.UserBean): UserBean = UserBean(
        b.name,
        b.account,
        b.password,
        b.mobile,
        b.salt
    ).also {
        if (!b.id.isNullOrEmpty()) {
            it.id = b.id!!
        }
    }

    fun convertLeaderBean(l: Maybe<List<LeaderBean>>): Maybe<List<com.sribs.common.bean.db.LeaderBean>> =
        l.map {
            it.map { b ->
                com.sribs.common.bean.db.LeaderBean(
                    b.id,
                    b.name,

                    )
            }
        }

    fun convertLeaderBean(b: com.sribs.common.bean.db.LeaderBean): LeaderBean = LeaderBean(
        b.name,
    ).also {
        if (!b.id.isNullOrEmpty()) {
            it.id = b.id!!
        }
    }

    fun convertInspectorBean(l: Maybe<List<InspectorBean>>): Maybe<List<com.sribs.common.bean.db.InspectorBean>> =
        l.map {
            it.map { b ->
                com.sribs.common.bean.db.InspectorBean(
                    b.id,
                    b.name,

                    )
            }
        }

    fun convertInspectorBean(b: com.sribs.common.bean.db.InspectorBean): InspectorBean =
        InspectorBean(
            b.name,
        ).also {
            if (!b.id.isNullOrEmpty()) {
                it.id = b.id!!
            }
        }

    fun convertBuildingBean(bb: com.sribs.common.bean.db.BuildingBean): BuildingBean = BuildingBean(
        bb.UUID,
        bb.projectRemoteId,
        bb.projectUUID,
        bb.projectId,
        bb.bldName,
        bb.bldType,
        bb.createTime,
        bb.updateTime,
        bb.deleteTime,
        bb.isDeleted,
        bb.leader,
        bb.inspectorName,
        bb.remoteId,
        bb.superiorVersion,
        bb.parentVersion,
        bb.version,
        bb.status,
        bb.drawing,
        bb.aboveGroundNumber,
        bb.underGroundNumber,
        bb.isChanged
    ).also {
        if (bb.id ?: -1 > 0) {
            it.id = bb.id!!
        }
    }

    fun convertBuildingBean(l: Flowable<List<BuildingBean>>): Flowable<List<com.sribs.common.bean.db.BuildingBean>> =
        l.map {
            it.map { b ->
                com.sribs.common.bean.db.BuildingBean(
                    b.id,
                    b.uuid,
                    b.projectRemoteId,
                    b.projectUUID,
                    b.projectId,
                    b.bldName,
                    b.bldType,
                    b.createTime,
                    b.updateTime,
                    b.deleteTime,
                    b.isDeleted,
                    b.leader,
                    b.inspectorName,
                    b.superiorVersion,
                    b.parentVersion,
                    b.version,
                    b.remoteId,
                    b.status,
                    b.drawing,
                    b.aboveGroundNumber,
                    b.underGroundNumber,
                    b.isChanged
                )
            }
        }

    fun convertBuildingBeanOnce(l: Single<List<BuildingBean>>): Single<List<com.sribs.common.bean.db.BuildingBean>> =
        l.map {
            it.map { b ->
                com.sribs.common.bean.db.BuildingBean(
                    b.id,
                    b.uuid,
                    b.projectRemoteId,
                    b.projectUUID,
                    b.projectId,
                    b.bldName,
                    b.bldType,
                    b.createTime,
                    b.updateTime,
                    b.deleteTime,
                    b.isDeleted,
                    b.leader,
                    b.inspectorName,
                    b.superiorVersion,
                    b.parentVersion,
                    b.version,
                    b.remoteId,
                    b.status,
                    b.drawing,
                    b.aboveGroundNumber,
                    b.underGroundNumber,
                    b.isChanged
                )
            }
        }

    fun convertFloorBean(fb: com.sribs.common.bean.db.FloorBean): FloorBean = FloorBean(
        fb.projectId,
        fb.bldId,
        fb.unitId,
        fb.floorId,
        fb.floorName,
        fb.floorType,
        fb.createTime,
        fb.updateTime,
        fb.deleteTime,
        fb.inspectorName,
        fb.remoteId,
        fb.version,
        fb.status,
        fb.drawing,
        fb.aboveGroundNumber,
        fb.underGroundNumber
    ).also {
        if (fb.id ?: -1 > 0) {
            it.id = fb.id!!
        }
    }

    fun convertFloorsInTheBuilding(l: Flowable<List<FloorBean>>): Flowable<List<com.sribs.common.bean.db.FloorBean>> =
        l.map {
            it.map { b ->
                com.sribs.common.bean.db.FloorBean(
                    b.id,
                    b.projectId,
                    b.bldId,
                    b.unitId,
                    b.floorId,
                    b.floorName,
                    b.floorType,
                    b.createTime,
                    b.updateTime,
                    b.deleteTime,
                    b.inspectorName,
                    b.version,
                    b.remoteId,
                    b.status,
                    b.drawing,
                    b.aboveGroundNumber,
                    b.underGroundNumber
                )
            }
        }

    fun convertDrawingBean(dw: com.sribs.common.bean.db.DrawingBean): DrawingBean = DrawingBean(
        dw.projectId,
        dw.bldId,
        dw.unitId,
        dw.floorId,
        dw.floorName,
        dw.fileName,
        dw.drawingType,
        dw.fileType,
        dw.localAbsPath,
        "",
        dw.createTime,
        dw.updateTime,
        dw.deleteTime,
        dw.inspectorName,
        dw.remoteId,
        dw.version,
        dw.status
    ).also {
        if (dw.id ?: -1 > 0) {
            it.id = dw.id!!
        }
    }

    fun convertDbDrawingBeanToCommon(dw: Maybe<List<com.sribs.db.project.drawing.DrawingBean>>): Maybe<List<com.sribs.common.bean.db.DrawingBean>> =
        dw.map {
            it.map { b ->
                com.sribs.common.bean.db.DrawingBean(
                    b.id,
                    b.projectId,
                    b.bldId,
                    b.unitId,
                    b.floorId,
                    b.floorName,
                    b.fileName,
                    b.drawingType,
                    b.fileType,
                    b.localAbsPath,
                    b.remoteAbsPath,
                    b.createTime,
                    b.updateTime,
                    b.deleteTime,
                    b.inspectorName,
                    b.remoteId,
                    b.version,
                    b.status
                )
            }
        }


    fun convertDbDamageBeanToCommon(dm: Flowable<List<com.sribs.db.project.damage.DamageBean>>): Flowable<List<com.sribs.common.bean.db.DamageBean>> =
        dm.map {
            it.map { b ->
                com.sribs.common.bean.db.DamageBean(
                    b.drawingId,
                    b.dTypeName,
                    b.annotRef,
                    b.axis,
                    b.dmDesc,
                    b.pohtoPath,
                    b.dDetailType,
                    b.leakLength,
                    b.leakWidth,
                    b.mntId,
                    b.mntWay,
                    b.monitorLength,
                    b.monitorWidth,
                    b.monitorPhotoPath,
                    b.createTime,
                    b.updateTime,
                    b.deleteTime,
                    b.inspectorName,
                    b.remoteId,
                    b.version,
                    b.status,
                    b.id
                )
            }
        }.also {

        }

    fun convertCommonDamageBeanToDb(dm: com.sribs.common.bean.db.DamageBean): com.sribs.db.project.damage.DamageBean =
        DamageBean(
            dm.drawingId,
            dm.dTypeName,
            dm.annotRef,
            dm.axis,
            dm.dmDesc,
            dm.pohtoPath,
            dm.dDetailType,
            dm.leakLength,
            dm.leakWidth,
            dm.mntId,
            dm.mntWay,
            dm.monitorLength,
            dm.monitorWidth,
            dm.monitorPhotoPath,
            dm.createTime,
            dm.updateTime,
            dm.deleteTime,
            dm.inspectorName,
            dm.remoteId,
            dm.version,
            dm.status
        ).also {
            if (dm.id ?: -1 > 0) {
                it.id = dm.id!!
            }
        }

    fun convertDbDrawingBeanToCommonFlow(dw: Flowable<List<com.sribs.db.project.drawing.DrawingBean>>): Flowable<List<com.sribs.common.bean.db.DrawingBean>> =
        dw.map {
            it.map { b ->
                com.sribs.common.bean.db.DrawingBean(
                    b.id,
                    b.projectId,
                    b.bldId,
                    b.unitId,
                    b.floorId,
                    b.floorName,
                    b.fileName,
                    b.drawingType,
                    b.fileType,
                    b.localAbsPath,
                    b.remoteAbsPath,
                    b.createTime,
                    b.updateTime,
                    b.deleteTime,
                    b.inspectorName,
                    b.remoteId,
                    b.version,
                    b.status
                )
            }
        }


    fun md5(str: String): ByteArray = MessageDigest.getInstance("MD5").digest(str.toByteArray())
    fun ByteArray.toHex() = joinToString(separator = "") { "%02x".format(it) }


}