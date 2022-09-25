package com.sribs.bdd.v3.ui.check.obd

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.v3.bean.CheckOBDMainBean
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.db.DrawingV3Bean
import com.sribs.common.module.BasePresenter
import com.sribs.common.server.IDatabaseService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CheckOBDPresenter :BasePresenter(),ICheckOBDContrast.ICheckOBDPresenter{

    private var mView: ICheckOBDContrast.ICheckOBDView? = null

    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB)
            .navigation() as IDatabaseService
    }

    override fun getModuleInfo(localProjectId:Long,localBuildingId:Long,localModuleId:Long) {
        addDisposable(mDb.getv3BuildingModuleOnce(localProjectId,localBuildingId,localModuleId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                var list = ArrayList(it.map { b->CheckOBDMainBean(
                    projectId = b.projectId,
                    bldId = b.buildingId,
                    moduleId = b.id,
                    moduleName = b.moduleName,
                    drawing = b.drawings,
                    inspectorName = b.inspectors,
                    leaderNamr = b.leaderName,
                    createTime = b.createTime,
                    updateTime = b.updateTime,
                    deleteTime = b.deleteTime,
                    version = b.version,
                    status = b.status
                )
                })
                LogUtils.d("获取到该模块下所有数据 "+list.toString())
                mView?.onModuleInfo(list)
            },{
                mView?.onMsg(it.toString())
                LogUtils.d("获取到该模块下数据失败 ${it}")
            }))
    }

    override fun saveDamageToDb(drawingV3Bean: List<DrawingV3Bean>, id: Long) {
        addDisposable(mDb.updatev3BuildingModuleDrawing(id,drawingV3Bean)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LogUtils.d("更新图纸损伤信息成功")
            },{
                LogUtils.d("更新图纸损伤信息失败: "+it)
            }))
    }

    override fun bindView(v: IBaseView) {
        mView = v as ICheckOBDContrast.ICheckOBDView
    }

    override fun unbindView() {
        dispose()
        mView = null
    }
}