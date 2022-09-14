package com.sribs.bdd.module.main

import android.content.Context
import com.cbj.sdk.libui.mvp.moudles.IBaseListView
import com.cbj.sdk.libui.mvp.moudles.IBasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.bean.MainProjectBean
import com.sribs.common.bean.HistoryBean
import com.sribs.common.bean.db.DrawingBean
import com.sribs.common.bean.db.UnitBean
import com.sribs.common.bean.net.ProjectSummaryRes

/**
 * @date 2021/6/24
 * @author elijah
 * @Description
 */
interface IMainListContrast {
    interface IView:IBaseListView{
        fun getContext():Context?
        fun onProjectList(l:ArrayList<MainProjectBean>)
        fun onAllUnitsInProject(r:ArrayList<UnitBean>)
        fun onAllDrawingsInProject(r:ArrayList<DrawingBean>)
    }
    interface IPresenter:IBasePresenter{
        fun getProjectList()
        fun getAllUnitsInProject(pjtId: Long)
        fun getAllDrawingInProject(pjtId: Long)

    }

    interface IMainView:IBaseView{
        fun getContext():Context
    }

    interface IMainPresenter:IBasePresenter{
        fun projectGetConfigHistory(remoteProjectId:String, cb:(ArrayList<HistoryBean>?)->Unit)
        fun projectGetRecordHistory(remoteProjectId:String, cb:(ArrayList<HistoryBean>?)->Unit)
        fun projectDownLoadConfig(historyId:String, bean:MainProjectBean, cb:(Boolean)->Unit)
        fun projectDownLoadRecord(configHistoryId:String, recordHistoryId:String, bean:MainProjectBean, cb:(Boolean)->Unit)
        fun projectGetSummary(remoteProjectId: String,cb:(ProjectSummaryRes)->Unit)
        fun projectGetLocalSummary(projectId:Long,cb:(ProjectSummaryRes)->Unit)
        fun projectDelete(projectId:Long)
    }
}