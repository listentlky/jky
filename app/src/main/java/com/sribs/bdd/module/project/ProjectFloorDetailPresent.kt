package com.sribs.bdd.module.project

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libnet.http.HttpManager
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.Config
import com.sribs.bdd.R
import com.sribs.bdd.action.Dict
import com.sribs.bdd.bean.BuildingModule
import com.sribs.bdd.utils.ModuleHelper
import com.sribs.bdd.utils.UUIDUtil
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.V3VersionBean
import com.sribs.common.bean.db.v3.project.v3BuildingModuleDbBean
import com.sribs.common.bean.net.v3.V3BuildingModuleListRes
import com.sribs.common.bean.net.v3.V3VersionDeleteReq
import com.sribs.common.bean.net.v3.V3VersionDownloadReq
import com.sribs.common.bean.net.v3.V3VersionReq
import com.sribs.common.bean.v3.v3ModuleFloorDbBean

import com.sribs.common.module.BasePresenter
import com.sribs.common.net.HttpApi
import com.sribs.common.server.IDatabaseService
import com.sribs.common.utils.FileUtil
import com.sribs.common.utils.TimeUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
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
     * 查询楼建筑下的所有版本模块列表
     */
    fun getRemoteModule(mBuildingRemoteId: String, version: Int) {
        LogUtils.d("请求网络楼模块下数据")
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
                        b.inspectors,
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
                            beanMain.parentVersion!!,
                            beanMain.version!!
                        )
                    )
                    .subscribeOn(Schedulers.io())
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
    fun uploadModule(beanMain: BuildingModule) {
        LogUtils.d("上传模块配置: " + beanMain)
        var floorDrawingsMap: HashMap<String?, Any?> = HashMap()
        var inspectorList: List<String> =
            if (beanMain.inspectors!!.contains("、")) beanMain.inspectors!!.split("、") else Arrays.asList(
                beanMain.inspectors!!
            )
        addDisposable(
            HttpManager.instance.getHttpService<HttpApi>()
                .saveBuildingModule(V3BuildingModuleListRes().also {
                    it.buildingId = beanMain.buildingUUID!!
                    it.moduleId = beanMain.moduleUUID!!
                    it.moduleName = beanMain.moduleName!!
                    it.inspectors = inspectorList
                    it.leaderId = Dict.getLeaderId(beanMain.leaderName!!)!!
                    it.leaderName = beanMain.leaderName!!
                    it.aboveGroundNumber = "" + beanMain.aboveGroundNumber
                    it.underGroundNumber = "" + beanMain.underGroundNumber
                    it.drawings = ArrayList()
                    it.floorDrawings = floorDrawingsMap

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    LogUtils.d("上传云端模块成功: " + it.toString())
                }, {
                    LogUtils.d("上传云端模块失败: " + it.toString())
                })
        )
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
                    cb(ArrayList(it.data!!.records.map {
                        V3VersionBean(
                            it.projectId,
                            it.projectName,
                            it.leaderName,
                            it.leaderId,
                            it.inspectors,
                            it.parentVersion,
                            it.version,
                            it.createTime
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
        remoteModuleId: String,
        version: Int,
        cb: (Boolean) -> Unit
    ) {
        addDisposable(
            HttpManager.instance.getHttpService<HttpApi>()
                .downloadV3ModuleVersionList(V3VersionDownloadReq(remoteModuleId, version))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe({
                    LogUtils.d("下载的模块版本数据: " + it)
                    cb(true)
                }, {
                    cb(false)
                })
        )
        /*.concatMap {
            LogUtils.d("下载的项目版本数据")

        }*/
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
        LogUtils.d("创建本地模块")
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

        addDisposable(mDb.getLocalBuildingOnce(buildingId).toObservable()
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

                LogUtils.d("it[0].drawing： " + it[0].drawing)
                mModuleUUID = UUIDUtil.getUUID(moduleName!!)
                mInspectors = it[0].inspectorName
                mLeader = it[0].leader
                aboveGroundNumber = it[0].aboveGroundNumber
                underGroundNumber = it[0].underGroundNumber

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
                cc.updateTime = TimeUtil.YMD_HMS.format(Date())
                cc.aboveGroundNumber = aboveGroundNumber
                cc.underGroundNumber = underGroundNumber
                cc.isChanged = true

                LogUtils.d("查询到楼栋下面的数据包装进 model表 : " + cc)

                mDb.updatev3BuildingModule(cc)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LogUtils.d("本地楼层model创建成功 " + it)
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
            }, {
                LogUtils.d("本地楼层model创建失败 : " + it)
            })
        )
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
                        isChanged = true
                    )
                })
                LogUtils.d("查询包装 model楼层表 : " + floorList)
                addDisposable(
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
                )

                if (Config.isNetAvailable) {
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

                }

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