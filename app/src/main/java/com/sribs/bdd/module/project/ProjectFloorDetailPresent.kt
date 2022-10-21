package com.sribs.bdd.module.project

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.rxbus.RxBus
import com.cbj.sdk.libnet.http.HttpManager
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.google.gson.Gson
import com.sribs.bdd.Config
import com.sribs.bdd.R
import com.sribs.bdd.action.Dict
import com.sribs.bdd.bean.BuildingModule
import com.sribs.bdd.bean.FloorSortBean
import com.sribs.bdd.utils.ModuleHelper
import com.sribs.bdd.utils.UUIDUtil
import com.sribs.bdd.v3.event.RefreshProjectListEvent
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.V3VersionBean
import com.sribs.common.bean.db.DamageV3Bean
import com.sribs.common.bean.db.DrawingV3Bean
import com.sribs.common.bean.db.v3.project.v3BuildingModuleDbBean
import com.sribs.common.bean.net.v3.*
import com.sribs.common.bean.v3.v3ModuleFloorDbBean

import com.sribs.common.module.BasePresenter
import com.sribs.common.net.HttpApi
import com.sribs.common.server.IDatabaseService
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
import java.util.*
import kotlin.collections.ArrayList

class ProjectFloorDetailPresent : BasePresenter(), IProjectContrast.IProjectFloorDetailPresent {

    private var mView: IProjectContrast.IProjectFloorDetailView? = null

    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB)
            .navigation() as IDatabaseService
    }

    private val mStateArr by lazy {
        mView?.getContext()?.resources?.getStringArray(R.array.main_project_status) ?: emptyArray()
    }

    /**
     * 查询楼建筑下的所有模块列表
     */
    fun getRemoteModule(mBuildingRemoteId: String, version: Long) {
        LogUtils.d("请求网络楼模块下数据 " + version)
        addDisposable(
            HttpManager.instance.getHttpService<HttpApi>()
                .getV3BuildingModuleList(mBuildingRemoteId, version)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    checkResult(it)
                    LogUtils.d("查询楼建筑下的模块： " + it)

                }, {
                    mView?.onMsg(checkError(it))
                })
        )
    }

    override fun getLocalModule(mLocalProjectId: Long, mBuildingId: Long) {

        mDb.getv3BuildingModule(mLocalProjectId, mBuildingId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LogUtils.d("查询到本地模块数据: " + it)
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
                        mStateArr[b.status ?: 0],
                        b.createTime,
                        b.deleteTime,
                        b.updateTime,
                        b.remoteId,
                        b.isChanged
                    )
                })
                mView?.handlItemList(list)
            }, {
                mView?.handlItemList(ArrayList())
                it.printStackTrace()
            })
    }

    /**
     * 删除模块
     */
    fun deleteModule(beanMain: BuildingModule) {
        LogUtils.d("删除本地模块")
        if (beanMain.moduleid!! > 0) {
            addDisposable(mDb.deletev3BuildingModule(v3BuildingModuleDbBean(id = beanMain.moduleid))
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .flatMap {
                    mDb.deletev3ModuleFloor(
                        beanMain.projectId!!,
                        beanMain.buildingId!!,
                        beanMain.moduleid!!
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    LogUtils.d("删除本地数据成功")
                }, {
                    it.printStackTrace()
                })
            )
        }

        if (!beanMain.remoteId.isNullOrEmpty()) {
            addDisposable(
                HttpManager.instance.getHttpService<HttpApi>()
                    .deleteBuildingModule(
                        V3VersionDeleteReq(
                            beanMain.remoteId!!,
                            beanMain.superiorVersion!!,
                            beanMain.version!!
                        )
                    )
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        checkResult(it)
                        LogUtils.d("删除远端模块成功：" + it.toString())
                        mView?.onMsg("删除模块成功")
                    }, {
                        mView?.onMsg("删除模块失败" + checkError(it))
                    })
            )
        }
    }

    /**
     * 上传模块配置
     */
    fun uploadModule(bean: BuildingModule, cb: (Boolean) -> Unit) {
        LogUtils.d("上传模块配置: " + bean)

        var buildingModuleFloorList = ArrayList<v3ModuleFloorDbBean>()

        var drawingList = ArrayList<String>()

        bean.drawings?.forEach {
            drawingList.add(it.localAbsPath!!)
        }

        addDisposable(
            mDb.getModuleFloorByModule(bean.moduleid!!)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .subscribe({
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

                    LogUtils.d("获取项目下所有模块层: " + buildingModuleFloorList)

                    LogUtils.d("获取项目下需要上传的图纸: " + drawingList)

                    var parts = ArrayList<MultipartBody.Part>()

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
                                    buildingModuleFloorList, res.data, bean, cb
                                )
                            }, {
                                LogUtils.d("文件上传失败: ${it}")
                                cb(false)
                            })
                    } else {
                        makeUploadProject(
                            buildingModuleFloorList, ArrayList(), bean, cb
                        )
                    }
                }, {
                    cb(false)
                })
        )

    }

    private fun makeUploadProject(
        buildingModuleFloorList: java.util.ArrayList<v3ModuleFloorDbBean>,
        res: List<V3UploadDrawingRes>?,
        bean: BuildingModule,
        cb: (Boolean) -> Unit
    ) {

        LogUtils.d("makeUploadProject")
        var inspectorList: List<String> =
            if (bean.inspectors.contains("、")) bean.inspectors.split("、") else Arrays.asList(bean.inspectors)

        var V3UploadDrawingReq = ArrayList<V3UploadDrawingReq>()

        // 模块无层概念下图纸损伤数据
        if (!isCopyFloorDrawing(bean.moduleName)) {

            bean.drawings?.forEachIndexed { index, b ->

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
                            b.fileName ?: "",
                            b.floorName ?: "",
                            inspectorList,
                            if (bean.remoteId.isNullOrEmpty()) bean.moduleUUID!! else bean.remoteId!!,
                            bean.moduleName ?: "",
                            bean.version ?: System.currentTimeMillis()
                        )
                    )
                }

                V3UploadDrawingReq.add(
                    V3UploadDrawingReq(
                        if (bean.buildingRemoteId.isNullOrEmpty()) bean.buildingUUID!! else bean.buildingRemoteId!!,
                        "drawing:" + resId?.get(0)?.resId,
                        b.fileName!!,
                        b.fileType!!,
                        "",
                        1,
                        index,
                        inspectorList,
                        if (bean.remoteId.isNullOrEmpty()) bean.moduleUUID!! else bean.remoteId!!,
                        resId?.get(0)?.resId ?: "",
                        "",
                        V3UploadDamageReq
                    )
                )
            }
        } else { //层概念下的图纸

            var currentModuleFloorList = buildingModuleFloorList.filter { bb ->
                bean.moduleid == bb.moduleId
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
                                bbb.fileName ?: "",
                                bbb.floorName ?: "",
                                inspectorList,
                                if (bean.remoteId.isNullOrEmpty()) bean.moduleUUID!! else bean.remoteId!!,
                                bean.moduleName ?: "",
                                bean.version ?: System.currentTimeMillis()
                            )
                        )
                    }

                    V3UploadDrawingReq.add(
                        V3UploadDrawingReq(
                            if (bean.buildingRemoteId.isNullOrEmpty()) bean.buildingUUID!! else bean.buildingRemoteId!!,
                            "drawing:" + resId?.get(0)?.resId,
                            bbb.fileName!!,
                            bbb.fileType!!,
                            cc.floorName ?: "",
                            cc.floorType,
                            index,
                            inspectorList,
                            if (bean.remoteId.isNullOrEmpty()) bean.moduleUUID!! else bean.remoteId!!,
                            resId?.get(0)?.resId ?: "",
                            "",
                            V3UploadDamageReq
                        )
                    )
                }
            }
        }


        var V3UploadModuleReq = V3UploadModuleReq(
            if (bean.buildingRemoteId.isNullOrEmpty()) bean.buildingUUID!! else bean.buildingRemoteId!!,
            V3UploadDrawingReq,
            inspectorList,
            bean.isChanged == 1,
            if (bean.remoteId.isNullOrEmpty()) bean.moduleUUID!! else bean.remoteId!!,
            bean.moduleName!!,
            bean.aboveGroundNumber ?: 0,
            bean.underGroundNumber ?: 0,
            bean.parentVersion!!,
            bean.superiorVersion!!,
            bean.version!!,
            System.currentTimeMillis()
        )

        LogUtils.d("生成上传数据: " + Gson().toJson(V3UploadModuleReq))

        HttpManager.instance.getHttpService<HttpApi>()
            .saveBuildingModule(V3UploadModuleReq)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                LogUtils.d("更新模块 ischanged")
                mDb.updateBuildingModule(bean.moduleid!!, 0,1)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                dispose()
                mView?.onMsg("模块上传成功")
                //    RxBus.getDefault().post(RefreshProjectListEvent(true))
                cb(true)
            }, {
                mView?.onMsg("上传模块失败: " + checkError(it))
                cb(false)
            })

    }

    /**
     * 获取模块版本
     */
    fun getV3ModuleVersionHistory(
        mBuildingRemoteId: String,
        mModuleRemoteId: String,
        cb: (ArrayList<V3VersionBean>?) -> Unit
    ) {
        addDisposable(
            HttpManager.instance.getHttpService<HttpApi>()
                .getV3ModuleVersionList(V3VersionReq(mBuildingRemoteId, mModuleRemoteId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    checkResult(it)
                    LogUtils.d("查询模块版本列表：" + it)
                    cb(ArrayList(it.data!!.map {
                        V3VersionBean(
                            it.projectId,
                            it.projectName,
                            it.leaderName?:"",
                            it.leaderId?:"",
                            it.inspectors,
                            it.parentVersion,
                            it.version,
                            TimeUtil.stampToDate(it.createTime)
                        )
                    }))
                }, {
                    LogUtils.d("查询模块版本列表失败：" + it)
                    mView?.onMsg(ERROR_HTTP)
                    cb(null)
                    it.printStackTrace()
                })
        )
    }

    /**
     * 下载指定版本模块
     */
    fun downloadModuleConfig(
        projectName: String,
        bldName:String,
        beanMain: BuildingModule,
        remoteModuleId: String,
        version: String,
        cb: (Boolean,String) -> Unit
    ) {
        var list = ArrayList<V3DownloadReq>()
        list.add(V3DownloadReq(remoteModuleId, version))

        var uploadPDFSuccess = true

        var downloadMsg = "下载成功"

        var needDownloadDrawingList = ArrayList<V3UploadDrawingRes>()

        cacheRootDir = FileUtil.getDrawingCacheRootDir(mView!!.getContext()!!)

        addDisposable(
            HttpManager.instance.getHttpService<HttpApi>()
                .downloadV3ModuleVersionList(V3VersionDownloadReq(list))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    checkResult(it)

                    var a = it.data?.get(0)
                    LogUtils.d("下载的模块版本数据: " + it)

                    mCurDrawingsDir =
                        "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + projectName + "/" + bldName + "/" + a?.moduleName + "/"


                    a?.drawings?.forEach { dd->

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

                        if (a?.moduleName.equals("构建测量")) {
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
                                                    drawingLocalPath,
                                                    damageV3Bean?.beamLeftRealPicList?.get(2)!!
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
                                                    drawingLocalPath,
                                                    damageV3Bean?.beamLeftDesignPicList?.get(
                                                        2
                                                    )!!
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
                                                    drawingLocalPath,
                                                    damageV3Bean?.beamRightRealPic?.get(2)!!
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
                                                    drawingLocalPath,
                                                    damageV3Bean?.beamRightDesignPic?.get(2)!!
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
                                                    drawingLocalPath,
                                                    damageV3Bean?.columnLeftRealPicList?.get(
                                                        2
                                                    )!!
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
                                                    drawingLocalPath,
                                                    damageV3Bean?.columnLeftDesignPicList?.get(
                                                        2
                                                    )!!
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
                                                    drawingLocalPath,
                                                    damageV3Bean?.columnRightRealPic?.get(2)!!
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
                                                    drawingLocalPath,
                                                    damageV3Bean?.columnRightDesignPic?.get(
                                                        2
                                                    )!!
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
                                                    drawingLocalPath,
                                                    damageV3Bean?.realPicture?.get(2)!!
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
                                                    drawingLocalPath,
                                                    damageV3Bean?.designPicture?.get(2)!!
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    LogUtils.d("需要下载的图纸个数: " + needDownloadDrawingList.size)
                    var uploadDrawingIndex = 0

                    needDownloadDrawingList?.forEach { res ->
                        HttpManager.instance.getHttpService<HttpApi>()
                            .downloadFile(res.resId)
                            .subscribeOn(Schedulers.computation())
                            .observeOn(Schedulers.computation())
                            .subscribe({
                                LogUtils.d("图纸下载本地成功 "+res)
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
                                        moduleUpdateV3Version(a, projectName,bldName,beanMain,cb)
                                    } else {
                                        cb(false,downloadMsg)
                                        LogUtils.d("图纸下载失败" + downloadMsg)
                                    }
                                }
                            }, {
                                uploadPDFSuccess = false
                                downloadMsg = "图纸下载失败: " + it.message
                                LogUtils.d(downloadMsg)
                                uploadDrawingIndex++
                                if (uploadDrawingIndex == needDownloadDrawingList.size) {
                                    if (uploadPDFSuccess) {
                                        LogUtils.d("图纸全部下载成功")
                                        moduleUpdateV3Version(a,projectName,bldName, beanMain,cb)
                                    } else {
                                        cb(false,downloadMsg)
                                        LogUtils.d("图纸下载失败" + downloadMsg)
                                    }
                                }
                            })
                    }

                }, {
                    LogUtils.d("下载的模块版本失败: " + checkError(it))
                    dispose()
                    cb(false,checkError(it))
                })
        )
    }

    private fun moduleUpdateV3Version(
        a: V3UploadModuleReq?,
        projectName: String,
        bldName:String,
        beanMain: BuildingModule,
        cb: (Boolean, String) -> Unit
    ) {

        var updateModuleDrawing = ArrayList<DrawingV3Bean>()

        var moduleFloorSortBean = ArrayList<FloorSortBean>()

        var updateModuleFloorDrawing =
            HashMap<String, ArrayList<DrawingV3Bean>>()

        cacheRootDir = FileUtil.getDrawingCacheRootDir(mView!!.getContext()!!)

        mDb.deleteModuleFloorByModuleId(beanMain.moduleid!!)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .subscribe({

                mCurDrawingsDir =
                    "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + projectName + "/" + bldName + "/" + a?.moduleName + "/"

                a?.drawings?.forEach { dd ->

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
                        if(a?.moduleName == "构建检测"){
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
                        -1,
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
                    beanMain.moduleid,
                    a?.moduleId,
                    a?.buildingId,
                    a?.buildingId,
                    beanMain.buildingId,
                    beanMain.projectUUID,
                    beanMain.projectId,
                    a?.moduleName,
                    beanMain.leaderId,
                    beanMain.leaderName,
                    0,
                    a?.aboveGroundNumber,
                    a?.underGroundNumber,
                    updateModuleDrawing,
                    a?.inspectors?.joinToString(separator = "、"),
                    "",
                    TimeUtil.stampToDate("" + a?.createTime),
                    "",
                    a?.moduleId,
                    a?.superiorVersion,
                    a?.parentVersion,
                    a?.version,
                    4,
                    0
                )

                mDb.updatev3BuildingModule(v3BuildingModuleDbBean)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.computation())
                    .subscribe({ MODULEID ->
                        if(moduleFloorSortBean.size<=0){
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
                                    projectId = beanMain.projectId,
                                    bldId = beanMain.buildingId,
                                    moduleId = MODULEID,
                                    floorId = -1,
                                    floorName = ff.floorNo,
                                    floorType = ff.direction,
                                    drawingsList = drawingV3Bean,
                                    remoteId = a?.moduleId,
                                    aboveNumber = a?.aboveGroundNumber,
                                    afterNumber = a?.underGroundNumber,
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
                    },{
                        dispose()
                    })

                dispose()
            },{
                dispose()
            })

    }


    var cacheRootDir = ""
    var mCurDrawingsDir = ""

    fun createOrSaveModule(
        projectId: Long,
        projectUUID: String,
        buildingId: Long,
        buildingUUID: String,
        remoteId: String?,
        projectName: String,
        buildingName: String,
        moduleName: String?,
    ) {
        LogUtils.d("创建本地模块 ")
        /**
         * 查询copy楼栋下的图纸
         */
        cacheRootDir = FileUtil.getDrawingCacheRootDir(mView!!.getContext()!!)
        mCurDrawingsDir =
            "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + projectName + "/" + buildingName + "/" + moduleName + "/"

        //页面数据
        var cc = v3BuildingModuleDbBean()
        var mModuleUUID: String? = ""
        var mInspectors: String? = ""
        var mLeader: String? = ""
        var aboveGroundNumber: Int? = 0
        var underGroundNumber: Int? = 0

        mDb.getLocalBuildingOnce(buildingId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                it[0].drawing?.forEachIndexed { index, item ->
                    var cacheFileParent = File(cacheRootDir + mCurDrawingsDir + index)
                    cacheFileParent.mkdirs()
                    var cacheFile = File(cacheFileParent, item.fileName)
                    LogUtils.d("当前缓存地址: " + cacheFile.absolutePath)
                    FileUtil.copyTo(File(item.localAbsPath), cacheFile)
                    item.localAbsPath = cacheFile.absolutePath
                }

                LogUtils.d("查询楼： " + it)
                mModuleUUID = UUIDUtil.getUUID(moduleName!!)
                mInspectors = it[0].inspectorName
                mLeader = it[0].leader
                aboveGroundNumber = it[0].aboveGroundNumber
                underGroundNumber = it[0].underGroundNumber

                cc.id = -1
                cc.uuid = mModuleUUID
                cc.projectUUID = projectUUID
                cc.projectId = projectId
                cc.buildingUUID = buildingUUID
                cc.buildingId = buildingId
                cc.remoteId = remoteId
                cc.moduleName = moduleName
                cc.drawings = it[0].drawing
                cc.inspectors = mInspectors
                cc.leaderId = Dict.getLeaderId(mLeader!!)
                cc.leaderName = mLeader
                cc.createTime = TimeUtil.YMD_HMS.format(Date())
                cc.updateTime = TimeUtil.YMD_HMS.format(Date())
                cc.aboveGroundNumber = aboveGroundNumber
                cc.underGroundNumber = underGroundNumber
                cc.superiorVersion = it[0].version
                cc.version = System.currentTimeMillis()
                cc.isChanged = 1

                LogUtils.d("查询到楼栋下面的数据包装进 model表 : " + cc)

                mDb.updatev3BuildingModule(cc)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .subscribe({
                LogUtils.d("本地楼层model创建成功 " + it)
                if (isCopyFloorDrawing(moduleName)) {
                    createModuleFloor(
                        it,
                        mModuleUUID!!,
                        moduleName!!,
                        projectId,
                        projectUUID,
                        buildingId,
                        buildingUUID,
                        remoteId,
                        mInspectors,
                        mLeader,
                        aboveGroundNumber,
                        underGroundNumber
                    )
                }
            }, {
                LogUtils.d("本地楼层model创建失败 : " + it)
            })
    }

    fun isCopyFloorDrawing(moduleName: String?): Boolean {
        if (moduleName == "建筑结构复核" ||
            moduleName == "构建检测"
        ) {
            return true
        }
        return false
    }

    fun createModuleFloor(
        moduleId: Long,
        moduleUUID: String,
        moduleName: String,
        projectId: Long,
        projectUUID: String,
        buildingId: Long,
        buildingUUID: String,
        remoteId: String?,
        mInspectors: String?,
        mLeader: String?,
        aboveGroundNumber: Int?,
        underGroundNumber: Int?
    ) {
        LogUtils.d("copy 层关系图纸")

        mDb.getLocalFloorsInTheBuilding(buildingId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .subscribe({
                var floorList = ArrayList(it.map { b ->
                    b.drawing?.forEachIndexed { index, item ->
                        var cacheFileParent =
                            File(cacheRootDir + mCurDrawingsDir + b.floorName + "/" + index)
                        cacheFileParent.mkdirs()
                        var cacheFile = File(cacheFileParent, item.fileName)
                        LogUtils.d("当前模块楼层缓存地址: " + cacheFile.absolutePath)
                        FileUtil.copyTo(File(item.localAbsPath), cacheFile)
                        item.localAbsPath = cacheFile.absolutePath
                    }

                    v3ModuleFloorDbBean(
                        id = -1,
                        projectId = projectId,
                        bldId = buildingId,
                        moduleId = moduleId,
                        floorId = b.floorId,
                        floorName = b.floorName,
                        floorType = b.floorType ?: 0,
                        drawingsList = b.drawing,
                        remoteId = remoteId,
                        aboveNumber = b.aboveGroundNumber,
                        afterNumber = b.underGroundNumber,
                        version = 1,
                        status = 0,
                        createTime = TimeUtil.YMD_HMS.format(Date()),
                        updateTime = TimeUtil.YMD_HMS.format(Date()),
                    )
                })
                LogUtils.d("查询包装 model楼层表 : " + floorList)

                Observable.fromIterable(floorList)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.computation())
                    .flatMap {
                        mDb.updatev3ModuleFloor(it)
                    }.observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        LogUtils.d("存储到model 楼层表 : " + it)
                    }, {

                    })


                /*    if (Config.isNetAvailable) {
                        LogUtils.d("有网进行云端模块创建: ")
                        var floorDrawingsMap: HashMap<String?, Any?> = HashMap()
                        var inspectorList: List<String> =
                            if (mInspectors!!.contains("、")) mInspectors.split("、") else Arrays.asList(
                                mInspectors
                            )
                        addDisposable(
                            HttpManager.instance.getHttpService<HttpApi>()
                                .saveBuildingModule(V3BuildingModuleListRes().also {
                                    it.buildingId = buildingUUID
                                    it.moduleId = moduleUUID
                                    it.moduleName = moduleName
                                    it.inspectors = inspectorList
                                    it.leaderId = Dict.getLeaderId(mLeader!!)!!
                                    it.leaderName = mLeader
                                    it.aboveGroundNumber = "" + aboveGroundNumber
                                    it.underGroundNumber = "" + underGroundNumber
                                    it.drawings = ArrayList()
                                    it.floorDrawings = floorDrawingsMap

                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    LogUtils.d("创建云端模块成功: " + it.toString())
                                }, {
                                    LogUtils.d("创建云端模块失败: " + it.toString())
                                })
                        )

                    }*/

            }, {
                LogUtils.d("存储到model 楼层表失败 : " + it)
            })
    }

    fun saveModuleFloor(
        floorList: ArrayList<v3ModuleFloorDbBean>,
        onResult: (Long) -> Unit
    ) {
        addDisposable(
            Observable.fromIterable(floorList)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .flatMap {
                    mDb.updatev3ModuleFloor(it)
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                }, {

                })
        )

    }

    override fun bindView(v: IBaseView) {
        mView = v as IProjectContrast.IProjectFloorDetailView
    }

    override fun unbindView() {
        mView = null
    }
}