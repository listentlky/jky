package com.sribs.bdd.module.building

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.R
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.module.BasePresenter
import com.sribs.common.server.IDatabaseService
import com.sribs.common.utils.TimeUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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
        mView?.getContext()?.resources?.getStringArray(R.array.main_project_status)?: emptyArray()
    }

    override fun getAllBuilding(localProject: Long) {
        addDisposable(mDb.getBuildingByProjectId(localProject)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                var list = ArrayList(it.map { b ->
                    com.sribs.bdd.bean.BuildingMainBean(
                        projectId = localProject,
                        bldId = b.id ?: -1,
                        bldName = b.bldName!!,
                        bldType = b.bldType!!,
                        leader = b.leader,
                        inspectorName = b.inspectorName,
                        remoteId = null,
                        createTime = if (b.createTime==null)"" else TimeUtil.YMD_HMS.format(b.createTime),
                        updateTime = if (b.updateTime==null)"" else TimeUtil.YMD_HMS.format(b.updateTime),
                        version = b.version!!,
                        status = mStateArr[b.status?:0]
                    )
                })
                LogUtils.d("获取本地数据库楼表: " + list.toString())
                mView!!.onAllBuilding(list)
            })
    }

    override fun bindView(v: IBaseView) {
        mView = v as IBuildingContrast.IBuildingListView
    }

    override fun unbindView() {
        dispose()
        mView = null
    }
}