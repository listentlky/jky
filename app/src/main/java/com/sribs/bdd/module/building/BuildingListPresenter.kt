package com.sribs.bdd.module.building

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.rxbus.RxBus
import com.cbj.sdk.libnet.http.HttpManager
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.google.gson.Gson
import com.sribs.bdd.R
import com.sribs.bdd.action.Dict
import com.sribs.bdd.bean.BuildingMainBean
import com.sribs.bdd.bean.BuildingModule
import com.sribs.bdd.bean.FloorSortBean
import com.sribs.bdd.utils.ModuleHelper
import com.sribs.bdd.v3.event.RefreshProjectListEvent
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
                        status = mStateArr[b.status ?: 0],
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
        LogUtils.d("请求云端楼数据项目ID: ${projectRemoteId}")
        addDisposable(HttpManager.instance.getHttpService<HttpApi>()
            .getV3BuildingList(projectRemoteId, version)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LogUtils.d("请求云端楼数据: " + it.toString())
                checkResult(it)
                var buildingMainBean = ArrayList<BuildingMainBean>()

                if (it.data == null) {
                    LogUtils.d("云端楼数据为空 直接返回")
                    mView?.onAllBuilding(ArrayList(localList.sortedByDescending { b -> b.updateTime }))
                    return@subscribe
                }

                /*   it.data!!.records.forEach { remoteBean->
                       var i = localList.indexOfFirst { localBean->
                           !localBean.remoteId.isNullOrEmpty() && localBean.remoteId == remoteBean.projectId
                       }

                       if (i>=0) {
                           var localBean = localList[i]
                           //判断时间
                           if (TimeUtil.isBefore(localBean.updateTime, remoteBean.updateTime)) {
                               localList[i].hasNewer = true
                           }
                           localBean.bldUUID = remoteBean.projectId
                           localBean.remoteData = remoteBean
                       }
                   }*/

                /*  it.data!!.records.forEach {  remoteBean->
                      var i = localList.indexOfFirst { localBean->
                          localBean.bldUUID == remoteBean.projectId
                      }
                      if (i>=0) {
                          var localBean = localList[i]
                          //判断时间
                          if (TimeUtil.isBefore(localBean.updateTime , remoteBean.updateTime)) {
                              localList[i].hasNewer = true
                          }
                          localBean.remoteData = remoteBean
                          localBean.remoteId = remoteBean.projectId
                      }
                  }*/


                /*        // 本地中没有
                        var onlyRemoteList =  it.data!!.filter { remoteBean->
                            localList.find { localBean->
                                (!localBean.remoteId.isNullOrEmpty() && localBean.remoteId == remoteBean.projectId) ||
                                        (localBean.bldName == remoteBean.projectName && localBean.remoteId.isNullOrEmpty())
                            }==null
                        }?.map { b -> BuildingMainBean(
                            localId = -1,
                            localUUID = b.projectId,
                            remoteId= b.projectId,
                            updateTimeYMD = TimeUtil.time2YMD(b.updateTime),
                            status = mStateArr[2],
                            address = b.projectName,
                            leader = b.leaderName?:"",
                            inspector = b.inspectors?.joinToString(separator = "、")?:""
                        ).also { _b->
                            _b.updateTime = b.updateTime
                            _b.createTime = b.createTime
                            _b.name = b.projectName
                            _b.remoteData = b
                        } }
                        buildingMainBean.addAll(localList)*/
                //    buildingMainBean.addAll(onlyRemoteList)
                mView?.onAllBuilding(ArrayList(buildingMainBean.sortedByDescending { b -> b.updateTime }))

            }, {
                LogUtils.d("${it.message}")
                mView?.onMsg(checkError(it))
                mView?.onAllBuilding(ArrayList(localList.sortedByDescending { b -> b.updateTime }))
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
                        b.inspectors?:"",
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
                buildingModuleList.addAll(list)
                mDb.getModuleFloorByBuilding(bean.bldId)
            }
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


                LogUtils.d("获取项目下所有楼栋层: " + buildingFloorList)

                LogUtils.d("获取项目下所有模块: " + buildingModuleList)

                LogUtils.d("获取项目下所有模块层: " + buildingModuleFloorList)

                LogUtils.d("获取项目下需要上传的图纸: " + drawingList)


                var parts = ArrayList<MultipartBody.Part>()

                drawingList.forEach { path ->
                    var fileBody = RequestBody.create(MediaType.parse("image/*"), File(path))
                    var filePart = MultipartBody.Part.createFormData("files", path, fileBody)
                    parts.add(filePart)
                }

                buildingModuleList?.forEach { buildingModule ->

                    if (buildingModule.moduleName.equals("构建检测")) {
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
            }))

    }

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
                                if (it.remoteId.isNullOrEmpty()) it.moduleUUID!! else it.remoteId!!,
                                it.moduleName ?: "",
                                it.version ?: System.currentTimeMillis()
                            )
                        )
                    }

                    V3UploadDrawingReq.add(
                        V3UploadDrawingReq(
                            if (it.buildingRemoteId.isNullOrEmpty()) it.buildingUUID!! else it.buildingRemoteId!!,
                            "drawing:" + resId?.get(0)?.resId,
                            b.fileName!!,
                            b.fileType!!,
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
                                "drawing:" + resId?.get(0)?.resId,
                                bbb.fileName!!,
                                bbb.fileType!!,
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
                    it.version!!,
                    System.currentTimeMillis()
                )
            )
        }

        var V3UploadDrawingReq = ArrayList<V3UploadDrawingReq>()

        bean.drawingList?.forEachIndexed { index, drawingV3Bean ->
            var resId = res?.filter {
                it.fileName.equals(drawingV3Bean.localAbsPath)
            }

            V3UploadDrawingReq.add(
                V3UploadDrawingReq(
                    if (bean.remoteId.isNullOrEmpty()) bean.bldUUID!! else bean.remoteId!!,
                    "drawing:" + resId?.get(0)?.resId,
                    drawingV3Bean.fileName!!,
                    drawingV3Bean.fileType!!,
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
            bean.bldId == cc.bldId
        }

        currentBuildingFloorList.forEachIndexed { index, floorBean ->

            floorBean.drawing?.forEach { dd ->
                var resId = res?.filter { ff ->
                    ff.fileName.equals(dd.localAbsPath)
                }

                V3UploadDrawingReq.add(
                    V3UploadDrawingReq(
                        if (bean.remoteId.isNullOrEmpty()) bean.bldUUID!! else bean.remoteId!!,
                        "drawing:" + resId?.get(0)?.resId,
                        dd.fileName!!,
                        dd.fileType!!,
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
                mDb.updateBuilding(bean.bldId,0,1)
            }
            .flatMap {
                LogUtils.d("更新楼下模块 ischanged")
                mDb.updateBuildingModuleByBuildingId(bean.bldId,0,1)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                dispose()
                LogUtils.d("楼上传成功")
                mView?.onMsg("楼上传成功")
             //   RxBus.getDefault().post(RefreshProjectListEvent(true))
                cb(true)
            }, {
                LogUtils.d("楼上传失败"+ checkError(it))
                mView?.onMsg("楼上传失败: " + checkError(it))
                dispose()
                cb(false)
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

    /**
     * 获取楼版本列表
     */
    fun getV3BuildingVersionHistory(
        projectRemoteId: String,
        buildingRemoteId: String,
        cb: (ArrayList<V3VersionBean>?) -> Unit
    ) {
        addDisposable(HttpManager.instance.getHttpService<HttpApi>()
            .getV3BuildingVersionList(V3VersionReq(projectRemoteId, buildingRemoteId))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                checkResult(it)
                LogUtils.d("查询楼版本列表：" + it)
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
        projectName:String,
        remoteBuildingId: String,
        version: String,
        cb: (Boolean,String) -> Unit
    ) {

        var list = ArrayList<V3DownloadReq>()
        list.add(V3DownloadReq(remoteBuildingId, version))

        var cacheRootDir: String = FileUtil.getDrawingCacheRootDir(mView!!.getContext()!!)

        var uploadPDFSuccess = true

        var downloadMsg = "下载成功"

        var needDownloadDrawingList = ArrayList<V3UploadDrawingRes>()

        addDisposable(HttpManager.instance.getHttpService<HttpApi>()
            .downloadV3BuildingVersionList(V3VersionDownloadReq(list))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .subscribe({

                checkResult(it)
                var a = it.data?.get(0)

                LogUtils.d("下载的楼版本数据: " + a)

                var mCurDrawingsDir =
                    "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + projectName + "/" + a?.buildingNo + "/"
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
                }

                /**
                 * 过滤出最新模块记录
                 */
                var iterator = a?.modules?.iterator()
                var filterModuleList = ArrayList<V3UploadModuleReq>()
                var ModuleList = a?.modules

                while (iterator?.hasNext()!!){
                    var next = iterator?.next()
                    var isSmallTime = false
                    ModuleList?.forEach { filter->
                        if(filter.moduleName == next.moduleName
                            && next.createTime< filter.createTime){
                            isSmallTime = true
                        }
                    }
                    if(!isSmallTime){
                        filterModuleList.add(next)
                    }
                }

                a?.modules?.clear()

                a?.modules?.addAll(filterModuleList)


                a?.modules?.forEach { mm->

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

                        if (mm.equals("构建测量")) {
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

                }

                LogUtils.d("需要下载的图纸个数: " + needDownloadDrawingList.size)
                var uploadDrawingIndex = 0

                needDownloadDrawingList?.forEach { res ->
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
                                    buildingUpdateV3Version(a, projectName,beanMain,cb)
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
                                    buildingUpdateV3Version(a,projectName, beanMain,cb)
                                } else {
                                    cb(false,downloadMsg)
                                    LogUtils.d("图纸下载失败" + downloadMsg)
                                }
                            }
                        })
                }

            },{
                cb(false, checkError(it))
            })
        )

    }

    /**
     * 更新本地版本数据
     */
    private fun buildingUpdateV3Version(
        a: V3UploadBuildingReq?,
        projectName:String,
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

                a?.drawings?.forEach {dd->

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
                        -1,
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
                                -1,
                                ff.floorNo,
                                ff.direction,
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
                                    if(a.modules.size<=0){
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
                                dd.damageMixes?.forEach {damage->
                                    var damageV3Bean =
                                        Gson().fromJson(damage.desc, DamageV3Bean::class.java)
                                    if(mm.moduleName == "构建检测"){
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
                                mm.superiorVersion,
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
                                    if(moduleFloorSortBean.size<=0){
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
                                                floorId = -1,
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
                                    LogUtils.d("部分模块创建失败: " + it)
                                    dispose()
                                })
                        }
                        dispose()
                    },{
                        LogUtils.d("楼创建失败: " + it)
                        dispose()
                    })
                dispose()
            },{
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

        if (!beanMain.remoteId.isNullOrEmpty()) {
            LogUtils.d("删除网络数据")
            addDisposable(
                HttpManager.instance.getHttpService<HttpApi>()
                    .deleteBuilding(
                        V3VersionDeleteReq(
                            beanMain.remoteId!!,
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