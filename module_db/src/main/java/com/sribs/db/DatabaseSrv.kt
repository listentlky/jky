package com.sribs.db

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libbase.utils.LOG
import com.sribs.common.bean.db.*
import com.sribs.common.bean.db.v3.project.v3BuildingModuleDbBean
import com.sribs.common.bean.db.v3.project.v3ProjectDbBean
import com.sribs.common.bean.v3.v3ModuleFloorDbBean
import com.sribs.common.server.IDatabaseService
import com.sribs.db.project.unit.UnitBean
import com.sribs.db.project.unit.config.ConfigBean
import com.sribs.db.util.ConverterHelper
import com.sribs.db.util.ConverterHelper.toHex
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

/**
 * @date 2021/7/8
 * @author elijah
 * @Description
 */
@Route(path = com.sribs.common.ARouterPath.SRV_DB)
class DatabaseSrv : IDatabaseService {
    private var mContext: Context? = null

    private var mDb: DDBDataBase? = null

    override fun init(context: Context?) {
        println("leon DatabaseSrv init in")
        mContext = context

        mDb = Room.databaseBuilder(
            context!!, DDBDataBase::class.java, "ddb.db"
        ).build()
    }

    override fun getProject(id: Long): Flowable<List<ProjectBean>> {
        var dao = mDb!!.projectDao()
        return dao.getProject(id).run { ConverterHelper.convertProjectBean(this) }
    }

    override fun getProjectOnce(id: Long): Single<List<ProjectBean>> {
        var dao = mDb!!.projectDao()
        return dao.getProjectOnce(id).run { ConverterHelper.convertProjectBeanOnce(this) }
    }

    override fun getProjectOnce(name: String, buildNo: String): Single<List<ProjectBean>> {
        var dao = mDb!!.projectDao()
        return dao.getProjectOnce(name, buildNo)
            .run { ConverterHelper.convertProjectBeanOnce(this) }
    }

    override fun getProjectOnce(name: String): Single<List<ProjectBean>> {
        var dao = mDb!!.projectDao()
        return dao.getProjectOnce(name).run { ConverterHelper.convertProjectBeanOnce(this) }
    }

    override fun getAllProject(): Flowable<List<ProjectBean>> {
        var dao = mDb!!.projectDao()
        return dao.getAllProject().run { ConverterHelper.convertProjectBean(this) }
    }

    override fun updateProject(b: ProjectBean): Observable<Long> = Observable.create {
        var dao = mDb!!.projectDao()
        it.onNext(dao.insertProject(ConverterHelper.covertProjectBean(b)))
    }

    override fun deleteProject(b: ProjectBean): Observable<Int> = Observable.create {
        var dao = mDb!!.projectDao()
        it.onNext(dao.deleteProject(ConverterHelper.covertProjectBean(b)))
    }

    /**
     * 3期项目 start
     */

    // 3007 查询楼建筑下的模块
    override fun getv3AllBuildingModule(): Flowable<List<v3BuildingModuleDbBean>> {
        var dao = mDb!!.v3BuildingModuleDao()
        return dao.getAllProject().run {
            ConverterHelper.convertv3BuildingModuleBean(this)
        }
    }

    override fun getv3BuildingModule(
        projectId: Long,
        buildingId: Long
    ): Flowable<List<v3BuildingModuleDbBean>> {
        var dao = mDb!!.v3BuildingModuleDao()
        return dao.getProject(projectId, buildingId).run {
            ConverterHelper.convertv3BuildingModuleBean(this)
        }
    }

    override fun getv3BuildingModule(
        projectId: Long,
        buildingId: String
    ): Flowable<List<v3BuildingModuleDbBean>> {
        var dao = mDb!!.v3BuildingModuleDao()
        return dao.getProject(projectId, buildingId.toLong()).run {
            ConverterHelper.convertv3BuildingModuleBean(this)
        }
    }

    override fun getv3BuildingModule(
        moduleId: Long,
    ): Single<v3BuildingModuleDbBean> {
        var dao = mDb!!.v3BuildingModuleDao()
        return dao.getProject(moduleId).run {
            ConverterHelper.convertOnlyv3BuildingModuleBeanOnce(this)
        }
    }

    override fun getv3BuildingModuleOnce(buildingId: String): Single<List<v3BuildingModuleDbBean>> {
        var dao = mDb!!.v3BuildingModuleDao()
        return dao.getProjectOnce(buildingId.toLong()).run {
            ConverterHelper.convertOnlyv3BuildingModuleBean(this)
        }
    }

    override fun getv3BuildingModuleOnce(
        projectId: Long,
        buildingId: Long
    ): Single<List<v3BuildingModuleDbBean>> {
        var dao = mDb!!.v3BuildingModuleDao()
        return dao.getProjectOnce(projectId, buildingId).run {
            ConverterHelper.convertOnlyv3BuildingModuleBean(this)
        }
    }

    override fun getv3BuildingModuleOnce(
        projectId: Long,
        buildingId: Long,
        moduleId: Long
    ): Single<List<v3BuildingModuleDbBean>> {
        var dao = mDb!!.v3BuildingModuleDao()
        return dao.getProjectOnce(projectId, buildingId,moduleId).run {
            ConverterHelper.convertOnlyv3BuildingModuleBean(this)
        }
    }

    override fun getv3BuildingModuleOnce(moduleId: Long): Single<List<v3BuildingModuleDbBean>> {
        var dao = mDb!!.v3BuildingModuleDao()
        return dao.getProjectOnce(moduleId).run {
            ConverterHelper.convertOnlyv3BuildingModuleBean(this)
        }
    }

    override fun updatev3BuildingModule(b: v3BuildingModuleDbBean): Observable<Long> =
        Observable.create {
            var dao = mDb!!.v3BuildingModuleDao()
            it.onNext(dao.insertProject(ConverterHelper.convertv3BuildingModuleRoom(b)))
        }

    override fun deleteBuildingModuleByProjectId(projectId: Long): Observable<Boolean> = Observable.create{
        var dao = mDb!!.v3BuildingModuleDao()
        dao.deleteBuildingModuleByProjectId(projectId)
        it.onNext(true)
    }

    override fun deleteBuildingModuleByBuildingId(buildingId: Long): Observable<Boolean> = Observable.create{
        var dao = mDb!!.v3BuildingModuleDao()
        dao.deleteBuildingModuleByBuildingId(buildingId)
        it.onNext(true)
    }

    override fun updatev3BuildingModuleDrawing(b: v3BuildingModuleDbBean): Observable<Int> =
        Observable.create {
            var dao = mDb!!.v3BuildingModuleDao()
            it.onNext(dao.updateProjectOneData(b.id!!,b.drawings!!))

            Log.e("llf", "updatev3BuildingModuleOneData: "+it )
        }

    override fun updatev3BuildingModuleDrawing(id:Long,drawingList:List<DrawingV3Bean>): Observable<Int> =
        Observable.create {
            var dao = mDb!!.v3BuildingModuleDao()
            it.onNext(dao.updateProjectOneData(id,drawingList))
            Log.e("llf", "updatev3BuildingModuleOneData: "+it )
        }


    override fun deletev3BuildingModule(b: v3BuildingModuleDbBean): Observable<Int> =
        Observable.create {
            var dao = mDb!!.v3BuildingModuleDao()
            it.onNext(dao.deleteProject(ConverterHelper.convertv3BuildingModuleRoom(b)))
        }

    override fun getv3ModuleFloor(
        projectId: Long,
        buildingId: Long,
        moduleId: Long
    ): Flowable<List<v3ModuleFloorDbBean>> {
        var dao = mDb!!.v3ModuleFloorDao()
        return dao.getModuleFloorByProjectIdOnce(projectId, buildingId, moduleId).run {
            ConverterHelper.convertv3ModuleFloorBean(this)
        }
    }

    override fun updatev3ModuleFloor(b: v3ModuleFloorDbBean): Observable<Long> =
        Observable.create {
            var dao = mDb!!.v3ModuleFloorDao()
            it.onNext(dao.insertModuleFloor(ConverterHelper.convertv3ModuleFloorRoom(b)))
        }


    override fun deletev3ModuleFloor(
        projectId: Long,
        buildingId: Long,
        moduleId: Long
    ): Observable<Boolean> = Observable.create {
        var dao = mDb!!.v3ModuleFloorDao()
        dao.deleteModuleFloor(projectId, buildingId, moduleId)
        it.onNext(true)
    }

    override fun deletev3ModuleFloorOnce(
        projectId: Long,
        buildingId: Long,
        moduleId: Long,
        floorId: Long
    ): Observable<Boolean> =Observable.create {
        var dao = mDb!!.v3ModuleFloorDao()
        dao.deleteModuleFloorOnce(projectId,buildingId,moduleId,floorId)
        it.onNext(true)
    }

    override fun deleteModuleFloorByProjectId(projectId: Long): Observable<Boolean> =Observable.create {
        var dao = mDb!!.v3ModuleFloorDao()
        dao.deleteModuleFloorByProjectId(projectId)
        it.onNext(true)
    }

    override fun deleteModuleFloorByBuildingId(buildingId: Long): Observable<Boolean> =Observable.create {
        var dao = mDb!!.v3ModuleFloorDao()
        dao.deleteModuleFloorByBuildingId(buildingId)
        it.onNext(true)
    }

    /**
     * 3期项目 end
     */

    override fun getAllUnit(projectId: Long): Flowable<List<com.sribs.common.bean.db.UnitBean>> {
        var dao = mDb!!.unitDao()
        return dao.getUnitByProjectId(projectId).run { ConverterHelper.convertUnitBean(this) }
    }

    override fun getAllUnitOnce(projectId: Long): Single<List<com.sribs.common.bean.db.UnitBean>> {
        var dao = mDb!!.unitDao()
        return dao.getUnitByProjectIdOnce(projectId)
            .run { ConverterHelper.convertUnitBeanOnce(this) }
    }

    override fun getUnit(unitId: Long): Flowable<List<com.sribs.common.bean.db.UnitBean>> {
        var dao = mDb!!.unitDao()
        return dao.getUnit(unitId).run { ConverterHelper.convertUnitBean(this) }
    }

    override fun getUnitOnce(unitId: Long): Single<List<com.sribs.common.bean.db.UnitBean>> {
        var dao = mDb!!.unitDao()
        return dao.getUnitOnce(unitId).run { ConverterHelper.convertUnitBeanOnce(this) }
    }

    override fun getUnitOnce(remoteId: String): Single<List<com.sribs.common.bean.db.UnitBean>> {
        var dao = mDb!!.unitDao()
        return dao.getUnitByRemoteIdOnce(remoteId).run { ConverterHelper.convertUnitBeanOnce(this) }
    }

    override fun updateUnit(b: com.sribs.common.bean.db.UnitBean): Observable<Long> =
        Observable.create {
            var dao = mDb!!.unitDao()
            it.onNext(dao.insertUnit(ConverterHelper.convertUnitBean(b)))
        }

    override fun deleteUnit(ids: List<Long>): Observable<Int> = Observable.create {
        var dao = mDb!!.unitDao()
        val arr = ids.map { id -> UnitBean(id) }.toTypedArray()
        it.onNext(dao.deleteUnits(*arr))
    }

    override fun deleteUnit(projectId: Long): Observable<Boolean> = Observable.create {
        var dao = mDb!!.unitDao()
        dao.deleteUnit(projectId)
        it.onNext(true)
    }

    override fun updateConfig(b: com.sribs.common.bean.db.ConfigBean): Observable<Long> =
        Observable.create {
            var dao = mDb!!.configDao()
            it.onNext(dao.insertConfig(ConverterHelper.convertConfigBean(b)))
        }

    override fun getConfig(
        projectId: Long,
        unitId: Long,
    ): Flowable<List<com.sribs.common.bean.db.ConfigBean>> {
        var dao = mDb!!.configDao()
        return dao.getConfigById(projectId, unitId).run { ConverterHelper.convertConfigBean(this) }
    }

    override fun getConfig(configId: Long): Flowable<List<com.sribs.common.bean.db.ConfigBean>> {
        var dao = mDb!!.configDao()
        return dao.getConfig(configId).run { ConverterHelper.convertConfigBean(this) }
    }

    override fun getConfigOnce(configId: Long): Single<List<com.sribs.common.bean.db.ConfigBean>> {
        var dao = mDb!!.configDao()
        return dao.getConfigOnce(configId).run { ConverterHelper.convertConfigBeanOnce(this) }
    }

    override fun getConfigOnce(
        unitId: Long,
        floorIdx: Int
    ): Single<List<com.sribs.common.bean.db.ConfigBean>> {
        var dao = mDb!!.configDao()
        return dao.getConfigByUnitFloorOnce(unitId, floorIdx)
            .run { ConverterHelper.convertConfigBeanOnce(this) }
    }

    override fun getAllConfig(projectId: Long): Flowable<List<com.sribs.common.bean.db.ConfigBean>> {
        var dao = mDb!!.configDao()
        return dao.getConfigById(projectId).run { ConverterHelper.convertConfigBean(this) }
    }

    override fun getAllConfigOnce(projectId: Long): Single<List<com.sribs.common.bean.db.ConfigBean>> {
        var dao = mDb!!.configDao()
        return dao.getConfigByIdOnce(projectId).run { ConverterHelper.convertConfigBeanOnce(this) }
    }

    override fun getUnitConfig(unitId: Long): Flowable<List<com.sribs.common.bean.db.ConfigBean>> {
        var dao = mDb!!.configDao()
        return dao.getConfigByUnit(unitId).run { ConverterHelper.convertConfigBean(this) }
    }

    override fun getUnitConfigOnce(unitId: Long): Single<List<com.sribs.common.bean.db.ConfigBean>> {
        var dao = mDb!!.configDao()
        return dao.getConfigByUnitOnce(unitId).run { ConverterHelper.convertConfigBeanOnce(this) }
    }

    override fun deleteConfig(ids: List<Long>): Observable<Int> = Observable.create {
        var dao = mDb!!.configDao()
        val arr = ids.map { id -> ConfigBean(id) }.toTypedArray()
        it.onNext(dao.deleteConfigs(*arr))
    }

    override fun deleteConfig(projectId: Long): Observable<Boolean> = Observable.create {
        var dao = mDb!!.configDao()
        dao.deleteConfig(projectId)
        it.onNext(true)
    }

    override fun deleteConfigByUnitId(unitId: Long): Observable<Boolean> = Observable.create {
        var dao = mDb!!.configDao()
        dao.deleteConfigByUnit(unitId)
        it.onNext(true)
    }

    override fun getHouseStatusByUnit(unitId: Long): Flowable<List<HouseStatusBean>> {
        var dao = mDb!!.houseStatusDao()
        return dao.getHouseStatusByUnit(unitId).run { ConverterHelper.convertHouseStatusBean(this) }
    }


    override fun getHouseStatusByUnitOnce(unitId: Long): Single<List<HouseStatusBean>> {
        var dao = mDb!!.houseStatusDao()
        return dao.getHouseStatusByUnitOnce(unitId)
            .run { ConverterHelper.convertHouseStatusBeanOnce(this) }
    }

    override fun getHouseStatusByProjectOnce(projectId: Long): Single<List<HouseStatusBean>> {
        var dao = mDb!!.houseStatusDao()
        return dao.getHouseStatusByProjectOnce(projectId)
            .run { ConverterHelper.convertHouseStatusBeanOnce(this) }
    }

    override fun getHouseStatus(configId: Long): Flowable<List<HouseStatusBean>> {
        var dao = mDb!!.houseStatusDao()
        return dao.getHouseStatusByConfig(configId)
            .run { ConverterHelper.convertHouseStatusBean(this) }
    }

    override fun getHouseStatusByConfigOnce(configId: Long): Single<List<HouseStatusBean>> {
        var dao = mDb!!.houseStatusDao()
        return dao.getHouseStatusByConfigOnce(configId)
            .run { ConverterHelper.convertHouseStatusBeanOnce(this) }
    }

    override fun updateHouseStatus(b: HouseStatusBean): Observable<Long> = Observable.create {
        LOG.I("123", "updateHouse Status ${b.configId} ${b}")
        var dao = mDb!!.houseStatusDao()
        it.onNext(dao.insertHouseStatus(ConverterHelper.convertHouseStatusBean(b)))
    }

    override fun deleteHouseStatusByProject(projectId: Long): Observable<Boolean> =
        Observable.create {
            var dao = mDb!!.houseStatusDao()
            dao.deleteHouseStatusByProject(projectId)
            it.onNext(true)
        }

    override fun deleteHouseStatusByUnit(unitId: Long): Observable<Boolean> = Observable.create {
        var dao = mDb!!.houseStatusDao()
        dao.deleteHouseStatusByUnit(unitId)
        it.onNext(true)
    }

    override fun deleteHouseStatusByConfig(configId: Long): Observable<Boolean> =
        Observable.create {
            var dao = mDb!!.houseStatusDao()
            dao.deleteHouseStatusByConfig(configId)
            it.onNext(true)
        }

    override fun getRoomStatus(configId: Long): Flowable<List<RoomStatusBean>> {
        var dao = mDb!!.roomStatusDao()
        return dao.getRoomStatusByConfig(configId)
            .run { ConverterHelper.convertRoomStatusBean(this) }
    }

    override fun getRoomStatus(configId: Long, name: String): Flowable<List<RoomStatusBean>> {
        var dao = mDb!!.roomStatusDao()
        return dao.getRoomStatusByConfig(configId, name)
            .run { ConverterHelper.convertRoomStatusBean(this) }
    }

    override fun getRoomStatusOnce(configId: Long, name: String): Single<List<RoomStatusBean>> {
        var dao = mDb!!.roomStatusDao()
        return dao.getRoomStatusByConfigOnce(configId, name)
            .run { ConverterHelper.convertRoomStatusOnce(this) }
    }

    override fun getRoomStatusOnce(configId: Long): Single<List<RoomStatusBean>> {
        var dao = mDb!!.roomStatusDao()
        return dao.getRoomStatusByConfigOnce(configId)
            .run { ConverterHelper.convertRoomStatusOnce(this) }
    }

    override fun getRoomStatusByUnitOnce(unitId: Long): Single<List<RoomStatusBean>> {
        var dao = mDb!!.roomStatusDao()
        return dao.getRoomStatusByUnitOnce(unitId)
            .run { ConverterHelper.convertRoomStatusOnce(this) }
    }


    override fun updateRoomStatus(b: RoomStatusBean): Observable<Long> = Observable.create {
        var dao = mDb!!.roomStatusDao()
        it.onNext(dao.insertRoomStatus(ConverterHelper.convertRoomStatusBean(b)))
    }

    override fun deleteRoomStatusByProject(projectId: Long): Observable<Boolean> =
        Observable.create {
            var dao = mDb!!.roomStatusDao()
            dao.deleteRoomStatusByProject(projectId)
            it.onNext(true)
        }

    override fun deleteRoomStatusByUnit(unitId: Long): Observable<Boolean> = Observable.create {
        var dao = mDb!!.roomStatusDao()
        dao.deleteRoomStatusByUnit(unitId)
        it.onNext(true)
    }

    override fun deleteRoomStatusByConfig(configId: Long): Observable<Boolean> = Observable.create {
        var dao = mDb!!.roomStatusDao()
        dao.deleteRoomStatusByConfig(configId)
        it.onNext(true)
    }

    override fun deleteRoomStatusByConfig(configId: Long, name: String): Observable<Boolean> =
        Observable.create {
            var dao = mDb!!.roomStatusDao()
            dao.deleteRoomStatusByConfig(configId, name)
            it.onNext(true)
        }

    override fun deleteRoomStatusById(ids: List<Long>): Observable<Int> = Observable.create {
        var dao = mDb!!.roomStatusDao()
        val arr = ids.map { id -> com.sribs.db.house.RoomStatusBean(id) }.toTypedArray()
        it.onNext(dao.deleteRoomStatus(*arr))
    }

    override fun getRoomDetailById(id: Long): Flowable<List<RoomDetailBean>> {
        var dao = mDb!!.roomDetailDao()
        return dao.getRoomDetail(id).run { ConverterHelper.convertRoomDetailBean(this) }
    }

    override fun getRoomDetail(configId: Long): Flowable<List<RoomDetailBean>> {
        var dao = mDb!!.roomDetailDao()
        return dao.getRoomDetailByConfig(configId)
            .run { ConverterHelper.convertRoomDetailBean(this) }
    }

    override fun getRoomDetail(configId: Long, name: String): Flowable<List<RoomDetailBean>> {
        var dao = mDb!!.roomDetailDao()
        return dao.getRoomDetailByRoom(configId, name)
            .run { ConverterHelper.convertRoomDetailBean(this) }
    }

    override fun getRoomDetailOnce(configId: Long): Single<List<RoomDetailBean>> {
        var dao = mDb!!.roomDetailDao()
        return dao.getRoomDetailByConfigOnce(configId)
            .run { ConverterHelper.convertRoomDetailBeanOnce(this) }
    }

    override fun getRoomDetailOnce(configId: Long, name: String): Single<List<RoomDetailBean>> {
        var dao = mDb!!.roomDetailDao()
        return dao.getRoomDetailByRoomOnce(configId, name)
            .run { ConverterHelper.convertRoomDetailBeanOnce(this) }
    }

    override fun getRoomDetailOnce(
        configId: Long,
        name: String,
        damagePath: String,
    ): Single<List<RoomDetailBean>> {
        var dao = mDb!!.roomDetailDao()
        return if (damagePath.contains("其他损伤")) {
            LOG.I("123", "search other  $damagePath")
            dao.getRoomDetailOtherByPath(configId, name, damagePath)
                .run { ConverterHelper.convertRoomDetailBeanOnce(this) }
        } else {
            LOG.I("123", "search normal  $damagePath")
            dao.getRoomDetailByPath(configId, name, damagePath)
                .run { ConverterHelper.convertRoomDetailBeanOnce(this) }
        }


    }

    override fun getRoomDetailOnce(
        configId: Long,
        name: String,
        damagePath: String,
        damageIdx: Int,
    ): Single<List<RoomDetailBean>> {
        var dao = mDb!!.roomDetailDao()
        return dao.getRoomDetailByPath(configId, name, damagePath, damageIdx)
            .run { ConverterHelper.convertRoomDetailBeanOnce(this) }
    }

    override fun getRoomDetailOnce(
        configId: Long,
        name: String,
        damagePath: String,
        des: String,
    ): Single<List<RoomDetailBean>> {
        var dao = mDb!!.roomDetailDao()
        return dao.getRoomDetailByDescription(configId, name, damagePath, des)
            .run { ConverterHelper.convertRoomDetailBeanOnce(this) }
    }


    override fun getRoomDetailByUnitOnce(unitId: Long): Single<List<RoomDetailBean>> {
        var dao = mDb!!.roomDetailDao()
        return dao.getRoomDetailByUnitOnce(unitId)
            .run { ConverterHelper.convertRoomDetailBeanOnce(this) }
    }

    override fun updateRoomDetail(b: RoomDetailBean): Observable<Long> = Observable.create {
        var dao = mDb!!.roomDetailDao()
        it.onNext(dao.insertRoomDetail(ConverterHelper.convertRoomDetailBean(b)))
    }

    override fun deleteRoomDetailByProject(projectId: Long): Observable<Boolean> =
        Observable.create {
            var dao = mDb!!.roomDetailDao()
            dao.deleteRoomDetailByProject(projectId)
            it.onNext(true)
        }

    override fun deleteRoomDetailByUnit(unitId: Long): Observable<Boolean> = Observable.create {
        var dao = mDb!!.roomDetailDao()
        dao.deleteRoomDetailByUnit(unitId)
        it.onNext(true)
    }

    override fun deleteRoomDetailByConfig(configId: Long): Observable<Boolean> = Observable.create {
        var dao = mDb!!.roomDetailDao()
        dao.deleteRoomDetailByConfig(configId)
        it.onNext(true)
    }

    override fun deleteRoomDetailByConfig(configId: Long, name: String): Observable<Boolean> =
        Observable.create {
            var dao = mDb!!.roomDetailDao()
            dao.deleteRoomDetailByRoom(configId, name)
            it.onNext(true)
        }


    override fun deleteRoomDetailByPath(
        configId: Long,
        name: String,
        likePos: String,
    ): Observable<Boolean> = Observable.create {
        var dao = mDb!!.roomDetailDao()
//        LOG.I("123","deleteRoomDetailByPath  ${configId}  $name $likePos")
//        dao.getRoomDetailOtherByPath(configId,name,likePos)
//            .toObservable()
//            .subscribe({ list->
//                LOG.I("123","deleteRoomDetailByPath=$list")
//            },{ e->
//                e.printStackTrace()
//            })


        dao.deleteRoomDetailOtherByPath(configId, name, likePos)
        it.onNext(true)
    }

    override fun deleteRoomDetailById(id: Long): Observable<Boolean> = Observable.create {
        var dao = mDb!!.roomDetailDao()
        dao.deleteRoomDetail(id)
        it.onNext(true)
    }

    override fun deleteRoomDetail(ids: List<Long>): Observable<Int> = Observable.create {
        var dao = mDb!!.roomDetailDao()
        val arr = ids.map { id -> com.sribs.db.house.detail.RoomDetailBean(id) }.toTypedArray()
        it.onNext(dao.deleteRoomDetail(*arr))
    }

    override fun getReport(configId: Long): Flowable<List<ReportBean>> {
        var dao = mDb!!.reportDao()
        return dao.getReport(configId).run { ConverterHelper.convertReportBean(this) }
    }

    override fun getReportOnce(configId: Long): Single<List<ReportBean>> {
        var dao = mDb!!.reportDao()
        return dao.getReportOnce(configId).run { ConverterHelper.convertReportBeanOnce(this) }
    }

    override fun getReportByUnitOnce(unitId: Long): Single<List<ReportBean>> {
        var dao = mDb!!.reportDao()
        return dao.getReportByUnitOnce(unitId).run { ConverterHelper.convertReportBeanOnce(this) }
    }

    override fun updateReport(b: ReportBean): Observable<Long> = Observable.create {
        var dao = mDb!!.reportDao()
        it.onNext(dao.insertReport(ConverterHelper.convertReportBean(b)))

    }

    override fun deleteReportByProjectId(projectId: Long): Observable<Boolean> = Observable.create {
        var dao = mDb!!.reportDao()
        dao.deleteReportByProject(projectId)
        it.onNext(true)
    }

    override fun deleteReportByUnit(unitId: Long): Observable<Boolean> = Observable.create {
        var dao = mDb!!.reportDao()
        dao.deleteReportByUnit(unitId)
        it.onNext(true)
    }

    override fun deleteReportByConfig(configId: Long): Observable<Boolean> = Observable.create {
        var dao = mDb!!.reportDao()
        dao.deleteReportByConfig(configId)
        it.onNext(true)
    }

    override fun getUserBean(mobile: String, passwordMd5: String): Maybe<List<UserBean>> {
        var dao = mDb!!.userDao()
        return dao.getUser(mobile, ConverterHelper.md5(passwordMd5).toHex()).run {
            ConverterHelper.convertUserBean(this)
        }
    }

    override fun getUserBean(account: String): Maybe<List<UserBean>> {
        var dao = mDb!!.userDao()
        return dao.getUser(account).run { ConverterHelper.convertUserBean(this) }
    }

    override fun updateUserBean(b: UserBean): Observable<Long> = Observable.create {
        var dao = mDb!!.userDao()
        it.onNext(dao.insertUser(ConverterHelper.convertUserBean(b)))
    }

    override fun updateUserBean(bs: List<UserBean>): Observable<Long> = Observable.create {
        var dao = mDb!!.userDao()
        var arr = bs.map { b -> ConverterHelper.convertUserBean(b) }.toTypedArray()
        dao.insertUser(*arr)
        it.onNext(arr.size.toLong())
    }

    override fun getLeader(): Maybe<List<LeaderBean>> {
        var dao = mDb!!.leaderDao()
        return dao.getLeader().run { ConverterHelper.convertLeaderBean(this) }
    }

    override fun updateLeader(bs: List<LeaderBean>): Observable<Long> = Observable.create {
        var dao = mDb!!.leaderDao()
        var arr = bs.map { b -> ConverterHelper.convertLeaderBean(b) }.toTypedArray()
        dao.insertLeader(*arr)
        it.onNext(arr.size.toLong())
    }

    override fun deleteLeader(ids: List<String>): Observable<Int> = Observable.create {
        var dao = mDb!!.leaderDao()
        val arr = ids.map { id -> com.sribs.db.leader.LeaderBean(id) }.toTypedArray()
        it.onNext(dao.deleteLeader(*arr))
    }

    override fun getInspector(): Maybe<List<InspectorBean>> {
        var dao = mDb!!.inspectorDao()
        return dao.getInspector().run { ConverterHelper.convertInspectorBean(this) }
    }

    override fun updateInspector(bs: List<InspectorBean>): Observable<Long> = Observable.create {
        var dao = mDb!!.inspectorDao()
        var arr = bs.map { b -> ConverterHelper.convertInspectorBean(b) }.toTypedArray()
        dao.insertInspector(*arr)
        it.onNext(arr.size.toLong())
    }

    override fun deleteInspector(ids: List<String>): Observable<Int> = Observable.create {
        var dao = mDb!!.inspectorDao()
        val arr = ids.map { id -> com.sribs.db.inspector.InspectorBean(id) }.toTypedArray()
        it.onNext(dao.deleteInspector(*arr))
    }

    override fun getLocalBuildingOnce(id: Long): Single<List<BuildingBean>> {
        var dao = mDb!!.buildingDao()
        return dao.getBuildingOnce(id).run { ConverterHelper.convertBuildingBeanOnce(this) }
    }

    override fun getLocalFloorsInTheBuilding(bldId: Long): Flowable<List<FloorBean>> {
        var dao = mDb!!.floorDao()
        return dao.getLocalFloorsInTheBuilding(bldId)
            .run { ConverterHelper.convertFloorsInTheBuilding(this) }
    }

    override fun deleteFloorByProjectId(projectId: Long): Observable<Boolean>  = Observable.create{
        var dao = mDb!!.floorDao()
        dao.deleteFloorByProjectId(projectId)
        it.onNext(true)
    }

    override fun deleteFloorByBuildingId(bldId: Long): Observable<Boolean>  = Observable.create{
        var dao = mDb!!.floorDao()
        dao.deleteFloorByBuildingId(bldId)
        it.onNext(true)
    }

    override fun getAllFloor(): Flowable<List<FloorBean>> {
        var dao = mDb!!.floorDao()
        return dao.getAllFloor().run { ConverterHelper.convertFloorsInTheBuilding(this) }
    }

    override fun getAllBuilding(): Flowable<List<BuildingBean>> {
        var dao = mDb!!.buildingDao()
        return dao.getAllBuilding().run { ConverterHelper.convertBuildingBean(this) }
    }

    override fun getBuildingByProjectId(projectId: Long): Flowable<List<BuildingBean>> {
        var dao = mDb!!.buildingDao()
        return dao.getBuildingByProjectId(projectId)
            .run { ConverterHelper.convertBuildingBean(this) }
    }

    override fun getAllDrawing(): Flowable<List<DrawingBean>> {
        var dao = mDb!!.drawingDao()
        return dao.getAllDrawing().run { ConverterHelper.convertDbDrawingBeanToCommonFlow(this) }
    }

    override fun updateModuleFloorDrawing (
        drawingList: List<DrawingV3Bean>,
        id: Long
    ):Observable<Int> = Observable.create {
        var dao = mDb!!.v3ModuleFloorDao();
        var moduleFloorId = dao.updateModuleFloorDrawing(drawingList,id)
        it.onNext(moduleFloorId)
    }

    override fun getBuildingIdByProjectId(proId: Long): Observable<Long> = Observable.create {
        var dao = mDb!!.buildingDao()
        var bldId: Long = dao.getBuildingIdByProjectId(proId)
        Log.i("leon", "getBuildingIdByProjectId return bldId = $bldId")
        it.onNext(bldId)
    }

    override fun deleteBuildingByProjectId(projectId: Long): Observable<Boolean> = Observable.create{
        var dao = mDb!!.buildingDao()
        dao.deleteBuildingByProjectId(projectId)
        it.onNext(true)
    }

    override fun deleteBuilding(bldId : Long): Observable<Boolean> = Observable.create{
        var dao = mDb!!.buildingDao()
        dao.deleteBuilding(bldId)
        it.onNext(true)
    }

    override fun createLocalBuilding(bb: BuildingBean): Observable<Long> = Observable.create {
        var dao = mDb!!.buildingDao()
        var ret: Long = dao.insertBuilding(ConverterHelper.convertBuildingBean(bb))
        println("leon createLocalBuilding new building number=${ret}")
        it.onNext(ret)
    }

    override fun updateLocalProject(
        pb: ProjectBean,
        bldList: List<BuildingBean>?,
        floorList: List<FloorBean>?,
        drawingList: List<DrawingBean>?,
    ): Observable<Long> = Observable.create {
        var dao = mDb!!.projectDao()

//        it.onNext(dao.updateProject(ConverterHelper.convertProjectBean(b)).toLong())
    }

    override fun createLocalFloor(bb: FloorBean): Observable<Long> = Observable.create {
        var dao = mDb!!.floorDao()
        var ret: Long = dao.insertFloor(ConverterHelper.convertFloorBean(bb))
        println("leon createLocalFloor new floor number=${ret}")
        it.onNext(ret)
    }

    override fun createLocalDrawing(dw: DrawingBean): Observable<Long> = Observable.create {
        var dao = mDb!!.drawingDao()
        var ret: Long = dao.insertDrawing(ConverterHelper.convertDrawingBean(dw))
        println("leon createLocalFloor new floor number=${ret}")
        it.onNext(ret)
    }

    override fun getLocalDrawingListInBuilding(
        proId: Long,
        bldId: Long
    ): Maybe<List<com.sribs.common.bean.db.DrawingBean>> {
        var dao = mDb!!.drawingDao()
        return dao.getLocalDrawingListInBuilding(proId, bldId)
            .run { ConverterHelper.convertDbDrawingBeanToCommon(this) }
    }

    override fun getLocalDamageListInDrawing(drwingId: Long): Flowable<List<com.sribs.common.bean.db.DamageBean>> {
        var dao = mDb!!.damageDao()
        return dao.getDamageByDrawingId(drwingId)
            .run { ConverterHelper.convertDbDamageBeanToCommon(this) }
    }

    override fun createDamageInDrawing(dmg: DamageBean): Observable<Long> = Observable.create {
        var dao = mDb!!.damageDao()
        var ret: Long = dao.insertDamage(ConverterHelper.convertCommonDamageBeanToDb(dmg))
        it.onNext(ret)
    }

    override fun updateDamageInDrawing(dmg: DamageBean): Observable<Int> = Observable.create {
        var dao = mDb!!.damageDao()
        var ret: Int = dao.updateDamage(ConverterHelper.convertCommonDamageBeanToDb(dmg))
        it.onNext(ret)
    }

    override fun getDrawingByProjectId(prjId: Long): Flowable<List<com.sribs.common.bean.db.DrawingBean>> {
        var dao = mDb!!.drawingDao()
        return dao.getDrawingByProjectId(prjId)
            .run { ConverterHelper.convertDbDrawingBeanToCommonFlow(this) }

    }

    override fun getNonInhabitProjects(): Flowable<List<com.sribs.common.bean.db.RelationBean>> {
        var dao = mDb!!.drawingDao()
        return dao.getNonInhabitProjects()
    }

    override fun removeDamageInDrawing(drwId: Long): Observable<Boolean> = Observable.create {
        var dao = mDb!!.damageDao()
        dao.deleteDamages(drwId)
        it.onNext(true)
    }
}