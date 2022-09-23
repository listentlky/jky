package com.sribs.bdd.module.project

import cc.shinichi.library.tool.ui.ToastUtil
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libnet.http.HttpManager
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.bean.BuildingFloorItem
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.db.v3.project.v3BuildingModuleDbBean

import com.sribs.common.module.BasePresenter
import com.sribs.common.net.HttpApi
import com.sribs.common.server.IDatabaseService
import com.sribs.common.utils.TimeUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.sql.Date
import java.util.*

class ProjectFloorDetailPresent : BasePresenter(), IProjectContrast.IProjectFloorDetailPresent {

    private var mView: IProjectContrast.IProjectFloorDetailView? = null

    var list = arrayListOf<BuildingFloorItem>()

    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB)
            .navigation() as IDatabaseService
    }

    override fun getRemoteModule(mLocalProjectId: Long, mBuildingId: Long) {

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


    }

    override fun getLocalModule(mLocalProjectId: Long, mBuildingId: Long) {

        mDb.getv3BuildingModule(mLocalProjectId, mBuildingId.toString())
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
                mDb.deletev3ModuleFloor(projectId,buildingId,moduleId )
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
             //   cb(it)
                onResult()
                       LogUtils.d("llf删除数据成功")
            },{
              //  cb(false)
                it.printStackTrace()
            })
        )

    }


    fun createOrSaveModule(
        projectId: Long?,
        buildingId: Long?,
        remoteId: String?,
        moduleName: String?,
        onResult: (Long) -> Unit
    ) {

        LogUtils.d("llf无网络 单本地创建")

        createNewLocalModule(projectId, buildingId, remoteId, moduleName, onResult)
    }


    fun createNewLocalModule(
        projectId: Long?,
        buildingId: Long?,
        remoteId: String?,
        moduleName: String?,
        onResult: (Long) -> Unit
    ) {
        var b = v3BuildingModuleDbBean()
        b.projectId = projectId
        b.buildingId = buildingId.toString()
        b.remoteId = remoteId
        b.moduleName = moduleName
        b.drawings = arrayListOf("1", "2")
        b.inspectors = arrayListOf("1", "2")
        b.leaderId = "21"
        b.leaderName = "21"
        b.updateTime = TimeUtil.YMD_HMS.format(Date())

        addDisposable(
            mDb.updatev3BuildingModule(b)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onResult(it)
                }, {
                    it.printStackTrace()
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