package com.sribs.bdd.module.main

import android.content.Context
import com.cbj.sdk.libbase.exception.MsgThrowable
import com.cbj.sdk.libbase.rxbus.RxBus
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libnet.http.HttpManager
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.google.gson.Gson
import com.sribs.bdd.R
import com.sribs.bdd.action.Dict
import com.sribs.bdd.bean.BuildingModule
import com.sribs.bdd.bean.FloorSortBean
import com.sribs.bdd.bean.MainProjectBean
import com.sribs.bdd.bean.UnitConfigType
import com.sribs.bdd.module.BaseUnitConfigPresenter
import com.sribs.bdd.utils.ModuleHelper
import com.sribs.bdd.v3.bean.UpdateModuleVersionBean
import com.sribs.bdd.v3.event.RefreshProjectListEvent
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.HistoryBean
import com.sribs.common.bean.V3VersionBean
import com.sribs.common.bean.db.*
import com.sribs.common.bean.db.v3.project.v3BuildingModuleDbBean
import com.sribs.common.bean.net.*
import com.sribs.common.bean.net.v3.*
import com.sribs.common.bean.v3.v3ModuleFloorDbBean
import com.sribs.common.net.HttpApi
import com.sribs.common.utils.FileUtil
import com.sribs.common.utils.TimeUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.sql.Date
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * @date 2021/8/5
 * @author elijah
 * @Description
 */
class MainPresenter : BaseUnitConfigPresenter(), IMainListContrast.IMainPresenter {

    private var mView: IMainListContrast.IMainView? = null

    private val mStateArr by lazy {
        mView?.getContext()?.resources?.getStringArray(R.array.main_project_status) ?: emptyArray()
    }

    /**
     * 三期查询项目版本列表
     */
    fun projectV3GetConfigVersionList(
        remoteProjectId: String,
        cb: (ArrayList<V3VersionBean>?) -> Unit
    ) {
        HttpManager.instance.getHttpService<HttpApi>()
            .getV3ProjectVersionList(remoteProjectId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                checkResult(it)
                LogUtils.d("查询项目版本列表：" + it)
                var versionList = ArrayList(it.data!!.map {
                    V3VersionBean(
                        it.projectId,
                        it.projectName,
                        it.leaderName ?: "",
                        it.leaderId ?: "",
                        it.inspectors,
                        it.parentVersion,
                        it.version,
                        TimeUtil.stampToDate(it.createTime)
                    )
                })
                cb(ArrayList(versionList.sortedByDescending { b -> b.createTime }))
            }, {
                LogUtils.d("查询项目版本列表失败：" + it)
                mView?.onMsg(ERROR_HTTP)
                cb(null)
                it.printStackTrace()
            })
    }


    override fun projectGetConfigHistory(
        remoteProjectId: String,
        cb: (ArrayList<HistoryBean>?) -> Unit
    ) {
        addDisposable(
            HttpManager.instance.getHttpService<HttpApi>()
                .getConfigHistoryList(HistoryListReq(projectId = remoteProjectId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    checkResult(it)
                    LOG.I("123", "records ${it.data!!.records}")
                    cb(ArrayList(it.data!!.records.map { b ->
                        HistoryBean(
                            b.historyId, null, null, b.updateTime, b.userName
                        )
                    }))
                }, {
                    mView?.onMsg(ERROR_HTTP)
                    cb(null)
                    it.printStackTrace()
                })
        )
    }

    override fun projectGetRecordHistory(
        remoteProjectId: String,
        cb: (ArrayList<HistoryBean>?) -> Unit
    ) {
        addDisposable(
            HttpManager.instance.getHttpService<HttpApi>()
                .getRecordHistoryList(HistoryListReq(projectId = remoteProjectId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    checkResult(it)
                    cb(ArrayList(it.data!!.records.map { b ->
                        HistoryBean(
                            b.configHistoryId, b.recordHistoryId, null, b.updateTime, b.userName
                        )
                    }))
                }, {
                    mView?.onMsg(ERROR_HTTP)
                    cb(null)
                    it.printStackTrace()
                })
        )
    }

    private fun deleteProjectAllInfo(localProjectId: Long, cb: (Boolean) -> Unit) {
        addDisposable(mDb.deleteProject(ProjectBean(id = localProjectId))
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                mDb.deleteConfig(localProjectId)
            }
            .flatMap {
                mDb.deleteConfig(localProjectId)
            }
            .flatMap {
                mDb.deleteHouseStatusByProject(localProjectId)
            }
            .flatMap {
                mDb.deleteRoomStatusByProject(localProjectId)
            }
            .flatMap {
                mDb.deleteRoomDetailByProject(localProjectId)
            }
            .flatMap {
                mDb.deleteReportByProjectId(localProjectId)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                cb(it)
            }, {
                cb(false)
                it.printStackTrace()
            })
        )
    }

    /**
     * 上传项目配置
     */
    fun uploadProject(bean: MainProjectBean, cb: (Boolean) -> Unit) {
        LogUtils.d("上传项目配置： " + bean)

        var buildingList = ArrayList<BuildingBean>()

        var buildingFloorList = ArrayList<FloorBean>()

        var buildingModuleList = ArrayList<BuildingModule>()

        var buildingModuleFloorList = ArrayList<v3ModuleFloorDbBean>()

        var drawingList = ArrayList<String>()

/*        Observable.just("uploadProject")
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .subscribe({
                var list = mDb.getBuildingByProjectId(bean.localId)
                LogUtils.d("查询项目下楼表"+list.)
                dispose()
                cb(true)
            },{
                cb(false)
            })

return*/

        addDisposable(mDb.getBuildingByProjectId(bean.localId)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                it.forEach {
                    it.drawing?.forEach {
                        drawingList.add(it.localAbsPath!!)
                    }
                }
                buildingList.addAll(it)
                mDb.getFloorByProjectId(bean.localId)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                it.forEach {
                    it.drawing?.forEach {
                        drawingList.add(it.localAbsPath!!)
                    }
                }
                buildingFloorList.addAll(it)
                mDb.getv3BuildingModuleByProjectId(bean.localId)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                it.forEach {
                    it.drawings?.forEach {
                        drawingList.add(it.localAbsPath!!)
                    }
                }
                var list = ArrayList(it.map { b ->
                    BuildingModule(
                        b.projectUUID,
                        b.projectId,
                        b.buildingRemoteId,
                        b.buildingUUID,
                        b.buildingId,
                        b.uuid,
                        b.id,
                        b.moduleName,
                        b.drawings,
                        b.inspectors ?: "",
                        b.leaderId,
                        b.leaderName,
                        b.aboveGroundNumber,
                        b.underGroundNumber,
                        b.isDeleted,
                        b.superiorVersion,
                        b.parentVersion,
                        b.version,
                        b.status!!,
                        b.createTime,
                        b.deleteTime,
                        b.updateTime,
                        b.remoteId,
                        b.isChanged
                    )
                })
                buildingModuleList.addAll(list)
                mDb.getModuleFloorByProjectId(bean.localId)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .subscribe({
                dispose()
                it.forEach {
                    it.drawingsList?.forEach {
                        drawingList.add(it.localAbsPath!!)
                    }
                }

                var list = ArrayList(it.map { b ->
                    v3ModuleFloorDbBean(
                        id = b.id,
                        projectId = b.projectId,
                        bldId = b.bldId,
                        moduleId = b.moduleId,
                        floorId = b.floorId,
                        floorName = b.floorName,
                        floorType = b.floorType,
                        drawingsList = b.drawingsList,
                        remoteId = b.remoteId,
                        aboveNumber = b.aboveNumber,
                        afterNumber = b.aboveNumber,
                        version = b.version,
                        status = b.status,
                        createTime = b.createTime,
                        updateTime = b.updateTime
                    )
                })
                buildingModuleFloorList.addAll(list)

                LogUtils.d("获取项目下所有楼栋: " + buildingList)

                LogUtils.d("获取项目下所有楼栋层: " + buildingFloorList)

                LogUtils.d("获取项目下所有模块: " + buildingModuleList)

                LogUtils.d("获取项目下所有模块层: " + buildingModuleFloorList)

                LogUtils.d("获取项目下需要上传的图纸: " + drawingList)

                buildingModuleList?.forEach { buildingModule ->

                    if (buildingModule.moduleName.equals("构件检测")) {
                        buildingModuleFloorList?.forEach { buildingModuleFloor ->
                            if (buildingModuleFloor.moduleId!!.equals(buildingModule.moduleid)) {
                                buildingModuleFloor.drawingsList?.forEach { drawing ->
                                    drawing?.damage?.forEach { damage ->
                                        when (damage.type) {
                                            "梁" -> {
                                                if (damage.beamLeftRealPicList?.size!! > 0) {
                                                    drawingList.add(
                                                        damage.beamLeftRealPicList!!.get(
                                                            1
                                                        )
                                                    )
                                                }
                                                if (damage.beamLeftDesignPicList?.size!! > 0) {
                                                    drawingList.add(
                                                        damage.beamLeftDesignPicList!!.get(
                                                            1
                                                        )
                                                    )
                                                }
                                                if (damage.beamRightRealPic?.size!! > 0) {
                                                    drawingList.add(
                                                        damage.beamRightRealPic!!.get(
                                                            1
                                                        )
                                                    )
                                                }
                                                if (damage.beamRightDesignPic?.size!! > 0) {
                                                    drawingList.add(
                                                        damage.beamRightDesignPic!!.get(
                                                            1
                                                        )
                                                    )
                                                }
                                            }
                                            "柱" -> {
                                                if (damage.columnLeftRealPicList?.size!! > 0) {
                                                    drawingList.add(
                                                        damage.columnLeftRealPicList!!.get(
                                                            1
                                                        )
                                                    )
                                                }
                                                if (damage.columnLeftDesignPicList?.size!! > 0) {
                                                    drawingList.add(
                                                        damage.columnLeftDesignPicList!!.get(
                                                            1
                                                        )
                                                    )
                                                }
                                                if (damage.columnRightRealPic?.size!! > 0) {
                                                    drawingList.add(
                                                        damage.columnRightRealPic!!.get(
                                                            1
                                                        )
                                                    )
                                                }
                                                if (damage.columnRightDesignPic?.size!! > 0) {
                                                    drawingList.add(
                                                        damage.columnRightDesignPic!!.get(
                                                            1
                                                        )
                                                    )
                                                }
                                            }
                                            "墙", "板" -> {
                                                if (damage.realPicture?.size!! > 0) {
                                                    drawingList.add(damage.realPicture!!.get(1))
                                                }
                                                if (damage.designPicture?.size!! > 0) {
                                                    drawingList.add(damage.designPicture!!.get(1))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                var parts = ArrayList<MultipartBody.Part>()

                LogUtils.d("drawingList: "+drawingList.size+" ; "+drawingList)

                drawingList.forEach { path ->
                    var fileBody = RequestBody.create(MediaType.parse("image/*"), File(path))
                    var filePart = MultipartBody.Part.createFormData("files", path, fileBody)
                    parts.add(filePart)
                }

                if (parts.size > 0) {
                    HttpManager.instance.getHttpService<HttpApi>()
                        .uploadFile(parts)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                        .subscribe({ res ->
                            LogUtils.d("文件上传成功: ${res}")
                            makeUploadProject(
                                buildingList, buildingFloorList, buildingModuleList,
                                buildingModuleFloorList, res.data, bean, cb
                            )
                        }, {
                            LogUtils.d("文件上传失败: ${it}")
                            cb(false)
                        })
                } else {
                    makeUploadProject(
                        buildingList, buildingFloorList, buildingModuleList,
                        buildingModuleFloorList, ArrayList(), bean, cb
                    )
                }
            }, {
                cb(false)
            })
        )

        /*   ProjectCreateReq().also {
               it.inspectors = inspectorList
               it.leaderId = Dict.getLeaderId(bean.leader)!!
               it.leaderName = bean.leader
               it.projectName = bean.address
               it.projectId = bean.localUUID
           }*/

    }

    fun isCopyFloorDrawing(moduleName: String?): Boolean {
        if (moduleName == "建筑结构复核" ||
            moduleName == "构件检测"
        ) {
            return true
        }
        return false
    }

    var buildingList = ArrayList<BuildingBean>()

    var buildingFloorList = ArrayList<FloorBean>()

    var buildingModuleList = ArrayList<BuildingModule>()

    var buildingModuleFloorList = ArrayList<v3ModuleFloorDbBean>()

    private fun makeUploadProject(
        buildingList: ArrayList<BuildingBean>,
        buildingFloorList: ArrayList<FloorBean>,
        buildingModuleList: ArrayList<BuildingModule>,
        buildingModuleFloorList: ArrayList<v3ModuleFloorDbBean>, res: List<V3UploadDrawingRes>?,
        bean: MainProjectBean,
        cb: (Boolean) -> Unit
    ) {

        LogUtils.d("makeUploadProject")
        var inspectorList: List<String> =
            if (bean.inspector.contains("、")) bean.inspector.split("、") else Arrays.asList(bean.inspector)

        var V3UploadBuildingList = ArrayList<String>()

        var V3UploadBuildingModuleList = ArrayList<String>()

        var V3UploadBuildingReq = ArrayList<V3UploadBuildingReq>()

        var V3UploadModuleReq = ArrayList<V3UploadModuleReq>()

        var UpdateModuleVersionBeanList = ArrayList<UpdateModuleVersionBean>()//模块版本

        buildingModuleList.forEach {
            V3UploadBuildingModuleList.add(if (it.remoteId.isNullOrEmpty()) it.moduleUUID!! else it.remoteId!!)
            var V3UploadDrawingReq = ArrayList<V3UploadDrawingReq>()

            // 模块无层概念下图纸损伤数据
            if (!isCopyFloorDrawing(it.moduleName)) {

                it.drawings?.forEachIndexed { index, b ->

                    var V3UploadDamageReq = ArrayList<V3UploadDamageReq>()

                    var resId = res?.filter {
                        it.fileName.equals(b.localAbsPath)
                    }

                    b.damage?.forEach { d ->
                        V3UploadDamageReq.add(
                            V3UploadDamageReq(
                                d.annotRef,
                                d.type ?: "",
                                Gson().toJson(d),
                                resId?.get(0)?.resId ?: "",
                                b.drawingID!!,
                                b.fileName ?: "",
                                b.floorName ?: "",
                                inspectorList,
                                if (it.remoteId.isNullOrEmpty()) it.moduleUUID!! else it.remoteId!!,
                                it.moduleName ?: "",
                                it.version ?: System.currentTimeMillis()
                            )
                        )
                    }

                    V3UploadDrawingReq.add(
                        V3UploadDrawingReq(
                            if (it.buildingRemoteId.isNullOrEmpty()) it.buildingUUID!! else it.buildingRemoteId!!,
                            b.drawingID!!,
                            b.fileName!!,
                            b.fileType!!,
                            "",
                            "",
                            1,
                            index,
                            inspectorList,
                            if (it.remoteId.isNullOrEmpty()) it.moduleUUID!! else it.remoteId!!,
                            resId?.get(0)?.resId ?: "",
                            "",
                            V3UploadDamageReq
                        )
                    )
                }
            } else { //层概念下的图纸

                var currentModuleFloorList = buildingModuleFloorList.filter { bb ->
                    it.moduleid == bb.moduleId
                }

                currentModuleFloorList.forEachIndexed { index, cc ->
                    cc.drawingsList?.forEach { bbb ->
                        var V3UploadDamageReq = ArrayList<V3UploadDamageReq>()

                        var resId = res?.filter {
                            it.fileName.equals(bbb.localAbsPath)
                        }

                        bbb.damage?.forEach { ddd ->

                            when (ddd.type) {
                                "梁" -> {

                                    if (ddd.beamLeftRealPicList?.size!! > 0) {
                                        //实测草图
                                        var resId1 = res?.filter {
                                            it.fileName.equals(ddd.beamLeftRealPicList?.get(1))
                                        }
                                        ddd.beamLeftRealPicList?.add(resId1?.get(0)?.resId ?: "")
                                    }

                                    if (ddd.beamLeftDesignPicList?.size!! > 0) {
                                        //设计草图
                                        var resId2 = res?.filter {
                                            it.fileName.equals(ddd.beamLeftDesignPicList?.get(1))
                                        }
                                        ddd.beamLeftDesignPicList?.add(resId2?.get(0)?.resId ?: "")
                                    }

                                    if (ddd.beamRightRealPic?.size!! > 0) {
                                        //实测图
                                        var resId3 = res?.filter {
                                            it.fileName.equals(ddd.beamRightRealPic?.get(1))
                                        }
                                        ddd.beamRightRealPic?.add(resId3?.get(0)?.resId ?: "")
                                    }

                                    if (ddd.beamRightDesignPic?.size!! > 0) {
                                        //设计图
                                        var resId4 = res?.filter {
                                            it.fileName.equals(ddd.beamRightDesignPic?.get(1))
                                        }
                                        ddd.beamRightDesignPic?.add(resId4?.get(0)?.resId ?: "")
                                    }

                                }
                                "柱" -> {

                                    if (ddd.columnLeftRealPicList?.size!! > 0) {
                                        //实测草图
                                        var resId1 = res?.filter {
                                            it.fileName.equals(ddd.columnLeftRealPicList?.get(1))
                                        }
                                        ddd.columnLeftRealPicList?.add(resId1?.get(0)?.resId ?: "")
                                    }

                                    if (ddd.columnLeftDesignPicList?.size!! > 0) {
                                        //设计草图
                                        var resId2 = res?.filter {
                                            it.fileName.equals(ddd.columnLeftDesignPicList?.get(1))
                                        }
                                        ddd.columnLeftDesignPicList?.add(
                                            resId2?.get(0)?.resId ?: ""
                                        )
                                    }

                                    if (ddd.columnRightRealPic?.size!! > 0) {
                                        //实测图
                                        var resId3 = res?.filter {
                                            it.fileName.equals(ddd.columnRightRealPic?.get(1))
                                        }
                                        ddd.columnRightRealPic?.add(resId3?.get(0)?.resId ?: "")
                                    }

                                    if (ddd.columnRightDesignPic?.size!! > 0) {
                                        //设计图
                                        var resId4 = res?.filter {
                                            it.fileName.equals(ddd.columnRightDesignPic?.get(1))
                                        }
                                        ddd.columnRightDesignPic?.add(resId4?.get(0)?.resId ?: "")
                                    }

                                }
                                "墙", "板" -> {
                                    if (ddd.realPicture?.size!! > 0) {
                                        //实测图
                                        var resId3 = res?.filter {
                                            it.fileName.equals(ddd.realPicture?.get(1))
                                        }
                                        ddd.realPicture?.add(resId3?.get(0)?.resId ?: "")
                                    }

                                    if (ddd.designPicture?.size!! > 0) {
                                        //设计图
                                        var resId4 = res?.filter {
                                            it.fileName.equals(ddd.designPicture?.get(1))
                                        }
                                        ddd.designPicture?.add(resId4?.get(0)?.resId ?: "")
                                    }

                                }

                            }

                            V3UploadDamageReq.add(
                                V3UploadDamageReq(
                                    ddd.annotRef,
                                    ddd.type ?: "",
                                    Gson().toJson(ddd),
                                    resId?.get(0)?.resId ?: "",
                                    bbb.drawingID!!,
                                    bbb.fileName ?: "",
                                    bbb.floorName ?: "",
                                    inspectorList,
                                    if (it.remoteId.isNullOrEmpty()) it.moduleUUID!! else it.remoteId!!,
                                    it.moduleName ?: "",
                                    it.version ?: System.currentTimeMillis()
                                )
                            )
                        }

                        V3UploadDrawingReq.add(
                            V3UploadDrawingReq(
                                if (it.buildingRemoteId.isNullOrEmpty()) it.buildingUUID!! else it.buildingRemoteId!!,
                                bbb.drawingID!!,
                                bbb.fileName!!,
                                bbb.fileType!!,
                                cc.floorId!!,
                                cc.floorName ?: "",
                                cc.floorType,
                                index,
                                inspectorList,
                                if (it.remoteId.isNullOrEmpty()) it.moduleUUID!! else it.remoteId!!,
                                resId?.get(0)?.resId ?: "",
                                "",
                                V3UploadDamageReq
                            )
                        )
                    }
                }
            }

            var version = System.currentTimeMillis()
            UpdateModuleVersionBeanList.add(UpdateModuleVersionBean(it.moduleid!!,version))

            V3UploadModuleReq.add(
                com.sribs.common.bean.net.v3.V3UploadModuleReq(
                    if (it.buildingRemoteId.isNullOrEmpty()) it.buildingUUID!! else it.buildingRemoteId!!,
                    V3UploadDrawingReq,
                    inspectorList,
                    it.isChanged == 1,
                    if (it.remoteId.isNullOrEmpty()) it.moduleUUID!! else it.remoteId!!,
                    it.moduleName!!,
                    it.aboveGroundNumber ?: 0,
                    it.underGroundNumber ?: 0,
                    it.parentVersion!!,
                    it.superiorVersion!!,
                    version,
                    System.currentTimeMillis()
                )
            )
        }

        buildingList.forEach {
            var V3UploadDrawingReq = ArrayList<V3UploadDrawingReq>()

            it.drawing?.forEachIndexed { index, drawingV3Bean ->
                var resId = res?.filter {
                    it.fileName.equals(drawingV3Bean.localAbsPath)
                }

                V3UploadDrawingReq.add(
                    V3UploadDrawingReq(
                        if (it.remoteId.isNullOrEmpty()) it.UUID!! else it.remoteId!!,
                        drawingV3Bean.drawingID!!,
                        drawingV3Bean.fileName!!,
                        drawingV3Bean.fileType!!,
                        "",
                        "",
                        1,
                        index,
                        inspectorList,
                        "",
                        resId?.get(0)?.resId ?: "",
                        "",
                        ArrayList()
                    )
                )
            }

            var currentBuildingFloorList = buildingFloorList.filter { cc ->
                it.id == cc.bldId
            }

            currentBuildingFloorList.forEachIndexed { index, floorBean ->

                floorBean.drawing?.forEach { dd ->
                    var resId = res?.filter { ff ->
                        ff.fileName.equals(dd.localAbsPath)
                    }

                    V3UploadDrawingReq.add(
                        V3UploadDrawingReq(
                            if (it.remoteId.isNullOrEmpty()) it.UUID!! else it.remoteId!!,
                            dd.drawingID!!,
                            dd.fileName!!,
                            dd.fileType!!,
                            floorBean.floorId!!,
                            floorBean.floorName ?: "",
                            floorBean.floorType ?: 0,
                            index,
                            inspectorList,
                            "",
                            resId?.get(0)?.resId ?: "",
                            "",
                            ArrayList()
                        )
                    )

                }
            }

            V3UploadBuildingList.add(if (it.remoteId.isNullOrEmpty()) it.UUID!! else it.remoteId!!)
            V3UploadBuildingReq.add(
                V3UploadBuildingReq(
                    if (it.remoteId.isNullOrEmpty()) it.UUID!! else it.remoteId!!,
                    it.bldName!!,
                    it.bldType!!,
                    inspectorList,
                    it.isChanged == 1,
                    Dict.getLeaderId(bean.leader) ?: "",
                    it.leader ?: "",
                    V3UploadBuildingModuleList,
                    V3UploadModuleReq,
                    V3UploadDrawingReq,
                    it.aboveGroundNumber ?: 0,
                    it.underGroundNumber ?: 0,
                    it.parentVersion ?: 0,
                    if (it.projectRemoteId.isNullOrEmpty()) it.projectUUID!! else it.projectRemoteId!!,
                    it.superiorVersion ?: 0,
                    it.version ?: System.currentTimeMillis(),
                    TimeUtil.dateToStamp(it.createTime ?: "" + System.currentTimeMillis())
                )
            )
        }

        var V3UploadProjectReq = V3UploadProjectReq(
            V3UploadBuildingList,
            V3UploadBuildingReq,
            TimeUtil.dateToStamp(bean.createTime),
            inspectorList,
            bean.isChanged == 1,
            Dict.getLeaderId(bean.leader!!)!!,
            bean.leader!!,
            bean.parentVersion,
            if (bean.remoteId.isNullOrEmpty()) bean.localUUID!! else bean.remoteId!!,
            bean.address,
            bean.version
        )

        LogUtils.d("生成上传数据: " + Gson().toJson(V3UploadProjectReq))

        HttpManager.instance.getHttpService<HttpApi>()
            .saveV3Project(V3UploadProjectReq)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                LogUtils.d("更新项目 ischanged")
                mDb.updateProject(bean.localId, 0, 1)
            }
            .flatMap {
                LogUtils.d("更新楼 ischanged")
                mDb.updateBuildingByProjectId(bean.localId, 0, 1)
            }
            .flatMap {
                LogUtils.d("更新模块 ischanged")
                mDb.updateBuildingModuleByProjectId(bean.localId, 0, 1)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                dispose()
                var updateModuleVersionCount = 0
                if(UpdateModuleVersionBeanList.size>0){

                    UpdateModuleVersionBeanList.forEach {
                        mDb.updateBuildingModuleVersion(it.moduleId,it.version)
                            .subscribeOn(Schedulers.computation())
                            .observeOn(Schedulers.computation())
                            .subscribe({
                                LogUtils.d("更新模块版本成功 "+it)
                                updateModuleVersionCount++
                                if(updateModuleVersionCount == UpdateModuleVersionBeanList.size){
                                    mView?.onMsg("项目上传成功")
                                    //    RxBus.getDefault().post(RefreshProjectListEvent(true))
                                    cb(true)
                                }
                            },{
                            })
                    }
                }else{
                    mView?.onMsg("项目上传成功")
                    //    RxBus.getDefault().post(RefreshProjectListEvent(true))
                    cb(true)
                }
            }, {
                dispose()
                mView?.onMsg("上传项目失败: " + checkError(it))
                cb(false)
            })
    }

    private fun updateProject(bean: MainProjectBean): Observable<Long> {
        var b = bean.remoteData ?: throw NullPointerException("remoteData==null")
        return mDb.updateProject(ProjectBean(
            name = b!!.projectName,
            leader = b.leaderName,
            inspector = b.inspectors?.joinToString(separator = "、"),
            buildNo = "",
            //      createTime = Date(TimeUtil.YMD_HMS.parse(b.createTime).time),
            //      updateTime = Date(TimeUtil.YMD_HMS.parse(b.updateTime).time),
            remoteId = b.projectId
        ).also {
            if (bean.localId > 0) {
                it.id = bean.localId
            }
            var status = bean.status

        })
    }

    /**
     * 更新本地数据
     */
    fun projectUpdateV3Version(a: V3UploadProjectReq?, beanMain: MainProjectBean,cb: (Boolean, String) -> Unit) {

        var cacheRootDir: String = FileUtil.getDrawingCacheRootDir(mView!!.getContext()!!)

        var projectName = a?.projectName
        var projectLocalId = beanMain.localId
        var projectBean = ProjectBean(
            id = beanMain.localId,
            uuid = a?.projectId,
            name = a?.projectName,
            leader = a?.leaderName,
            inspector = a?.inspectors?.joinToString(separator = "、") ?: "",
            parentVersion = a?.parentVersion,
            version = a?.version,
            createTime = TimeUtil.stampToDate("" + a?.createTime),
            updateTime = TimeUtil.stampToDate("" + a?.createTime),
            remoteId = a?.projectId,
            status = 4,
        )
        addDisposable(mDb.updateProject(projectBean)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                projectLocalId = it
                LogUtils.d("更新项目成功: " + projectLocalId)
                mDb.deleteBuildingByProjectId(projectLocalId)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                LogUtils.d("项目下楼删除成功")
                mDb.deleteFloorByProjectId(projectLocalId)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                LogUtils.d("项目下楼层删除成功")
                mDb.deleteBuildingModuleByProjectId(projectLocalId)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                LogUtils.d("项目下模块删除成功")
                mDb.deleteModuleFloorByProjectId(projectLocalId)
            }
            .observeOn(Schedulers.computation())
            .subscribeOn(Schedulers.computation())
            .subscribe({


                LogUtils.d("项目下模块层删除成功 " + a)

                LogUtils.d("项目下楼数据长度 " + a?.buildings?.size)

                if(a?.buildings?.size!!<=0){
                    cb(true, "下载成功")
                }

                a?.buildings?.forEach { bb ->

                    var updateBuildingDrawing = ArrayList<DrawingV3Bean>()

                    var updateBuildingFloorDrawing =
                        HashMap<String, ArrayList<DrawingV3Bean>>()

                    var floorSortBean = ArrayList<FloorSortBean>()

                    var mCurDrawingsDir =
                        "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + projectName + "/" + bb.buildingNo + "/"

                    bb.drawings?.forEach { dd ->
                        var drawingLocalPath: String
                        if (dd.floorNo.isNullOrEmpty()) {
                            drawingLocalPath = File(
                                cacheRootDir + mCurDrawingsDir,
                                dd.drawingName
                            ).absolutePath
                        } else {
                            drawingLocalPath = File(
                                cacheRootDir + mCurDrawingsDir + dd.floorNo,
                                dd.drawingName
                            ).absolutePath
                        }
                        var DrawingV3Bean = DrawingV3Bean(
                            dd.drawingId,
                            dd.drawingName,
                            dd.fileType,
                            "overall",
                            drawingLocalPath,
                            dd.resId,
                            dd.sort,
                            ArrayList()
                        )

                        if (dd.floorNo.isNullOrEmpty()) {
                            updateBuildingDrawing.add(DrawingV3Bean)
                        } else {
                            floorSortBean.add(
                                FloorSortBean(
                                    dd.floorId,
                                    dd.floorNo,
                                    dd.direction,
                                    DrawingV3Bean.sort!!
                                )
                            )
                            var buildingFloorDrawingList =
                                updateBuildingFloorDrawing.get(dd.floorNo)
                            if (buildingFloorDrawingList == null) {
                                buildingFloorDrawingList = ArrayList()
                            }
                            buildingFloorDrawingList.add(DrawingV3Bean)
                            updateBuildingFloorDrawing.put(
                                dd.floorNo,
                                buildingFloorDrawingList
                            )
                        }

                    }


                    var buildingBean = BuildingBean(
                        -1,
                        bb.buildingId,
                        a?.projectId,
                        a?.projectId,
                        projectLocalId,
                        bb.buildingNo,
                        "all",
                        TimeUtil.stampToDate("" + bb.createTime),
                        TimeUtil.stampToDate("" + bb.createTime),
                        "",
                        0,
                        bb.leaderName,
                        bb.inspectors?.joinToString(separator = "、") ?: "",
                        a?.version,
                        bb.parentVersion,
                        bb.version,
                        bb.buildingId,
                        4,
                        updateBuildingDrawing,
                        bb.aboveGroundNumber,
                        bb.underGroundNumber,
                        0
                    )
                    LogUtils.d("创建楼数据: " + buildingBean)
                    mDb.createLocalBuilding(buildingBean)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(Schedulers.computation())
                        .subscribe({ BUIDINGID ->
                            LogUtils.d("创建楼成功: " + BUIDINGID)

                            /**
                             * 创建楼层
                             */
                            var floorSortList =
                                ArrayList(floorSortBean.sortedBy { b -> b.sort })

                            floorSortList.forEach { ff ->
                                LogUtils.d("当前楼层: " + ff)
                                var drawingV3Bean =
                                    updateBuildingFloorDrawing.get(ff.floorNo)

                                var appFloor = FloorBean(
                                    -1,
                                    projectLocalId,
                                    BUIDINGID,
                                    -1,
                                    ff.floorId,
                                    ff.floorNo,
                                    ff.direction,
                                    TimeUtil.stampToDate("" + System.currentTimeMillis()),
                                    TimeUtil.stampToDate("" + System.currentTimeMillis()),
                                    "",
                                    bb.inspectors?.joinToString(
                                        separator = "、" ?: ""
                                    ),
                                    bb.version,
                                    bb.projectId,
                                    0,
                                    drawingV3Bean,
                                    bb.aboveGroundNumber,
                                    bb.underGroundNumber,
                                )

                                LogUtils.d("创建楼层数据: " + appFloor)
                                mDb.createLocalFloor(appFloor)
                                    .subscribeOn(Schedulers.computation())
                                    .observeOn(Schedulers.computation())
                                    .subscribe({
                                        if (bb.modules.size <= 0) {
                                            cb(true, "下载成功")
                                        }
                                        LogUtils.d("创建楼层成功: " + it)
                                    }, {
                                        mView?.onMsg("部分楼层创建失败")
                                    })
                            }

                            /**
                             * 创建楼下模块
                             */

                            bb.modules?.forEach { mm ->

                                var updateModuleDrawing = ArrayList<DrawingV3Bean>()

                                var updateModuleFloorDrawing =
                                    HashMap<String, ArrayList<DrawingV3Bean>>()

                                var moduleFloorSortBean = ArrayList<FloorSortBean>()

                                mCurDrawingsDir =
                                    "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + projectName + "/" + bb.buildingNo + "/" + mm.moduleName + "/"

                                mm.drawings.forEach { dd ->

                                    var drawingLocalPath: String
                                    if (dd.floorNo.isNullOrEmpty()) {
                                        drawingLocalPath = File(
                                            cacheRootDir + mCurDrawingsDir,
                                            dd.drawingName
                                        ).absolutePath
                                    } else {
                                        drawingLocalPath = File(
                                            cacheRootDir + mCurDrawingsDir + dd.floorNo,
                                            dd.drawingName
                                        ).absolutePath
                                    }
                                    var damageV3List = ArrayList<DamageV3Bean>()
                                    dd.damageMixes?.forEach {damage->
                                        var damageV3Bean =
                                            Gson().fromJson(damage.desc, DamageV3Bean::class.java)
                                        if(mm.moduleName == "构件检测"){
                                            when(damageV3Bean.type){
                                                "梁"->{
                                                    if (damageV3Bean?.beamLeftRealPicList?.size!! > 1) {
                                                        damageV3Bean?.beamLeftRealPicList?.set(1,File(
                                                            cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                            damageV3Bean?.beamLeftRealPicList?.get(0)
                                                        ).absolutePath)
                                                    }

                                                    if (damageV3Bean?.beamLeftDesignPicList?.size!! > 1) {
                                                        damageV3Bean?.beamLeftDesignPicList?.set(1,File(
                                                            cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                            damageV3Bean?.beamLeftDesignPicList?.get(0)
                                                        ).absolutePath)
                                                    }

                                                    if (damageV3Bean?.beamRightRealPic?.size!! > 1) {
                                                        damageV3Bean?.beamRightRealPic?.set(1,File(
                                                            cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                            damageV3Bean?.beamRightRealPic?.get(0)
                                                        ).absolutePath)
                                                    }

                                                    if (damageV3Bean?.beamRightDesignPic?.size!! > 1) {
                                                        damageV3Bean?.beamRightDesignPic?.set(1,File(
                                                            cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                            damageV3Bean?.beamRightDesignPic?.get(0)
                                                        ).absolutePath)
                                                    }

                                                }
                                                "柱"->{
                                                    if (damageV3Bean?.columnLeftRealPicList?.size!! > 1) {
                                                        damageV3Bean?.columnLeftRealPicList?.set(1,File(
                                                            cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                            damageV3Bean?.columnLeftRealPicList?.get(0)
                                                        ).absolutePath)
                                                    }

                                                    if (damageV3Bean?.columnLeftDesignPicList?.size!! > 1) {
                                                        damageV3Bean?.columnLeftDesignPicList?.set(1,File(
                                                            cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                            damageV3Bean?.columnLeftDesignPicList?.get(0)
                                                        ).absolutePath)
                                                    }

                                                    if (damageV3Bean?.columnRightRealPic?.size!! > 1) {
                                                        damageV3Bean?.columnRightRealPic?.set(1,File(
                                                            cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                            damageV3Bean?.columnRightRealPic?.get(0)
                                                        ).absolutePath)
                                                    }

                                                    if (damageV3Bean?.columnRightDesignPic?.size!! > 1) {
                                                        damageV3Bean?.columnRightDesignPic?.set(1,File(
                                                            cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                            damageV3Bean?.columnRightDesignPic?.get(0)
                                                        ).absolutePath)
                                                    }

                                                }
                                                "墙","板"->{

                                                    if (damageV3Bean?.realPicture?.size!! > 1) {
                                                        damageV3Bean?.realPicture?.set(1,File(
                                                            cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                            damageV3Bean?.realPicture?.get(0)
                                                        ).absolutePath)
                                                    }

                                                    if (damageV3Bean?.designPicture?.size!! > 1) {
                                                        damageV3Bean?.designPicture?.set(1,File(
                                                            cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                            damageV3Bean?.designPicture?.get(0)
                                                        ).absolutePath)
                                                    }
                                                }
                                            }
                                        }
                                        damageV3List?.add(damageV3Bean)
                                    }


                                    var DrawingV3Bean = DrawingV3Bean(
                                        dd.drawingId,
                                        dd.drawingName,
                                        dd.fileType,
                                        "overall",
                                        drawingLocalPath,
                                        dd.resId,
                                        dd.sort,
                                        damageV3List
                                    )

                                    if (dd.floorNo.isNullOrEmpty()) {
                                        updateModuleDrawing.add(DrawingV3Bean)
                                    } else {
                                        moduleFloorSortBean.add(
                                            FloorSortBean(
                                                dd.floorId,
                                                dd.floorNo,
                                                dd.direction,
                                                DrawingV3Bean.sort!!
                                            )
                                        )
                                        var moduleFloorDrawingList =
                                            updateModuleFloorDrawing.get(dd.floorNo)
                                        if (moduleFloorDrawingList == null) {
                                            moduleFloorDrawingList = ArrayList()
                                        }
                                        moduleFloorDrawingList.add(DrawingV3Bean)
                                        updateModuleFloorDrawing.put(
                                            dd.floorNo,
                                            moduleFloorDrawingList
                                        )
                                    }
                                }

                                var v3BuildingModuleDbBean = v3BuildingModuleDbBean(
                                    -1,
                                    mm.moduleId,
                                    bb.buildingId,
                                    bb.buildingId,
                                    BUIDINGID,
                                    a?.projectId,
                                    projectLocalId,
                                    mm.moduleName,
                                    a?.leaderId,
                                    a?.leaderName,
                                    0,
                                    mm.aboveGroundNumber,
                                    mm.underGroundNumber,
                                    updateModuleDrawing,
                                    mm.inspectors?.joinToString(separator = "、"),
                                    "",
                                    TimeUtil.stampToDate("" + mm.createTime),
                                    "",
                                    mm.moduleId,
                                    bb.version,
                                    mm.parentVersion,
                                    mm.version,
                                    4,
                                    0
                                )

                                mDb.updatev3BuildingModule(v3BuildingModuleDbBean)
                                    .subscribeOn(Schedulers.computation())
                                    .observeOn(Schedulers.computation())
                                    .subscribe({ MODULEID ->
                                        LogUtils.d(mm.moduleName + " 模块创建成功 " + it)
                                        if (moduleFloorSortBean.size <= 0) {
                                            dispose()
                                            cb(true, "下载成功")
                                        }

                                        /**
                                         * 创建模块层
                                         */

                                        var moduleFloorSortList =
                                            ArrayList(moduleFloorSortBean.sortedBy { b -> b.sort })

                                        moduleFloorSortList.forEach { ff ->
                                            var drawingV3Bean =
                                                updateModuleFloorDrawing.get(ff.floorNo)
                                            var v3ModuleFloorDbBean =
                                                v3ModuleFloorDbBean(
                                                    id = -1,
                                                    projectId = projectLocalId,
                                                    bldId = BUIDINGID,
                                                    moduleId = MODULEID,
                                                    floorId = ff.floorId,
                                                    floorName = ff.floorNo,
                                                    floorType = ff.direction,
                                                    drawingsList = drawingV3Bean,
                                                    remoteId = mm.moduleId,
                                                    aboveNumber = mm.aboveGroundNumber,
                                                    afterNumber = mm.underGroundNumber,
                                                    version = 1,
                                                    status = 0,
                                                    createTime = TimeUtil.YMD_HMS.format(
                                                        Date()
                                                    ),
                                                    updateTime = TimeUtil.YMD_HMS.format(
                                                        Date()
                                                    ),
                                                )
                                            mDb.updatev3ModuleFloor(
                                                v3ModuleFloorDbBean
                                            )
                                                .subscribeOn(Schedulers.computation())
                                                .observeOn(Schedulers.computation())
                                                .subscribe({
                                                    LogUtils.d("创建模块层成功: " + it)
                                                    cb(true, "下载成功")
                                                    dispose()
                                                }, {
                                                    LogUtils.d("部分模块层创建失败")
                                                    dispose()
                                                })
                                        }
                                        dispose()
                                    }, {
                                        dispose()
                                    })
                            }
                            dispose()
                        }, {
                            dispose()
                        })

                }
                dispose()
            }, {
                dispose()
            }))
    }


    /**
     * V3下载指定版本的项目
     */
    fun projectV3DownloadConfig(
        beanMain: MainProjectBean,
        remoteProjectId: String,
        version: String,
        cb: (Boolean, String) -> Unit
    ) {

        var list = ArrayList<V3DownloadReq>()
        list.add(V3DownloadReq(remoteProjectId, version))

        var V3UploadProjectReq: V3UploadProjectReq?

        var projectName = beanMain.name

        var uploadPDFSuccess = true

        var downloadMsg = "下载成功"

        var cacheRootDir: String = FileUtil.getDrawingCacheRootDir(mView!!.getContext()!!)

        var needDownloadDrawingList = ArrayList<V3UploadDrawingRes>()

        addDisposable(
            HttpManager.instance.getHttpService<HttpApi>()
                .downloadV3ProjectVersionList(V3VersionDownloadReq(list))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe({
                    checkResult(it)
                    var a = it.data?.get(0)
                    V3UploadProjectReq = a
                    LogUtils.d("当前版本数据: " + V3UploadProjectReq)

                    V3UploadProjectReq?.buildings?.forEach { bb ->
                        var mCurDrawingsDir =
                            "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + projectName + "/" + bb.buildingNo + "/"
                        bb.drawings.forEach { dd ->
                            var drawingLocalPath = ""
                            if (dd.floorNo.isNullOrEmpty()) {
                                drawingLocalPath = File(
                                    cacheRootDir + mCurDrawingsDir,
                                    dd.drawingName
                                ).absolutePath
                            } else {
                                drawingLocalPath = File(
                                    cacheRootDir + mCurDrawingsDir + dd.floorNo,
                                    dd.drawingName
                                ).absolutePath
                            }
                            needDownloadDrawingList.add(
                                V3UploadDrawingRes(
                                    dd.resId,
                                    drawingLocalPath
                                )
                            )
                        }

                        /**
                         * 过滤出最新模块记录
                         */
                        var iterator = bb.modules.iterator()
                        var filterModuleList = ArrayList<V3UploadModuleReq>()
                        var ModuleList = bb.modules

                        while (iterator.hasNext()){
                            var next = iterator.next()
                            var isSmallTime = false
                            ModuleList.forEach { filter->
                                if(filter.moduleName == next.moduleName
                                    && next.createTime< filter.createTime){
                                    isSmallTime = true
                                }
                            }
                            if(!isSmallTime){
                                filterModuleList.add(next)
                            }
                        }

                        bb.modules.clear()

                        bb.modules.addAll(filterModuleList)

                        bb.modules?.forEach { mm ->
                            mCurDrawingsDir =
                                "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + projectName + "/" + bb.buildingNo + "/" + mm.moduleName + "/"
                            mm?.drawings?.forEach { dd ->
                                var drawingLocalPath = ""
                                if (dd.floorNo.isNullOrEmpty()) {
                                    drawingLocalPath = File(
                                        cacheRootDir + mCurDrawingsDir,
                                        dd.drawingName
                                    ).absolutePath
                                } else {
                                    drawingLocalPath = File(
                                        cacheRootDir + mCurDrawingsDir + dd.floorNo,
                                        dd.drawingName
                                    ).absolutePath
                                }
                                needDownloadDrawingList.add(
                                    V3UploadDrawingRes(
                                        dd.resId,
                                        drawingLocalPath
                                    )
                                )

                                if (mm.moduleName.equals("构件检测")) {
                                    dd.damageMixes.forEach { damage ->
                                        var damageV3Bean =
                                            Gson().fromJson(damage.desc, DamageV3Bean::class.java)
                                        when (damageV3Bean?.type) {
                                            "梁" -> {
                                                if (damageV3Bean?.beamLeftRealPicList?.size!! > 0) {
                                                    drawingLocalPath = File(
                                                        cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                        damageV3Bean?.beamLeftRealPicList?.get(0)
                                                    ).absolutePath
                                                    needDownloadDrawingList.add(
                                                        V3UploadDrawingRes(
                                                            damageV3Bean?.beamLeftRealPicList?.get(2)!!,
                                                            drawingLocalPath
                                                        )
                                                    )
                                                }

                                                if (damageV3Bean?.beamLeftDesignPicList?.size!! > 0) {
                                                    drawingLocalPath = File(
                                                        cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                        damageV3Bean?.beamLeftDesignPicList?.get(0)
                                                    ).absolutePath
                                                    needDownloadDrawingList.add(
                                                        V3UploadDrawingRes(
                                                            damageV3Bean?.beamLeftDesignPicList?.get(
                                                                2
                                                            )!!,
                                                            drawingLocalPath
                                                        )
                                                    )
                                                }

                                                if (damageV3Bean?.beamRightRealPic?.size!! > 0) {
                                                    drawingLocalPath = File(
                                                        cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                        damageV3Bean?.beamRightRealPic?.get(0)
                                                    ).absolutePath
                                                    needDownloadDrawingList.add(
                                                        V3UploadDrawingRes(
                                                            damageV3Bean?.beamRightRealPic?.get(2)!!,
                                                            drawingLocalPath
                                                        )
                                                    )
                                                }

                                                if (damageV3Bean?.beamRightDesignPic?.size!! > 0) {
                                                    drawingLocalPath = File(
                                                        cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                        damageV3Bean?.beamRightDesignPic?.get(0)
                                                    ).absolutePath
                                                    needDownloadDrawingList.add(
                                                        V3UploadDrawingRes(
                                                            damageV3Bean?.beamRightDesignPic?.get(2)!!,
                                                            drawingLocalPath
                                                        )
                                                    )
                                                }

                                            }
                                            "柱" -> {
                                                if (damageV3Bean?.columnLeftRealPicList?.size!! > 0) {
                                                    drawingLocalPath = File(
                                                        cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                        damageV3Bean?.columnLeftRealPicList?.get(0)
                                                    ).absolutePath
                                                    needDownloadDrawingList.add(
                                                        V3UploadDrawingRes(
                                                            damageV3Bean?.columnLeftRealPicList?.get(
                                                                2
                                                            )!!,
                                                            drawingLocalPath
                                                        )
                                                    )
                                                }

                                                if (damageV3Bean?.columnLeftDesignPicList?.size!! > 0) {
                                                    drawingLocalPath = File(
                                                        cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                        damageV3Bean?.columnLeftDesignPicList?.get(0)
                                                    ).absolutePath
                                                    needDownloadDrawingList.add(
                                                        V3UploadDrawingRes(
                                                            damageV3Bean?.columnLeftDesignPicList?.get(
                                                                2
                                                            )!!,
                                                            drawingLocalPath
                                                        )
                                                    )
                                                }

                                                if (damageV3Bean?.columnRightRealPic?.size!! > 0) {
                                                    drawingLocalPath = File(
                                                        cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                        damageV3Bean?.columnRightRealPic?.get(0)
                                                    ).absolutePath
                                                    needDownloadDrawingList.add(
                                                        V3UploadDrawingRes(
                                                            damageV3Bean?.columnRightRealPic?.get(2)!!,
                                                            drawingLocalPath
                                                        )
                                                    )
                                                }

                                                if (damageV3Bean?.columnRightDesignPic?.size!! > 0) {
                                                    drawingLocalPath = File(
                                                        cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                        damageV3Bean?.columnRightDesignPic?.get(0)
                                                    ).absolutePath
                                                    needDownloadDrawingList.add(
                                                        V3UploadDrawingRes(
                                                            damageV3Bean?.columnRightDesignPic?.get(
                                                                2
                                                            )!!,
                                                            drawingLocalPath
                                                        )
                                                    )
                                                }

                                            }
                                            "墙", "板" -> {
                                                if (damageV3Bean?.realPicture?.size!! > 0) {
                                                    drawingLocalPath = File(
                                                        cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                        damageV3Bean?.realPicture?.get(0)
                                                    ).absolutePath
                                                    needDownloadDrawingList.add(
                                                        V3UploadDrawingRes(
                                                            damageV3Bean?.realPicture?.get(2)!!,
                                                            drawingLocalPath
                                                        )
                                                    )
                                                }

                                                if (damageV3Bean?.designPicture?.size!! > 0) {
                                                    drawingLocalPath = File(
                                                        cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                        damageV3Bean?.designPicture?.get(0)
                                                    ).absolutePath
                                                    needDownloadDrawingList.add(
                                                        V3UploadDrawingRes(
                                                            damageV3Bean?.designPicture?.get(2)!!,
                                                            drawingLocalPath
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    LogUtils.d("需要下载的图纸个数: " + needDownloadDrawingList.size)
                    var uploadDrawingIndex = 0
                    if(needDownloadDrawingList?.size<=0){
                        projectUpdateV3Version(V3UploadProjectReq, beanMain,cb)
                    }

                    needDownloadDrawingList?.forEach { res ->
                        LogUtils.d("下载的图纸: "+res)
                        HttpManager.instance.getHttpService<HttpApi>()
                            .downloadFile(res.resId)
                            .subscribeOn(Schedulers.computation())
                            .observeOn(Schedulers.computation())
                            .subscribe({
                                LogUtils.d("图纸下载本地成功")
                                var file = File(res.fileName)
                                if (!file.parentFile.exists()) {
                                    file.parentFile.mkdirs()
                                }
                                Files.copy(
                                    it.byteStream(),
                                    file.toPath(),
                                    StandardCopyOption.REPLACE_EXISTING
                                )
                                uploadDrawingIndex++
                                if (uploadDrawingIndex == needDownloadDrawingList.size) {
                                    if (uploadPDFSuccess) {
                                        LogUtils.d("图纸全部下载成功")
                                        projectUpdateV3Version(V3UploadProjectReq, beanMain,cb)
                                    } else {
                                        cb(false,downloadMsg)
                                        LogUtils.d("图纸下载失败" + downloadMsg)
                                    }
                                }
                            }, {
                                uploadPDFSuccess = false
                                downloadMsg = "图纸下载失败: " + it.message
                                LogUtils.d("图纸下载本地失败 " + it + " ; " + it.message)
                                uploadDrawingIndex++
                                if (uploadDrawingIndex == needDownloadDrawingList.size) {
                                    if (uploadPDFSuccess) {
                                        LogUtils.d("图纸全部下载成功")
                                        projectUpdateV3Version(V3UploadProjectReq, beanMain,cb)
                                    } else {
                                        cb(false,downloadMsg)
                                        LogUtils.d("图纸下载失败" + downloadMsg)
                                    }
                                }
                            })
                    }
                }, {
                    cb(false, checkError(it))
                })
        )
    }

    override fun projectDownLoadConfig(
        historyId: String,
        bean: MainProjectBean,
        cb: (Boolean) -> Unit
    ) {
        var resConfig: ProjectConfigDownloadRes? = null
        var resNo = 0;
        addDisposable(HttpManager.instance.getHttpService<HttpApi>()
            .projectConfigDownload(ProjectDownloadReq(historyId))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .concatMap { //更新Project
                checkResult(it)
                resConfig = it.data!!
                updateProject(bean)

            }
            .map {
                if (bean.localId > 0) bean.localId else it
            }
            .observeOn(Schedulers.computation())
            .concatMap { //更新 unit
                checkUnitConfig(it, 1, resConfig!!.configUnits)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                cb(it)
            }, {
                mView?.onMsg(checkError(it))
                cb(false)
            })
        )
    }

    override fun ConfigBean(
        projectId: Long,
        bldId: Long,
        unitId: Long,
        configId: Long,
        floorIdx: Int?,
        configType: Int?,
        floorNum: String,
        createTime: Date?,
        updateTime: Date?,
    ): ConfigBean {
        TODO("Not yet implemented")
    }

    private fun createPartNo(b: ConfigBean): String = when (b.configType) {
        UnitConfigType.CONFIG_TYPE_FLOOR_BOTTOM.value -> b.floorNum + "层"
        UnitConfigType.CONFIG_TYPE_FLOOR_NORMAL.value -> b.floorNum + "层"
        UnitConfigType.CONFIG_TYPE_FLOOR_TOP.value -> b.floorNum + "层"
        UnitConfigType.CONFIG_TYPE_UNIT_BOTTOM.value -> b.floorNum + b.neighborNum + "室"
        UnitConfigType.CONFIG_TYPE_UNIT_NORMAL.value -> b.floorNum + b.neighborNum + "室"
        UnitConfigType.CONFIG_TYPE_UNIT_TOP.value -> b.floorNum + b.neighborNum + "室"
        else -> ""
    }

    private fun updateHouseStatus() {

    }


    private fun searchLocalConfig(
        projectId: Long,
        unitId: Long,
        remoteUnit: RecordUnit
    ) {
        mDb.getUnitConfigOnce(unitId).toObservable()
            .subscribe({
                LOG.I("123", "it=$it")
                remoteUnit.parts.forEach { r ->//查找configId
                    var localConfigBean =
                        it.find { local -> r.partNo.contains(createPartNo(local)) }
                    if (localConfigBean != null) {
                        var configId = localConfigBean.configId


                    } else { //error 有记录缺没有配置
                    }
                }
            }, {
                it.printStackTrace()
            }, {

            })
    }

    private fun searchLocalUnit(units: List<RecordUnit>): Observable<Long> {
        var obList = ArrayList<Observable<List<UnitBean>>>()
        units.forEach {
            LOG.I("123", "remoteId=${it.unitId}")
            obList.add(mDb.getUnitOnce(it.unitId).toObservable())
        }
        return Observable.merge(obList)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                if (it.isEmpty()) {
                    throw MsgThrowable("记录的单元id与配置的单元id不符")
                }
                var localUnit = it[0]
                var projectId = localUnit.projectId!!
                var unitId = localUnit.unitId!!
                var remoteUnit = units.find { r -> r.unitId == localUnit.remoteId }
                checkConfigRecord(
                    projectId,
                    unitId,
                    remoteUnit!!.parts,
                    remoteUnit.createTime,
                    remoteUnit.updateTime
                )
            }
    }

    private fun downloadProjectRecord(
        historyId: String,
        cb: (Boolean) -> Unit
    ) {
        var unitIdx = 0
        addDisposable(HttpManager.instance.getHttpService<HttpApi>()
            .projectRecordDownload(ProjectDownloadReq(historyId))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .flatMap {
                checkResult(it)
                searchLocalUnit(it.data!!.units)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                unitIdx++
                LOG.I("123", "单元 idx:$unitIdx  更新:$it")
                cb(true)//更新了至少一个单元
            }, {
                mView?.onMsg(checkError(it))
                cb(false)
            })


        )
    }

    override fun projectDownLoadRecord(
        configHistoryId: String,
        recordHistoryId: String,
        bean: MainProjectBean,
        cb: (Boolean) -> Unit
    ) {
        projectDownLoadConfig(configHistoryId, bean) {
            LOG.I("123", "downLoadProjectConfig  $it")
            downloadProjectRecord(recordHistoryId, cb)
        }
    }

    override fun projectGetLocalSummary(
        projectId: Long,
        cb: (ProjectSummaryRes) -> Unit
    ) {
        var allCount = 0

        mDb.getAllUnitOnce(projectId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                it.forEach { u ->
                    var t = u.floorSize?.times(u.neighborSize ?: 0)
                    allCount += t ?: 0
                }
                mDb.getHouseStatusByProjectOnce(projectId).toObservable()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                var roomList = it.filter { s -> s.houseType == 2 }
                var noAccess = roomList.filter { s ->
                    s.houseStatus?.contains("不让进") ?: false && !(s.houseStatus?.contains(
                        "无人"
                    )
                        ?: false)
                }.count()
                var noOne = roomList.filter { s ->
                    !(s.houseStatus?.contains("不让进")
                        ?: false) && s.houseStatus?.contains("无人") ?: false
                }.count()
                var both = roomList.filter { s ->
                    s.houseStatus?.contains("不让进") ?: false && s.houseStatus?.contains("无人") ?: false
                }.count()
                var finish = roomList.filter { s -> s.isFinish == 1 }.count()
                var res = ProjectSummaryRes(
                    allCount,
                    finish,
                    noAccess + both,
                    noOne + both,
                    (finish - noAccess - noOne - both).toFloat() * 100f / allCount
                )
                cb(res)
            }, {
                it.printStackTrace()
            })
    }

    override fun projectGetSummary(
        remoteProjectId: String,
        cb: (ProjectSummaryRes) -> Unit
    ) {
        HttpManager.instance.getHttpService<HttpApi>()
            .getProjectSummary(ProjectSummaryReq(remoteProjectId))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                checkResult(it)
                cb(it.data!!)
            }, {
                mView?.onMsg(checkError(it))
            })
    }

    /**
     * 三期删除
     */
    override fun projectDelete(beanMain: MainProjectBean) {
        //删除本地数据库
        if (beanMain.localId > 0) {
            LogUtils.d("删除本地数据")
            var obList = ArrayList<Observable<Boolean>>()
            var projectId = beanMain.localId
            // 3期
            obList.add(mDb.deleteProject(ProjectBean(id = projectId)).map { true })
            obList.add(mDb.deleteBuildingByProjectId(projectId)) //删除本地项目下的所有本地楼
            obList.add(mDb.deleteFloorByProjectId(projectId)) //删除本地项目下的所有本地楼层
            obList.add(mDb.deleteBuildingModuleByProjectId(projectId)) //删除本地项目下的所有楼模块
            obList.add(mDb.deleteModuleFloorByProjectId(projectId)) //删除本地项目下的所有楼模块层

            //2期
            obList.add(mDb.deleteUnit(projectId))
            obList.add(mDb.deleteConfig(projectId))
            obList.add(mDb.deleteHouseStatusByProject(projectId))
            obList.add(mDb.deleteRoomStatusByProject(projectId))
            obList.add(mDb.deleteRoomDetailByProject(projectId))
            obList.add(mDb.deleteReportByProjectId(projectId))
            var count = 0
            Observable.merge(obList)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .subscribe({
                    count++
                    LOG.I("123", "delete project count=$count  $it")
                }, {
                    it.printStackTrace()
                })
        }

        if (!beanMain.remoteId.isNullOrEmpty()) {
            //删除网络数据

            var dd = V3VersionDeleteReq(
                beanMain.remoteId,
                0,
                beanMain.version
            )
            LogUtils.d("删除网络数据 " + Gson().toJson(dd))
            HttpManager.instance.getHttpService<HttpApi>()
                .deleteProject(
                    V3VersionDeleteReq(
                        beanMain.remoteId,
                        beanMain.parentVersion,
                        beanMain.version
                    )
                )
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .subscribe({
                    checkResult(it)
                    LogUtils.d("删除远端项目成功：" + it.toString())
                    RxBus.getDefault().post(RefreshProjectListEvent(true))
                    mView?.onMsg("删除项目成功")
                }, {
                    LogUtils.d("删除远端项目成功：" + checkError(it))
                    mView?.onMsg("删除远端项目失败" + checkError(it))
                })
        }
    }


    override fun bindView(v: IBaseView) {
        mView = v as IMainListContrast.IMainView
    }

    override fun unbindView() {
        dispose()
        mView = null
    }

    override fun getContext(): Context = mView?.getContext()!!
}