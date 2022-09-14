package com.sribs.bdd.module.project

import android.content.Context
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.module.BaseUnitConfigPresenter
import com.sribs.common.bean.HistoryBean
import com.sribs.common.bean.db.ConfigBean
import java.sql.Date

class ProjectFloorPresenter: BaseUnitConfigPresenter(),IProjectContrast.IProjectFloorPresenter {

    private var mView: IProjectContrast.IProjectFloorView?=null


    override fun getAllUnit(projectId: Long) {
        TODO("Not yet implemented")
    }

    override fun uploadUnit(projectId: Long, unitId: Long, unitNo: String, willCover: Boolean) {
        TODO("Not yet implemented")
    }

    override fun unitGetConfigHistory(
        projectId: Long,
        unitId: Long?,
        unitNo: String,
        history: (Array<HistoryBean>, String) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun unitGetRecordHistory(
        projectId: Long,
        unitId: Long?,
        unitNo: String,
        history: (Array<HistoryBean>, String) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun unitDownloadConfig(
        historyUnitId: String,
        projectId: Long,
        bldId: Long,
        unitId: Long?,
        cb: (Long) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun unitDownloadRecord(
        historyUnitId: String,
        projectId: Long,
        unitId: Long?,
        cb: (Long) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun bindView(v: IBaseView) {
        mView = v as IProjectContrast.IProjectFloorView
    }

    override fun unbindView() {
        mView = null
    }

    fun initRecycleView(){

    }

    override fun ConfigBean(
        projectId: Long,
        bldId: Long,
        unitId: Long,
        configId: Long,
        floorIdx: Int?,
        configType: Int?,
        floorNum: String,
        createTime: Date?,
        updateTime: Date?
    ): ConfigBean {
        TODO("Not yet implemented")
    }

    override fun getContext(): Context {
        TODO("Not yet implemented")
    }
}