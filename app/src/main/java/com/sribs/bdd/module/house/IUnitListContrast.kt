package com.sribs.bdd.module.house

import android.content.Context
import com.cbj.sdk.libui.mvp.moudles.IBasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.common.bean.HistoryBean
import com.sribs.common.bean.db.UnitBean

interface IUnitListContrast {
    interface IView:IBaseView{
        fun getContext(): Context
        fun onAllUnit(l:List<UnitBean>)
        fun onUpdate(b:Boolean)
    }

    interface IPresenter:IBasePresenter{
        fun getAllUnit(projectId:Long)
        fun uploadUnit(projectId: Long,unitId:Long,unitNo:String,willCover:Boolean)
        fun unitGetConfigHistory(projectId: Long, unitId:Long?, unitNo: String, history:(Array<HistoryBean>, String)->Unit)
        fun unitGetRecordHistory(projectId: Long, unitId:Long?, unitNo: String, history:(Array<HistoryBean>, String)->Unit)
        fun unitDownloadConfig(historyUnitId: String, projectId: Long, bldId:Long, unitId: Long?,cb:(Long)->Unit)
        fun unitDownloadRecord(historyUnitId: String, projectId: Long, unitId: Long?,cb:(Long)->Unit)

    }
}