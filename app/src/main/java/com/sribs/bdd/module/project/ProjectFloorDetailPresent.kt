package com.sribs.bdd.module.project

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libnet.http.HttpManager
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.google.gson.Gson
import com.sribs.bdd.R
import com.sribs.bdd.action.Dict
import com.sribs.bdd.bean.BuildingModule
import com.sribs.bdd.bean.FloorSortBean
import com.sribs.bdd.utils.ModuleHelper
import com.sribs.bdd.utils.UUIDUtil
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
    fun getRemoteModule(
        mLocalProjectId: Long,
        mLocalProjectUUID: String,
        mBuildingRemoteId: String,
        mBuildingId: Long,
        mVersion: Long,
        localList: ArrayList<BuildingModule>
    ) {
        LogUtils.d("请求网络楼模块下数据 ${mBuildingRemoteId}  ${mVersion}")
        addDisposable(
            HttpManager.instance.getHttpService<HttpApi>()
                .getV3BuildingModuleList(mBuildingRemoteId, mVersion)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    checkResult(it)
                    LogUtils.d("查询楼建筑下的模块： " + it)

                    if (it.data == null || it.data?.size!! <= 0) {
                        mView?.onMsg("云端未查到数据")
                        return@subscribe
                    }

                    /*  it.data!!.forEach { remoteBean ->
                          var i = localList.indexOfFirst { localBean ->
                              !localBean.remoteId.isNullOrEmpty() && localBean.remoteId == remoteBean.moduleId
                          }

                          if (i >= 0) {
                              var localBean = localList[i]
                              //判断时间
                              if (TimeUtil.isBefore(
                                      localBean.createTime!!,
                                      TimeUtil.stampToDate("" + remoteBean.createTime)
                                  )
                              ) {
                                //  localList[i].hasNewer = true
                              }
                              localBean.moduleUUID = remoteBean.moduleId
                          }
                      }*/

                    /*   it.data!!.forEach { remoteBean ->
                           var i = localList.indexOfFirst { localBean ->
                               localBean.remoteId.isNullOrEmpty() &&
                                       localBean.moduleUUID == remoteBean.moduleId
                           }
                           if (i >= 0) {
                               var localBean = localList[i]
                               //判断时间
                               if (TimeUtil.isBefore(
                                       localBean.createTime!!,
                                       TimeUtil.stampToDate("" + remoteBean.createTime)
                                   )
                               ) {
                                //   localList[i].hasNewer = true
                               }
                               localBean.remoteId = remoteBean.moduleId
                           }
                       }*/

                    var moduleMainBeanList = ArrayList<BuildingModule>()

                    // 本地中没有
                    var onlyRemoteList = it.data!!.filter { remoteBean ->
                        localList.find { localBean ->
                            localBean.moduleName == remoteBean.moduleName
                        } == null
                    }?.map { b ->

                        /* var drawingV3Bean = ArrayList(b.drawings.map { drawing->DrawingV3Bean(
                             -1,
                             drawing.drawingName,
                             drawing.fileType,
                             drawing.

                         )
                         }

                         )*/

                        BuildingModule(
                            projectUUID = mLocalProjectUUID,
                            projectId = mLocalProjectId,
                            buildingRemoteId = mBuildingRemoteId,
                            buildingUUID = mBuildingRemoteId,
                            buildingId = mBuildingId,
                            moduleUUID = b.moduleId,
                            moduleid = -1,
                            moduleName = b.moduleName,
                            inspectors = b.inspectors?.joinToString(separator = "、") ?: "",
                            superiorVersion = mVersion,
                            parentVersion = b.parentVersion,
                            version = b.version,
                            status = 2,
                            createTime = TimeUtil.stampToDate("" + b.createTime),
                            updateTime = TimeUtil.stampToDate("" + b.createTime),
                            remoteId = b.moduleId,
                            isChanged = 0,
                            drawings = ArrayList(),
                            aboveGroundNumber = b.aboveGroundNumber,
                            underGroundNumber = b.underGroundNumber,
                            isDeleted = 0,
                            leaderId = "",
                            leaderName = "",
                            deleteTime = ""
                        )
                    }


                    /**
                     * 过滤出最新模块记录
                     */
                    var iterator = onlyRemoteList?.iterator()
                    var filterModuleList = ArrayList<BuildingModule>()
                    var ModuleList = onlyRemoteList

                    while (iterator!!.hasNext()) {
                        var next = iterator.next()
                        var isSmallTime = false
                        ModuleList?.forEach { filter ->
                            if (filter.moduleName == next.moduleName
                                && TimeUtil.dateToStamp(next.createTime!!) < TimeUtil.dateToStamp(
                                    filter.createTime!!
                                )
                            ) {
                                isSmallTime = true
                            }
                        }
                        if (!isSmallTime) {
                            filterModuleList.add(next)
                        }
                    }

                    moduleMainBeanList.addAll(filterModuleList)
                    mView?.handlRemoteItemList(ArrayList(moduleMainBeanList.sortedByDescending { b -> b.createTime }))
                    mView?.onMsg("更新成功")
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
                        b.status!!,
                        b.createTime,
                        b.deleteTime,
                        b.updateTime,
                        b.remoteId,
                        b.isChanged
                    )
                })
                mView?.handlItemList(ArrayList(list.sortedByDescending { b -> b.createTime }))
            }, {
                LogUtils.d("获取模块数据失败： "+it)
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

        /*   if (beanMain.status != "0") {
               addDisposable(
                   HttpManager.instance.getHttpService<HttpApi>()
                       .deleteBuildingModule(
                           V3VersionDeleteReq(
                               beanMain.moduleUUID!!,
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
           }*/
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

                    if (bean.moduleName.equals("构件检测")) {
                        buildingModuleFloorList?.forEach { buildingModuleFloor ->
                            if (buildingModuleFloor.moduleId!!.equals(bean.moduleid)) {
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
                    } else if (bean.moduleName == "倾斜测量") {
                        bean.drawings?.forEach { drawing ->
                            drawing.damage?.forEach { damage ->
                                when (damage.type) {
                                    "点位" -> {
                                        if (damage.scalePath?.size!! > 0) {
                                            drawingList.add(damage.scalePath!!.get(1))
                                        }
                                    }
                                }
                            }
                        }
                    }

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

                var drawingID = UUIDUtil.getUUID(b.fileName!!)

                b.damage?.forEach { d ->

                    when (d.type) {
                        "点位" -> {
                            if (d.scalePath?.size!! > 0) {
                                //点位放大图
                                var resId1 = res?.filter {
                                    it.fileName.equals(d.scalePath?.get(1))
                                }
                                d.scalePath?.add(resId1?.get(0)?.resId ?: "")
                            }
                        }
                    }

                    V3UploadDamageReq.add(
                        V3UploadDamageReq(
                            d.annotRef,
                            d.type ?: "",
                            Gson().toJson(d),
                            resId?.get(0)?.resId ?: "",
                            drawingID,
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
                        drawingID,
                        b.fileName!!,
                        b.fileType!!,
                        "",
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

                    var drawingID = UUIDUtil.getUUID(bbb.fileName!!)

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
                                drawingID,
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
                            drawingID,
                            bbb.fileName!!,
                            bbb.fileType!!,
                            cc.floorId!!,
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

        var version = System.currentTimeMillis()

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
            version,
            System.currentTimeMillis()
        )

        LogUtils.d("生成上传数据: " + Gson().toJson(V3UploadModuleReq))

        HttpManager.instance.getHttpService<HttpApi>()
            .saveBuildingModule(V3UploadModuleReq)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                LogUtils.d("更新模块 ischanged")
                mDb.updateBuildingModule(bean.moduleid!!, 0, 1)
            }
            .flatMap {
                mDb.updateBuildingModuleVersion(bean.moduleid!!, version)
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
        bldName: String,
        beanMain: BuildingModule,
        remoteModuleId: String,
        version: String,
        cb: (Boolean, String) -> Unit
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


                    a?.drawings?.forEach { dd ->

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

                        if (a?.moduleName.equals("构件检测")) {
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
                        } else if (a?.moduleName == "倾斜测量") {
                            dd.damageMixes?.forEach { damage ->
                                var damageV3Bean =
                                    Gson().fromJson(damage.desc, DamageV3Bean::class.java)
                                when (damageV3Bean.type) {
                                    "点位" -> {
                                        if (damageV3Bean.scalePath?.size!! > 0) {

                                            drawingLocalPath = File(
                                                cacheRootDir + mCurDrawingsDir + "/damage/" + damageV3Bean?.type,
                                                damageV3Bean?.scalePath?.get(0)
                                            ).absolutePath

                                            needDownloadDrawingList.add(
                                                V3UploadDrawingRes(
                                                    damageV3Bean?.scalePath?.get(2)!!,
                                                    drawingLocalPath
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
                        LogUtils.d("下载的图纸: " + res)
                        HttpManager.instance.getHttpService<HttpApi>()
                            .downloadFile(res.resId)
                            .subscribeOn(Schedulers.computation())
                            .observeOn(Schedulers.computation())
                            .subscribe({
                                LogUtils.d("图纸下载本地成功 " + res)
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
                                        moduleUpdateV3Version(
                                            a,
                                            projectName,
                                            bldName,
                                            beanMain,
                                            cb
                                        )
                                    } else {
                                        cb(false, downloadMsg)
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
                                        moduleUpdateV3Version(
                                            a,
                                            projectName,
                                            bldName,
                                            beanMain,
                                            cb
                                        )
                                    } else {
                                        cb(false, downloadMsg)
                                        LogUtils.d("图纸下载失败" + downloadMsg)
                                    }
                                }
                            })
                    }

                }, {
                    LogUtils.d("下载的模块版本失败: " + checkError(it))
                    dispose()
                    cb(false, checkError(it))
                })
        )
    }

    private fun moduleUpdateV3Version(
        a: V3UploadModuleReq?,
        projectName: String,
        bldName: String,
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
                    dd.damageMixes?.forEach { damage ->
                        var damageV3Bean =
                            Gson().fromJson(damage.desc, DamageV3Bean::class.java)
                        if (a?.moduleName == "构件检测") {
                            when (damageV3Bean.type) {
                                "梁" -> {
                                    if (damageV3Bean?.beamLeftRealPicList?.size!! > 1) {
                                        damageV3Bean?.beamLeftRealPicList?.set(
                                            1, File(
                                                cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                damageV3Bean?.beamLeftRealPicList?.get(
                                                    0
                                                )
                                            ).absolutePath
                                        )
                                    }

                                    if (damageV3Bean?.beamLeftDesignPicList?.size!! > 1) {
                                        damageV3Bean?.beamLeftDesignPicList?.set(
                                            1, File(
                                                cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                damageV3Bean?.beamLeftDesignPicList?.get(
                                                    0
                                                )
                                            ).absolutePath
                                        )
                                    }

                                    if (damageV3Bean?.beamRightRealPic?.size!! > 1) {
                                        damageV3Bean?.beamRightRealPic?.set(
                                            1, File(
                                                cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                damageV3Bean?.beamRightRealPic?.get(
                                                    0
                                                )
                                            ).absolutePath
                                        )
                                    }

                                    if (damageV3Bean?.beamRightDesignPic?.size!! > 1) {
                                        damageV3Bean?.beamRightDesignPic?.set(
                                            1, File(
                                                cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                damageV3Bean?.beamRightDesignPic?.get(
                                                    0
                                                )
                                            ).absolutePath
                                        )
                                    }

                                }
                                "柱" -> {
                                    if (damageV3Bean?.columnLeftRealPicList?.size!! > 1) {
                                        damageV3Bean?.columnLeftRealPicList?.set(
                                            1, File(
                                                cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                damageV3Bean?.columnLeftRealPicList?.get(
                                                    0
                                                )
                                            ).absolutePath
                                        )
                                    }

                                    if (damageV3Bean?.columnLeftDesignPicList?.size!! > 1) {
                                        damageV3Bean?.columnLeftDesignPicList?.set(
                                            1, File(
                                                cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                damageV3Bean?.columnLeftDesignPicList?.get(
                                                    0
                                                )
                                            ).absolutePath
                                        )
                                    }

                                    if (damageV3Bean?.columnRightRealPic?.size!! > 1) {
                                        damageV3Bean?.columnRightRealPic?.set(
                                            1, File(
                                                cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                damageV3Bean?.columnRightRealPic?.get(
                                                    0
                                                )
                                            ).absolutePath
                                        )
                                    }

                                    if (damageV3Bean?.columnRightDesignPic?.size!! > 1) {
                                        damageV3Bean?.columnRightDesignPic?.set(
                                            1, File(
                                                cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                damageV3Bean?.columnRightDesignPic?.get(
                                                    0
                                                )
                                            ).absolutePath
                                        )
                                    }

                                }
                                "墙", "板" -> {

                                    if (damageV3Bean?.realPicture?.size!! > 1) {
                                        damageV3Bean?.realPicture?.set(
                                            1, File(
                                                cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                damageV3Bean?.realPicture?.get(0)
                                            ).absolutePath
                                        )
                                    }

                                    if (damageV3Bean?.designPicture?.size!! > 1) {
                                        damageV3Bean?.designPicture?.set(
                                            1, File(
                                                cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                damageV3Bean?.designPicture?.get(0)
                                            ).absolutePath
                                        )
                                    }
                                }
                            }
                        }else if (a?.moduleName == "倾斜测量") {
                            if (damageV3Bean?.scalePath?.size!! > 1) {
                                damageV3Bean?.scalePath?.set(
                                    1, File(
                                        cacheRootDir + mCurDrawingsDir + "/damage/" + damageV3Bean?.type,
                                        damageV3Bean?.scalePath?.get(0)
                                    ).absolutePath
                                )
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
                    beanMain.superiorVersion,
                    a?.parentVersion,
                    a?.version,
                    4,
                    0
                )

                mDb.updatev3BuildingModule(v3BuildingModuleDbBean)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.computation())
                    .subscribe({ MODULEID ->
                        if (moduleFloorSortBean.size <= 0) {
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
                                    floorId = ff.floorId,
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
                    }, {
                        dispose()
                    })

                dispose()
            }, {
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
     //   moduleName: String?,
    moduleNameList:ArrayList<String>,
    ) {
        LogUtils.d("创建本地模块 "+moduleNameList)

        if(moduleNameList.size<=0){
            return
        }

        /**
         * 查询copy楼栋下的图纸
         */

        mDb.getLocalBuildingOnce(buildingId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .subscribe({
                var index = 0
                Timer().schedule(object :TimerTask(){
                    override fun run() {
                        while (index<moduleNameList.size){

                            var moduleName = moduleNameList.get(index)

                            index++

                            var  cacheRootDir = FileUtil.getDrawingCacheRootDir(mView!!.getContext()!!)
                            var  mCurDrawingsDir =
                                "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + projectName + "/" + buildingName + "/" + moduleName + "/"

                            if(!isCopyFloorDrawing(moduleName)){
                                it[0].drawing?.forEachIndexed { index, item ->
                                    item.drawingID = UUIDUtil.getUUID(item.fileName!!)
                                    var cacheFileParent = File(cacheRootDir + mCurDrawingsDir + index)
                                    cacheFileParent.mkdirs()
                                    var cacheFile = File(cacheFileParent, item.fileName)
                                    LogUtils.d("当前缓存地址: " + cacheFile.absolutePath)
                                    FileUtil.copyTo(File(item.localAbsPath), cacheFile)
                                    item.localAbsPath = cacheFile.absolutePath
                                }
                            }

                            LogUtils.d("查询楼： " + it)
                            var cc = v3BuildingModuleDbBean()
                            var mModuleUUID = UUIDUtil.getUUID(moduleName!!)
                            var mInspectors = it[0].inspectorName
                            var mLeader = it[0].leader
                            var aboveGroundNumber = it[0].aboveGroundNumber
                            var underGroundNumber = it[0].underGroundNumber

                            cc.id = -1
                            cc.uuid = mModuleUUID
                            cc.projectUUID = projectUUID
                            cc.projectId = projectId
                            cc.buildingUUID = buildingUUID
                            cc.buildingId = buildingId
                            cc.remoteId = remoteId
                            cc.moduleName = moduleName
                            cc.drawings = if(isCopyFloorDrawing(moduleName)) ArrayList() else it[0].drawing
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
                                .subscribeOn(Schedulers.computation())
                                .observeOn(Schedulers.computation())
                                .subscribe({
                                    LogUtils.d("本地model创建成功 " + it+" ; "+moduleName)
                                    dispose()
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
                                            underGroundNumber,
                                            cacheRootDir,
                                            mCurDrawingsDir
                                        )
                                    }
                                },{
                                    LogUtils.d("本地模块创建失败: "+it)
                                })

                        }
                    }

                },50)

               /* moduleNameList.forEach { moduleName->

                }*/

            },{
                LogUtils.d("本地模块创建失败: "+it)
            })




     /*   mDb.getLocalBuildingOnce(buildingId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {

                var  cacheRootDir = FileUtil.getDrawingCacheRootDir(mView!!.getContext()!!)
                var  mCurDrawingsDir =
                    "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + projectName + "/" + buildingName + "/" + moduleName + "/"

                it[0].drawing?.forEachIndexed { index, item ->
                    item.drawingID = UUIDUtil.getUUID(item.fileName!!)
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
                LogUtils.d("本地model创建成功 " + it+" ; "+moduleName)
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
                        underGroundNumber,
                    )
                }
            }, {
                LogUtils.d("本地楼层model创建失败 : " + it)
            })*/
    }

    fun isCopyFloorDrawing(moduleName: String?): Boolean {
        if (moduleName == "建筑结构复核" ||
            moduleName == "构件检测"
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
        underGroundNumber: Int?,
        cacheRootDir:String,
        mCurDrawingsDir:String
    ) {
        LogUtils.d("copy 层关系图纸: ${moduleName} ${moduleId}")

        mDb.getLocalFloorsInTheBuilding(buildingId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .subscribe({
                var arrayListFloorBeanList = ArrayList(it.sortedBy { b -> b.id })
                var floorList = ArrayList(arrayListFloorBeanList.map { b ->
                    b.drawing?.forEachIndexed { index, item ->
                        var cacheFileParent =
                            File(cacheRootDir + mCurDrawingsDir + b.floorName + "/" + index)
                        cacheFileParent.mkdirs()
                        var cacheFile = File(cacheFileParent, item.fileName)
                        LogUtils.d("当前模块楼层缓存地址: " + cacheFile.absolutePath)
                        FileUtil.copyTo(File(item.localAbsPath), cacheFile)
                        item.localAbsPath = cacheFile.absolutePath
                    }

                    b.drawing?.forEach {
                        it.drawingID = UUIDUtil.getUUID(it.fileName!!)
                    }

                    v3ModuleFloorDbBean(
                        id = -1,
                        projectId = projectId,
                        bldId = buildingId,
                        moduleId = moduleId,
                        floorId = UUIDUtil.getUUID(b.floorName!!),
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

                var index = 0

                Observable.fromIterable(floorList)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.computation())
                    .flatMap {
                        mDb.updatev3ModuleFloor(it)
                    }.observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if(index == floorList.size){
                            dispose()
                        }
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