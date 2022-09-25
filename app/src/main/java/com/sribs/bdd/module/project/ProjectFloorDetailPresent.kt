package com.sribs.bdd.module.project

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libnet.http.HttpManager
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.bean.BuildingFloorItem
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.db.v3.project.v3BuildingModuleDbBean
import com.sribs.common.bean.v3.v3ModuleFloorDbBean

import com.sribs.common.module.BasePresenter
import com.sribs.common.net.HttpApi
import com.sribs.common.server.IDatabaseService
import com.sribs.common.utils.TimeUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class ProjectFloorDetailPresent : BasePresenter(), IProjectContrast.IProjectFloorDetailPresent {

    private var mView: IProjectContrast.IProjectFloorDetailView? = null

    var list = arrayListOf<BuildingFloorItem>()

    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB)
            .navigation() as IDatabaseService
    }

/*    override fun getRemoteModule(mLocalProjectId: Long, mBuildingId: Long) {

        LogUtils.d("请求网络楼模块下数据")
        addDisposable(
            HttpManager.instance.getHttpService<HttpApi>()
                .getV3BuildingModuleList(mBuildingId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    checkResult(it)


                }, {
                    mView?.onMsg(checkError(it))
                })
        )


    }*/

    override fun getLocalModule(mLocalProjectId: Long, mBuildingId: Long) {

        mDb.getv3BuildingModule(mLocalProjectId, mBuildingId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe() {
                list.clear()
                for (i in 0 until it.size) {
                    var moduleId = it.get(i).id
                    var buildingId = it.get(i).buildingId
                    var moduleName = it.get(i).moduleName
                    var updateTime = it.get(i).updateTime
                    list.add(BuildingFloorItem(moduleId, buildingId, moduleName, updateTime))

                    if (i == it.size - 1) {
                        mView?.handlItemList(list)
                    }
                }
            }

    }

    fun deleteModule(
        projectId: Long,
        buildingId: Long,
        moduleId: Long,
        onResult: () -> Unit
    ) {
        LogUtils.d("llf无网络 删除本地模块")

        var b = v3BuildingModuleDbBean()

        b.id = moduleId

        addDisposable(mDb.deletev3BuildingModule(v3BuildingModuleDbBean(id = moduleId))
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                mDb.deletev3ModuleFloor(projectId, buildingId, moduleId)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //   cb(it)
                onResult()
                LogUtils.d("llf删除数据成功")
            }, {
                //  cb(false)
                it.printStackTrace()
            })
        )

    }

    fun createOrSaveModule(
        projectId: Long,
        buildingId: Long,
        remoteId: String?,
        moduleName: String?,
        onResult: (Long) -> Unit
    ) {
        LogUtils.d("llf无网络 单本地创建")
        /**
         * 查询copy楼栋下的图纸
         */

            mDb.getLocalBuildingOnce(buildingId).toObservable()
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .flatMap {
                    var cc = v3BuildingModuleDbBean()
                    cc.projectId = projectId
                    cc.buildingId = buildingId
                    cc.remoteId = remoteId
                    cc.moduleName = moduleName
                    cc.drawings = it[0].drawing
                    cc.inspectors = it[0].inspectorName
                    cc.leaderId = ""
                    cc.leaderName = it[0].leader
                    cc.updateTime = TimeUtil.YMD_HMS.format(Date())
                    cc.aboveGroundNumber = it[0].aboveGroundNumber
                    cc.underGroundNumber = it[0].underGroundNumber

                    LogUtils.d("查询到楼栋下面的数据包装进 model表 : " + cc)

                    mDb.updatev3BuildingModule(cc)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    LogUtils.d("本地楼层model创建成功 "+it)
                    onResult(it)
                    createModuleFloor(it, projectId, buildingId, remoteId, moduleName)
                },{
                    LogUtils.d("本地楼层model创建失败 : " + it)
                })
    }

    fun createModuleFloor(
        moduleId:Long,
        projectId: Long,
        buildingId: Long,
        remoteId: String?,
        moduleName: String?,
    ) {

        mDb.getLocalFloorsInTheBuilding(buildingId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .subscribe({
                var floorList = ArrayList(it.map { b->v3ModuleFloorDbBean(
                    id = -1,
                    projectId = projectId,
                    bldId = buildingId,
                    moduleId = moduleId,
                    floorId = b.floorId,
                    floorName =  b.floorName,
                    drawingsList = b.drawing,
                    remoteId = remoteId,
                    aboveNumber = b.aboveGroundNumber,
                    afterNumber = b.underGroundNumber,
                    version = 1,
                    status = 0,
                    createTime = TimeUtil.YMD_HMS.format(Date()),
                    updateTime = TimeUtil.YMD_HMS.format(Date())
                )})
                LogUtils.d("查询包装 model楼层表 : " + floorList)
                addDisposable(Observable.fromIterable(floorList)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.computation())
                    .flatMap {
                        mDb.updatev3ModuleFloor(it)
                    }.observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        LogUtils.d("存储到model 楼层表 : " + it)
                    },{

                    }))
            },{
                LogUtils.d("存储到model 楼层表失败 : " + it)
            })
    }

    fun saveModuleFloor(
        floorList:ArrayList<v3ModuleFloorDbBean>,
        onResult: (Long) -> Unit
    ){
        addDisposable(Observable.fromIterable(floorList)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                mDb.updatev3ModuleFloor(it)
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            },{

            }))

    }

    override fun bindView(v: IBaseView) {
    mView = v as IProjectContrast.IProjectFloorDetailView
}

override fun unbindView() {
    mView = null
}
}