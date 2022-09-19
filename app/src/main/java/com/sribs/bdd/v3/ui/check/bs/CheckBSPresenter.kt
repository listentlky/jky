package com.sribs.bdd.v3.ui.check.bs

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.v3.bean.CheckBSMainBean
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.module.BasePresenter
import com.sribs.common.server.IDatabaseService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * create time: 2022/9/19
 * author: bruce
 * description:
 */
class CheckBSPresenter : BasePresenter(),ICheckBSContrast.ICheckBSPresenter{

    private var mView: ICheckBSContrast.ICheckBSView? = null

    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB)
            .navigation() as IDatabaseService
    }

    override fun getModuleInfo(localProjectId:Long,localBldId:Long,remoteId:String?) {
        LogUtils.d("getModuleInfo ${localProjectId}  ${localBldId}   ${remoteId}")
        addDisposable(mDb.getLocalDrawingListInBuilding(localProjectId,localBldId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                       var list = ArrayList(it.map { b->CheckBSMainBean(
                           projectId = b.projectId,
                           bldId = b.bldId,
                           floorName = b.floorName,
                           remoteId = b.remoteId,
                           fileName = b.fileName,
                           drawingType = b.drawingType,
                           fileType = b.fileType,
                           localAbsPath = b.localAbsPath,
                           remoteAbsPath = b.remoteAbsPath,
                           version = b.version,
                           status = b.status
                       )
                       })
                LogUtils.d("获取到该楼下所有图纸 "+list.toString())
                mView?.onModuleInfo(list)
            },{
                LogUtils.d("获取到该楼下所有图纸失败 ${it}")
            }))

    }

    override fun bindView(v: IBaseView) {
        mView = v as ICheckBSContrast.ICheckBSView
    }

    override fun unbindView() {
       dispose()
        mView = null
    }
}