package com.sribs.bdd.v3.ui.check.nres

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.v3.bean.CheckBSMainBean
import com.sribs.bdd.v3.bean.CheckNResMainBean
import com.sribs.bdd.v3.bean.CheckOBDMainBean
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.db.DrawingV3Bean
import com.sribs.common.module.BasePresenter
import com.sribs.common.server.IDatabaseService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * create time: 2022/11/15
 * author: bruce
 * description:
 */
class CheckNResPresenter : BasePresenter(), ICheckNResContrast.ICheckNonResidentsPresenter {

    private var mView: ICheckNResContrast.ICheckNonResidentsView? = null

    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB)
            .navigation() as IDatabaseService
    }

    override fun getModuleInfo(localProjectId: Long, localBuildingId: Long, localModuleId: Long) {
        LogUtils.d("getModuleInfo ${localProjectId}  ${localBuildingId}   ${localModuleId}")

        addDisposable(
            mDb.getv3BuildingModuleOnce(localProjectId, localBuildingId, localModuleId)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .subscribe({
                    var list = ArrayList(it.map { b ->
                        CheckNResMainBean(
                            id = b.id,
                            projectId = b.projectId,
                            bldId = b.buildingId,
                            moduleId = b.id,
                            drawing = b.drawings,
                            inspectorName = b.inspectors,
                            createTime = b.createTime,
                            updateTime = b.updateTime,
                            deleteTime = b.deleteTime,
                            version = b.version,
                            status = b.status,
                            isChanged = b.isChanged
                        )
                    })
                    LogUtils.d("获取到该模块下基于楼所有数据 " + list.toString())
                    getModuleFloorInfo(localProjectId,localBuildingId,localModuleId,list)
                }, {
                    getModuleFloorInfo(localProjectId,localBuildingId,localModuleId,ArrayList())
                    LogUtils.d("获取到该模块下基于楼数据失败 ${it}")
                })
        )
    }

    override fun getModuleFloorInfo(
        localProjectId: Long,
        localBuildingId: Long,
        localModuleId: Long,
        localList:ArrayList<CheckNResMainBean>
    ) {
        LogUtils.d("getModuleFloorInfo ${localProjectId}  ${localBuildingId}   ${localModuleId}")
        addDisposable(mDb.getv3ModuleFloor(localProjectId,localBuildingId,localModuleId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                var list = ArrayList(it.map { b->
                    CheckNResMainBean(
                    id = b.id,
                    projectId = b.projectId,
                    bldId = b.bldId,
                    moduleId = b.moduleId,
                    floorId = b.floorId,
                    floorName = b.floorName,
                    remoteId = b.remoteId,
                    drawing = b.drawingsList,
                    createTime = b.createTime,
                    updateTime = b.updateTime,
                    deleteTime = b.deleteTime,
                    version = b.version,
                    status = b.status,
                )
                })
                LogUtils.d("获取到该楼下基于楼层数据 "+list.toString())

                list.addAll(localList)
                LogUtils.d("获取到该楼下所有数据 "+list.toString())
                mView?.onModuleInfo(list)
                dispose()
            },{
                dispose()
                mView?.onMsg(it.toString())
                mView?.onModuleInfo(localList)
                LogUtils.d("获取到该楼下所有楼层失败 ${it}")
            }))
    }



    override fun saveDamageToDb(bean: CheckNResMainBean) {

        if(bean.floorName.isNullOrEmpty()){
            addDisposable(mDb.updatev3BuildingModuleDrawing(bean.id!!,bean.drawing!!)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    LogUtils.d("更新图纸损伤信息成功")
                },{
                    LogUtils.d("更新图纸损伤信息失败: "+it)
                }))
        }else{
            addDisposable(mDb.updateModuleFloorDrawing(bean.drawing!!,bean.id!!)
                .flatMap {
                    mDb.updateBuildingModule(bean.moduleId!!,1)
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    LogUtils.d("更新图纸损伤信息成功")
                },{
                    LogUtils.d("更新图纸损伤信息失败: "+it)
                })
            )
        }
    }

    override fun bindView(v: IBaseView) {
        mView = v as ICheckNResContrast.ICheckNonResidentsView
    }

    override fun unbindView() {
        dispose()
        mView = null
    }
}