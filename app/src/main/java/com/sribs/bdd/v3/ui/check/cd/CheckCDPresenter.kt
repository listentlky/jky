package com.sribs.bdd.v3.ui.check.cd

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.v3.bean.CheckBSMainBean
import com.sribs.bdd.v3.bean.CheckCDMainBean
import com.sribs.bdd.v3.ui.check.bs.ICheckBSContrast
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.db.DamageV3Bean
import com.sribs.common.bean.db.DrawingV3Bean
import com.sribs.common.module.BasePresenter
import com.sribs.common.server.IDatabaseService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * create time: 2022/9/19
 * author: bruce
 * description:
 */
class CheckCDPresenter : BasePresenter(),ICheckCDContrast.ICheckCDPresenter{

    private var mView: ICheckCDContrast.ICheckCDView? = null

    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB)
            .navigation() as IDatabaseService
    }

    override fun getModuleInfo(localProjectId:Long,localBldId:Long,localModuleId:Long,remoteId:String?) {
        LogUtils.d("getModuleInfo ${localProjectId}  ${localBldId}   ${remoteId}")

        addDisposable(mDb.getv3ModuleFloor(localProjectId,localBldId,localModuleId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                var list = ArrayList(it.map { b->
                    CheckCDMainBean(
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
                    status = b.status
                )
                })
                LogUtils.d("获取到该楼下所有楼层数据 "+list.toString())
                mView?.onModuleInfo(list)
            },{
                it.printStackTrace()
             //   mView?.onMsg(it.toString())
                LogUtils.d("获取到该楼下所有楼层失败 ${it}")
            }))




    }

    /**
     * 保存图纸损伤信息
     */
    override fun saveDamageToDb(bean:CheckCDMainBean) {
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

    override fun bindView(v: IBaseView) {
        mView = v as ICheckCDContrast.ICheckCDView
    }

    override fun unbindView() {
       dispose()
        mView = null
    }
}