package com.sribs.bdd.module.building

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libnet.http.HttpManager
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.google.gson.Gson
import com.sribs.bdd.R
import com.sribs.bdd.action.Dict
import com.sribs.bdd.bean.BuildingMainBean
import com.sribs.bdd.bean.BuildingModule
import com.sribs.bdd.bean.FloorSortBean
import com.sribs.bdd.utils.ModuleHelper
import com.sribs.bdd.utils.UUIDUtil
import com.sribs.bdd.v3.bean.UpdateModuleVersionBean
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.V3VersionBean
import com.sribs.common.bean.db.BuildingBean
import com.sribs.common.bean.db.DamageV3Bean
import com.sribs.common.bean.db.DrawingV3Bean
import com.sribs.common.bean.db.FloorBean
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

/**
 * create time: 2022/9/19
 * author: bruce
 * description:
 */
class BuildingListPresenter : BasePresenter(), IBuildingContrast.IBuildingListPresent {

    private var mView: IBuildingContrast.IBuildingListView? = null

    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB)
            .navigation() as IDatabaseService
    }

    private val mStateArr by lazy {
        mView?.getContext()?.resources?.getStringArray(R.array.main_project_status) ?: emptyArray()
    }

    /**
     * 获取本地楼
     */
    override fun getAllBuilding(localProject: Long, projectUUID: String) {
        mDb.getBuildingByProjectId(localProject)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                var list = ArrayList(it.map { b ->
                    com.sribs.bdd.bean.BuildingMainBean(
                        projectRemoteId = b.projectRemoteId,
                        projectUUID = b.projectUUID,
                        projectId = localProject,
                        bldUUID = b.UUID,
                        bldId = b.id ?: -1,
                        bldName = b.bldName ?: "",
                        bldType = b.bldType ?: "",
                        leader = b.leader ?: "",
                        inspectorName = b.inspectorName ?: "",
                        remoteId = b.remoteId,
                        createTime = b.createTime ?: "",
                        updateTime = b.updateTime ?: "",
                        superiorVersion = b.superiorVersion,
                        parentVersion = b.parentVersion,
                        version = b.version,
                        status = b.status!!,
                        aboveGroundNumber = b.aboveGroundNumber ?: 0,
                        underGroundNumber = b.underGroundNumber ?: 0,
                        drawingList = b.drawing,
                        isChanged = b.isChanged
                    )
                })
                LogUtils.d("获取本地数据库楼表: " + list.toString())
                mView!!.onAllBuilding(ArrayList(list.sortedByDescending { b -> b.createTime }))

                /* if(!Config.isNetAvailable){
                     LogUtils.d("无网络 直接展示本地数据: ")

                 }else{
                     LogUtils.d("有网络 获取云端楼: ")
                     getBuildingRemote(projectUUID,list)
                 }*/
            }
    }

    /**
     * 获取云端楼
     */
    fun getBuildingRemote(
        projectRemoteId: String,
        version: Long,
        localList: ArrayList<BuildingMainBean>
    ) {
        LogUtils.d("getBuildingRemote: " + localList)

        LogUtils.d("请求云端楼数据项目ID: ${projectRemoteId} ${version}")
        addDisposable(
            HttpManager.instance.getHttpService<HttpApi>()
                .getV3BuildingList(projectRemoteId, version)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    LogUtils.d("请求云端楼数据: " + it.toString())
                    checkResult(it)

                    if (it.data == null || it.data?.size!! <= 0) {
                        mView?.onMsg("云端未查到数据")
                        return@subscribe
                    }

                    var buildingMainBeanList = ArrayList<BuildingMainBean>()

                    // 本地中没有
                    var onlyRemoteList = it.data!!.filter { remoteBean ->
                        localList.find { localBean ->
                            localBean.bldName == remoteBean.buildingNo
                            /*(!localBean.remoteId.isNullOrEmpty() && localBean.remoteId == remoteBean.buildingId)
                                    || (localBean.remoteId.isNullOrEmpty() && localBean.bldUUID == remoteBean.buildingId)*/
                        } == null
                    }?.map { b ->
                        BuildingMainBean(
                            projectRemoteId = b.projectId,
                            projectUUID = b.projectId,
                            projectId = -1,
                            bldUUID = b.buildingId,
                            bldId = -1,
                            bldName = b.buildingNo,
                            bldType = b.buildingType,
                            leader = b.leaderName ?: "",
                            inspectorName = b.inspectors?.joinToString(separator = "、") ?: "",
                            remoteId = b.buildingId,
                            createTime = TimeUtil.stampToDate("" + b.createTime),
                            updateTime = TimeUtil.stampToDate("" + b.createTime),
                            superiorVersion = version,
                            parentVersion = b.parentVersion,
                            version = b.version,
                            status = 2,
                            aboveGroundNumber = b.aboveGroundNumber ?: 0,
                            underGroundNumber = b.underGroundNumber ?: 0,
                            drawingList = ArrayList(),
                            isChanged = 0
                        )
                    }

                    /**
                     * 过滤出最新模块记录
                     */
                    var iterator = onlyRemoteList.iterator()
                    var filterBuildingList = ArrayList<BuildingMainBean>()
                    var buildingList = onlyRemoteList

                    while (iterator.hasNext()) {
                        var next = iterator.next()
                        var isSmallTime = false
                        buildingList.forEach { filter ->
                            if (filter.bldName == next.bldName
                                && TimeUtil.dateToStamp(next.createTime!!) < TimeUtil.dateToStamp(
                                    filter.createTime!!
                                )
                            ) {
                                isSmallTime = true
                            }
                        }
                        if (!isSmallTime) {
                            filterBuildingList.add(next)
                        }
                    }

                    buildingMainBeanList.addAll(filterBuildingList)
                    mView?.onAllRemoteBuilding(ArrayList(buildingMainBeanList.sortedByDescending { b -> b.createTime }))
                    mView?.onMsg("更新成功")
                }, {
                    LogUtils.d("${it.message}")
                    mView?.onMsg(checkError(it))
                })
        )
    }

    /**
     * 上传楼配置
     */
    fun uploadBuilding(bean: BuildingMainBean, cb: (Boolean) -> Unit) {

        LogUtils.d("上传楼配置： " + bean)

        var buildingFloorList = ArrayList<FloorBean>()

        var buildingModuleList = ArrayList<BuildingModule>()

        var buildingModuleFloorList = ArrayList<v3ModuleFloorDbBean>()

        var drawingList = ArrayList<String>()

        bean.drawingList?.forEach {
            drawingList.add(it.localAbsPath!!)
        }

        addDisposable(mDb.getFloorByBuildingId(bean.bldId)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                it.forEach {
                    it.drawing?.forEach {
                        drawingList.add(it.localAbsPath!!)
                    }
                }
                buildingFloorList.addAll(it)
                mDb.getv3BuildingModuleByBuildingId(bean.bldId)
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
                mDb.getModuleFloorByBuilding(bean.bldId)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .subscribe({
                LogUtils.d("查询楼下模块数据: " + it)
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
                        floorIndex = b.floorIndex,
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


                LogUtils.d("获取项目下所有楼栋层: " + buildingFloorList)

                LogUtils.d("获取项目下所有模块: " + buildingModuleList)

                LogUtils.d("获取项目下所有模块层: " + Gson().toJson(buildingModuleFloorList))

                LogUtils.d("获取项目下需要上传的图纸: " + Gson().toJson(drawingList))

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
                    } else if (buildingModule.moduleName == "倾斜测量") {
                        buildingModule.drawings?.forEach { drawing ->
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
                    } else if (buildingModule.moduleName == "非居民类检测") {
                        buildingModule.drawings?.forEach { drawing ->
                            drawing.damage?.forEach { damage ->
                                if(mCurrentDamageType.contains(damage.type)){
                                    if (damage.noResDamagePicList?.size!! > 0) {
                                        drawingList.add(damage.noResDamagePicList!!.get(1))
                                    }
                                    if (damage.noResCrackPointPicList?.size!! > 0) {
                                        drawingList.add(damage.noResCrackPointPicList!!.get(1))
                                    }
                                }
                            }
                        }
                    }
                }

                var parts = ArrayList<MultipartBody.Part>()

                LogUtils.d("添加模块下图纸后: " + drawingList.size + " ; " + drawingList)

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
                            makeUploadBuilding(
                                buildingFloorList, buildingModuleList,
                                buildingModuleFloorList, res.data, bean, cb
                            )
                        }, {
                            LogUtils.d("文件上传失败: ${it}")
                            cb(false)
                        })
                } else {
                    makeUploadBuilding(
                        buildingFloorList, buildingModuleList,
                        buildingModuleFloorList, ArrayList(), bean, cb
                    )
                }
            }, {
                cb(false)
            })
        )

    }

    var mCurrentDamageType =
        Arrays.asList("裂缝", "渗漏", "变形", "高坠", "其他")

    private fun makeUploadBuilding(
        buildingFloorList: java.util.ArrayList<FloorBean>,
        buildingModuleList: java.util.ArrayList<BuildingModule>,
        buildingModuleFloorList: java.util.ArrayList<v3ModuleFloorDbBean>,
        res: List<V3UploadDrawingRes>?,
        bean: BuildingMainBean,
        cb: (Boolean) -> Unit
    ) {

        LogUtils.d("makeUploadProject")
        var inspectorList: List<String> =
            if (bean.inspectorName.contains("、")) bean.inspectorName.split("、") else Arrays.asList(
                bean.inspectorName
            )

        var V3UploadBuildingModuleList = ArrayList<String>()

        var V3UploadModuleReq = ArrayList<V3UploadModuleReq>()

        var UpdateModuleVersionBeanList = ArrayList<UpdateModuleVersionBean>()//模块版本

        buildingModuleList.forEach {
            var V3UploadDrawingReq = ArrayList<V3UploadDrawingReq>()

            // 模块无层概念下图纸损伤数据
            if (!isCopyFloorDrawing(it.moduleName)) {
                V3UploadBuildingModuleList.add(if (it.remoteId.isNullOrEmpty()) it.moduleUUID!! else it.remoteId!!)
                it.drawings?.forEachIndexed { index, b ->

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

                        if(mCurrentDamageType.contains(d.type)){

                            if (d.noResDamagePicList?.size!! > 0) {
                                var resId1 = res?.filter {
                                    it.fileName.equals(d.noResDamagePicList?.get(1))
                                }
                                d.noResDamagePicList?.add(resId1?.get(0)?.resId ?: "")
                            }
                            if (d.noResCrackPointPicList?.size!! > 0) {
                                var resId1 = res?.filter {
                                    it.fileName.equals(d.noResCrackPointPicList?.get(1))
                                }
                                d.noResCrackPointPicList?.add(resId1?.get(0)?.resId ?: "")
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
                                if (it.remoteId.isNullOrEmpty()) it.moduleUUID!! else it.remoteId!!,
                                it.moduleName ?: "",
                                it.version ?: System.currentTimeMillis()
                            )
                        )
                    }

                    V3UploadDrawingReq.add(
                        V3UploadDrawingReq(
                            if (it.buildingRemoteId.isNullOrEmpty()) it.buildingUUID!! else it.buildingRemoteId!!,
                            drawingID,
                            b.fileName!!,
                            b.drawingType!!,
                            b.fileType!!,
                            "",
                            "",
                            0,
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
            }

            if(isCopyAllDrawing(it.moduleName) || isCopyFloorDrawing(it.moduleName)){ //层概念下的图纸

                var currentModuleFloorList = buildingModuleFloorList.filter { bb ->
                    it.moduleid == bb.moduleId
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

                            if(mCurrentDamageType.contains(ddd.type)){ //非居民类损伤

                                if (ddd.noResDamagePicList?.size!! > 0) {
                                    var resId5 = res?.filter {
                                        it.fileName.equals(ddd.noResDamagePicList?.get(1))
                                    }
                                    ddd.noResDamagePicList?.add(resId5?.get(0)?.resId ?: "")
                                }

                                if (ddd.noResCrackPointPicList?.size!! > 0) {
                                    var resId6 = res?.filter {
                                        it.fileName.equals(ddd.noResCrackPointPicList?.get(1))
                                    }
                                    ddd.noResCrackPointPicList?.add(resId6?.get(0)?.resId ?: "")
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
                                    if (it.remoteId.isNullOrEmpty()) it.moduleUUID!! else it.remoteId!!,
                                    it.moduleName ?: "",
                                    it.version ?: System.currentTimeMillis()
                                )
                            )
                        }

                        V3UploadDrawingReq.add(
                            V3UploadDrawingReq(
                                if (it.buildingRemoteId.isNullOrEmpty()) it.buildingUUID!! else it.buildingRemoteId!!,
                                drawingID,
                                bbb.fileName!!,
                                bbb.drawingType!!,
                                bbb.fileType!!,
                                cc.floorId!!,
                                cc.floorName ?: "",
                                cc.floorIndex,
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
            UpdateModuleVersionBeanList.add(UpdateModuleVersionBean(it.moduleid!!, version))

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

        var V3UploadDrawingReq = ArrayList<V3UploadDrawingReq>()

        bean.drawingList?.forEachIndexed { index, drawingV3Bean ->
            var resId = res?.filter {
                it.fileName.equals(drawingV3Bean.localAbsPath)
            }

            var drawingID = UUIDUtil.getUUID(drawingV3Bean.fileName!!)

            V3UploadDrawingReq.add(
                V3UploadDrawingReq(
                    if (bean.remoteId.isNullOrEmpty()) bean.bldUUID!! else bean.remoteId!!,
                    drawingID,
                    drawingV3Bean.fileName!!,
                    drawingV3Bean.drawingType!!,
                    drawingV3Bean.fileType!!,
                    "",
                    "",
                    0,
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
            bean.bldId == cc.bldId
        }

        currentBuildingFloorList.forEachIndexed { index, floorBean ->

            floorBean.drawing?.forEach { dd ->
                var resId = res?.filter { ff ->
                    ff.fileName.equals(dd.localAbsPath)
                }

                var drawingID = UUIDUtil.getUUID(dd.fileName!!)

                V3UploadDrawingReq.add(
                    V3UploadDrawingReq(
                        if (bean.remoteId.isNullOrEmpty()) bean.bldUUID!! else bean.remoteId!!,
                        drawingID,
                        dd.fileName!!,
                        dd.drawingType!!,
                        dd.fileType!!,
                        floorBean.floorId!!,
                        floorBean.floorName ?: "",
                        floorBean.floorIndex?:0,
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


        var V3UploadBuildingReq = V3UploadBuildingReq(
            if (bean.remoteId.isNullOrEmpty()) bean.bldUUID!! else bean.remoteId!!,
            bean.bldName!!,
            bean.bldType!!,
            inspectorList,
            bean.isChanged == 1,
            Dict.getLeaderId(bean.leader) ?: "",
            bean.leader,
            V3UploadBuildingModuleList,
            V3UploadModuleReq,
            V3UploadDrawingReq,
            bean.aboveGroundNumber ?: 0,
            bean.underGroundNumber ?: 0,
            bean.parentVersion ?: 0,
            if (bean.projectRemoteId.isNullOrEmpty()) bean.projectUUID!! else bean.projectRemoteId!!,
            bean.superiorVersion ?: 0,
            bean.version ?: System.currentTimeMillis(),
            TimeUtil.dateToStamp(bean.createTime ?: "" + System.currentTimeMillis())
        )

        LogUtils.d("生成楼上传数据: " + Gson().toJson(V3UploadBuildingReq))

        HttpManager.instance.getHttpService<HttpApi>()
            .saveV3Building(V3UploadBuildingReq)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                LogUtils.d("更新楼 ischanged")
                mDb.updateBuilding(bean.bldId, 0, 1)
            }
            .flatMap {
                LogUtils.d("更新楼下模块 ischanged")
                mDb.updateBuildingModuleByBuildingId(bean.bldId, 0, 1)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                dispose()
                var updateModuleVersionCount = 0
                if (UpdateModuleVersionBeanList.size > 0) {

                    UpdateModuleVersionBeanList.forEach {
                        mDb.updateBuildingModuleVersion(it.moduleId, it.version)
                            .subscribeOn(Schedulers.computation())
                            .observeOn(Schedulers.computation())
                            .subscribe({
                                LogUtils.d("更新模块版本成功 " + it)
                                updateModuleVersionCount++
                                if (updateModuleVersionCount == UpdateModuleVersionBeanList.size) {
                                    LogUtils.d("楼上传成功")
                                    mView?.onMsg("楼上传成功")
                                    //   RxBus.getDefault().post(RefreshProjectListEvent(true))
                                    cb(true)
                                }
                            }, {
                            })
                    }
                } else {
                    LogUtils.d("楼上传成功")
                    mView?.onMsg("楼上传成功")
                    //   RxBus.getDefault().post(RefreshProjectListEvent(true))
                    cb(true)
                }
            }, {
                LogUtils.d("楼上传失败" + checkError(it))
                mView?.onMsg("楼上传失败: " + checkError(it))
                dispose()
                cb(false)
            })

    }

    fun isCopyAllDrawing(moduleName: String?): Boolean {
        if (moduleName == "非居民类检测"
        ) {
            return true
        }
        return false
    }

    fun isCopyFloorDrawing(moduleName: String?): Boolean {
        if (moduleName == "建筑结构复核" ||
            moduleName == "构件检测"
        ) {
            return true
        }
        return false
    }

    /**
     * 获取楼版本列表
     */
    fun getV3BuildingVersionHistory(
        projectRemoteId: String,
        buildingRemoteId: String,
        cb: (ArrayList<V3VersionBean>?) -> Unit
    ) {
        addDisposable(
            HttpManager.instance.getHttpService<HttpApi>()
                .getV3BuildingVersionList(V3VersionReq(projectRemoteId, buildingRemoteId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    checkResult(it)
                    LogUtils.d("查询楼版本列表：" + it)
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
                    LogUtils.d("查询楼版本列表失败：" + it)
                    mView?.onMsg(ERROR_HTTP)
                    cb(null)
                    it.printStackTrace()
                })
        )
    }

    /**
     * 下载指定版本楼
     */
    fun downloadBuildingConfig(
        beanMain: BuildingMainBean,
        projectName: String,
        remoteBuildingId: String,
        version: String,
        cb: (Boolean, String) -> Unit
    ) {

        var list = ArrayList<V3DownloadReq>()
        list.add(V3DownloadReq(remoteBuildingId, version))

        var cacheRootDir: String = FileUtil.getDrawingCacheRootDir(mView!!.getContext()!!)

        var uploadPDFSuccess = true

        var downloadMsg = "下载成功"

        var needDownloadDrawingList = ArrayList<V3UploadDrawingRes>()

        addDisposable(
            HttpManager.instance.getHttpService<HttpApi>()
                .downloadV3BuildingVersionList(V3VersionDownloadReq(list))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe({

                    checkResult(it)
                    var a = it.data?.get(0)

                    LogUtils.d("下载的楼版本数据: " + a)

                    var mCurDrawingsDir =
                        "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + projectName + "/" + a?.buildingNo + "/"
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
                    }

                    /**
                     * 过滤出最新模块记录
                     */
                    var iterator = a?.modules?.iterator()
                    var filterModuleList = ArrayList<V3UploadModuleReq>()
                    var ModuleList = a?.modules

                    while (iterator?.hasNext()!!) {
                        var next = iterator?.next()
                        var isSmallTime = false
                        ModuleList?.forEach { filter ->
                            if (filter.moduleName == next.moduleName
                                && next.createTime < filter.createTime
                            ) {
                                isSmallTime = true
                            }
                        }
                        if (!isSmallTime) {
                            filterModuleList.add(next)
                        }
                    }

                    a?.modules?.clear()

                    a?.modules?.addAll(filterModuleList)


                    a?.modules?.forEach { mm ->

                        mCurDrawingsDir =
                            "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + projectName + "/" + a?.buildingNo + "/" + mm.moduleName + "/"
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
                            } else if (mm.moduleName == "倾斜测量") {
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
                            }else if(mm.moduleName == "非居民类检测"){
                                dd.damageMixes?.forEach { damage ->
                                    var damageV3Bean =
                                        Gson().fromJson(damage.desc, DamageV3Bean::class.java)
                                    if(mCurrentDamageType.contains(damageV3Bean.type)){

                                        if (damageV3Bean.noResDamagePicList?.size!! > 0) {
                                            drawingLocalPath = File(
                                                cacheRootDir + mCurDrawingsDir + "/damage/" + damageV3Bean?.type,
                                                damageV3Bean?.noResDamagePicList?.get(0)
                                            ).absolutePath

                                            needDownloadDrawingList.add(
                                                V3UploadDrawingRes(
                                                    damageV3Bean?.noResDamagePicList?.get(2)!!,
                                                    drawingLocalPath
                                                )
                                            )
                                        }
                                        if (damageV3Bean.noResCrackPointPicList?.size!! > 0) {
                                            drawingLocalPath = File(
                                                cacheRootDir + mCurDrawingsDir + "/damage/" + damageV3Bean?.type,
                                                damageV3Bean?.noResCrackPointPicList?.get(0)
                                            ).absolutePath

                                            needDownloadDrawingList.add(
                                                V3UploadDrawingRes(
                                                    damageV3Bean?.noResCrackPointPicList?.get(2)!!,
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
                                        buildingUpdateV3Version(a, projectName, beanMain, cb)
                                    } else {
                                        cb(false, downloadMsg)
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
                                        buildingUpdateV3Version(a, projectName, beanMain, cb)
                                    } else {
                                        cb(false, downloadMsg)
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

    /**
     * 更新本地版本数据
     */
    private fun buildingUpdateV3Version(
        a: V3UploadBuildingReq?,
        projectName: String,
        beanMain: BuildingMainBean,
        cb: (Boolean, String) -> Unit
    ) {
        var cacheRootDir: String = FileUtil.getDrawingCacheRootDir(mView!!.getContext()!!)

        mDb.deleteFloorByBuildingId(beanMain.bldId)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                LogUtils.d("楼层删除成功")
                mDb.deleteBuildingModuleByBuildingId(beanMain.bldId)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                LogUtils.d("楼下模块删除成功")
                mDb.deleteModuleFloorByBuildingId(beanMain.bldId)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .subscribe({

                var updateBuildingDrawing = ArrayList<DrawingV3Bean>()

                var updateBuildingFloorDrawing =
                    HashMap<String, ArrayList<DrawingV3Bean>>()

                var floorSortBean = ArrayList<FloorSortBean>()

                var mCurDrawingsDir =
                    "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + projectName + "/" + a?.buildingNo + "/"

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
                    var DrawingV3Bean = DrawingV3Bean(
                        dd.drawingId,
                        dd.drawingName,
                        dd.fileType,
                        dd.drawingType,
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
                                dd.floorIndex,
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
                    beanMain.bldId,
                    a?.buildingId!!,
                    beanMain.projectRemoteId,
                    beanMain.projectRemoteId,
                    beanMain.projectId,
                    a.buildingNo,
                    "all",
                    TimeUtil.stampToDate("" + a.createTime),
                    TimeUtil.stampToDate("" + a.createTime),
                    "",
                    0,
                    a.leaderName,
                    a.inspectors?.joinToString(separator = "、") ?: "",
                    beanMain.superiorVersion,
                    a.parentVersion,
                    a.version,
                    a.buildingId,
                    4,
                    updateBuildingDrawing,
                    a.aboveGroundNumber,
                    a.underGroundNumber,
                    0
                )

                mDb.createLocalBuilding(buildingBean)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.computation())
                    .subscribe({ BUIDINGID ->
                        LogUtils.d("下载楼成功: " + BUIDINGID)
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
                                beanMain.projectId,
                                BUIDINGID,
                                -1,
                                ff.floorId,
                                ff.floorNo,
                                ff.direction,
                                ff.floorIndex,
                                TimeUtil.stampToDate("" + System.currentTimeMillis()),
                                TimeUtil.stampToDate("" + System.currentTimeMillis()),
                                "",
                                a.inspectors?.joinToString(
                                    separator = "、" ?: ""
                                ),
                                a.version,
                                a.projectId,
                                0,
                                drawingV3Bean,
                                a.aboveGroundNumber,
                                a.underGroundNumber,
                            )

                            LogUtils.d("创建楼层数据: " + appFloor)
                            mDb.createLocalFloor(appFloor)
                                .subscribeOn(Schedulers.computation())
                                .observeOn(Schedulers.computation())
                                .subscribe({
                                    if (a.modules.size <= 0) {
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
                        a?.modules?.forEach { mm ->

                            var updateModuleDrawing = ArrayList<DrawingV3Bean>()

                            var updateModuleFloorDrawing =
                                HashMap<String, ArrayList<DrawingV3Bean>>()

                            var moduleFloorSortBean = ArrayList<FloorSortBean>()

                            mCurDrawingsDir =
                                "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + projectName + "/" + a.buildingNo + "/" + mm.moduleName + "/"

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
                                dd.damageMixes?.forEach { damage ->
                                    var damageV3Bean =
                                        Gson().fromJson(damage.desc, DamageV3Bean::class.java)
                                    if (mm.moduleName == "构件检测") {
                                        when (damageV3Bean.type) {
                                            "梁" -> {
                                                if (damageV3Bean?.beamLeftRealPicList?.size!! > 1) {
                                                    damageV3Bean?.beamLeftRealPicList?.set(
                                                        1, File(
                                                            cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                            damageV3Bean?.beamLeftRealPicList?.get(0)
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
                                                            damageV3Bean?.beamRightRealPic?.get(0)
                                                        ).absolutePath
                                                    )
                                                }

                                                if (damageV3Bean?.beamRightDesignPic?.size!! > 1) {
                                                    damageV3Bean?.beamRightDesignPic?.set(
                                                        1, File(
                                                            cacheRootDir + mCurDrawingsDir + dd.floorNo + "/damage/" + damageV3Bean?.type,
                                                            damageV3Bean?.beamRightDesignPic?.get(0)
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
                                                            damageV3Bean?.columnRightRealPic?.get(0)
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
                                    } else if (mm.moduleName == "倾斜测量") {
                                        if (damageV3Bean?.scalePath?.size!! > 1) {
                                            damageV3Bean?.scalePath?.set(
                                                1, File(
                                                    cacheRootDir + mCurDrawingsDir + "/damage/" + damageV3Bean?.type,
                                                    damageV3Bean?.scalePath?.get(0)
                                                ).absolutePath
                                            )
                                        }
                                    } else if(mm.moduleName == "非居民类检测"){
                                        if (damageV3Bean?.noResDamagePicList?.size!! > 1) {
                                            damageV3Bean?.noResDamagePicList?.set(
                                                1, File(
                                                    cacheRootDir + mCurDrawingsDir + "/damage/" + damageV3Bean?.type,
                                                    damageV3Bean?.noResDamagePicList?.get(0)
                                                ).absolutePath
                                            )
                                        }
                                        if (damageV3Bean?.noResCrackPointPicList?.size!! > 1) {
                                            damageV3Bean?.noResCrackPointPicList?.set(
                                                1, File(
                                                    cacheRootDir + mCurDrawingsDir + "/damage/" + damageV3Bean?.type,
                                                    damageV3Bean?.noResCrackPointPicList?.get(0)
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
                                    dd.drawingType,
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
                                            dd.floorIndex,
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
                                a.buildingId,
                                a.buildingId,
                                BUIDINGID,
                                beanMain.projectRemoteId,
                                beanMain.projectId,
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
                                a?.version,
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
                                        cb(true, "下载成功")
                                    }
                                    /**
                                     * 创建模块楼层
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
                                                bldId = BUIDINGID,
                                                moduleId = MODULEID,
                                                floorId = ff.floorId,
                                                floorName = ff.floorNo,
                                                floorType = ff.direction,
                                                floorIndex = ff.floorIndex,
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
                                    LogUtils.d("部分模块创建失败: " + it)
                                    dispose()
                                })
                        }
                        dispose()
                    }, {
                        LogUtils.d("楼创建失败: " + it)
                        dispose()
                    })
                dispose()
            }, {
                dispose()
            })

    }

    /**
     * 删除楼
     */
    fun deleteBuilding(beanMain: BuildingMainBean) {
        //本地楼
        if (beanMain.bldId > 0) {
            var obList = ArrayList<Observable<Boolean>>()
            var bldId = beanMain.bldId
            obList.add(mDb.deleteBuilding(bldId))
            obList.add(mDb.deleteFloorByBuildingId(bldId))
            obList.add(mDb.deleteBuildingModuleByBuildingId(bldId))
            obList.add(mDb.deleteModuleFloorByBuildingId(bldId))

            var count = 0
            Observable.merge(obList)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    count++
                    LogUtils.d("删除楼 count=$count  $it")
                }, {
                    it.printStackTrace()
                })
        }

        if (beanMain.status != 0) {
            LogUtils.d("删除网络数据")
            addDisposable(
                HttpManager.instance.getHttpService<HttpApi>()
                    .deleteBuilding(
                        V3VersionDeleteReq(
                            beanMain.bldUUID,
                            beanMain.superiorVersion!!,
                            beanMain.version!!
                        )
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        checkResult(it)
                        LogUtils.d("删除远端楼成功：" + it.toString())
                        mView?.onMsg("删除楼成功")
                    }, {
                        mView?.onMsg("删除远端楼失败" + checkError(it))
                    })
            )
        }
    }

    override fun bindView(v: IBaseView) {
        mView = v as IBuildingContrast.IBuildingListView
    }

    override fun unbindView() {
        dispose()
        mView = null
    }
}