package com.sribs.bdd.module.project

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.cbj.sdk.libui.mvp.moudles.IBasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.bean.Building
import com.sribs.bdd.bean.BuildingFloorBean
import com.sribs.bdd.bean.BuildingFloorItem
import com.sribs.bdd.bean.data.ModuleFloorBean
import com.sribs.bdd.bean.data.ProjectConfigBean
import com.sribs.common.bean.HistoryBean
import com.sribs.common.bean.db.ConfigBean
import com.sribs.common.bean.db.ProjectBean
import com.sribs.common.bean.db.UnitBean
import com.sribs.common.bean.v3.v3ModuleFloorDbBean

/**
 * @date 2021/7/13
 * @author elijah
 * @Description
 */
interface IProjectContrast {
    interface IView: IBaseView {
        fun getContext(): Context?
    }
    interface IProjectPresenter: IBasePresenter {
        fun getLocalProjectInfo(projectId:Long,res:(ProjectBean)->Unit)
        fun createLocalProject(projectId: Long?,name:String, leader:String, buildNo:String, res:(Long)->Unit)
        fun delLocalProject(projectId: Long)
    }

    interface IUnitView:IBaseView {
        fun getContext(): Context?
        fun onLocalUnit(l:List<UnitBean>)
        fun onLocalConfig(l:List<ConfigBean>)
        fun onUnitUpdate(b:Boolean)
    }

    interface IUnitPresenter:IBasePresenter {
        fun getLocalUnit(unitId: Long)
        fun getLocalUnit(projectId: Long,res:(List<UnitBean>)->Unit)
        fun createLocalUnit(projectId: Long,bldId: Long, unitNo:String?,res:(Long)->Unit)
        fun updateLocalUnit(unitNo: String?,floorSize:Int?,neighborSize:Int?,floorType:Int?)
        fun delLocalUnit(unitId:Long)
        fun getUnitConfig(unitId:Long)
        fun copyUnit(unitId:Long,res:(Long)->Unit)
        fun uploadUnit(unitId:Long, bCover:Boolean)
    }

    interface IConfigView:IBaseView{
        fun onLocalConfig(l:List<ConfigBean>)
        fun getContext(): Context?
        fun onCopyError(msg:String)
    }

    interface IConfigPresenter:IBasePresenter{
        fun getLocalConfig(configId:Long)
        fun updateLocalConfig(b:ConfigBean)
        fun checkConfig(desList:List<ProjectConfigBean>, cb:(Boolean, String?)->Unit)
        fun copyRoom(srcConfigId: Long,desList:List<ProjectConfigBean>)
        fun copyFloor(src: ProjectConfigBean,desList:List<Pair<ProjectConfigBean,List<ProjectConfigBean>>>)
        fun copyUnit(oldUnitId: Long,newUnitId:Long)
    }

    interface IFloorPresenter:IBasePresenter{
        fun createLocalBuilding(proId: Long, bb: Building):Long?
//        fun updateLocalConfig(b:ConfigBean)
    }


    interface IProjectCreateTypePresenter:IBasePresenter{
        fun addAboveFlourList(num:Int)
        fun addAfterFlourList(num: Int)
    }

    interface IProjectCreateTypeView:IView{
        fun getFlourRecycleView():RecyclerView
        fun getPicRecycleView():RecyclerView
        fun chosePic(bean: BuildingFloorBean)
        fun takePhone(bean: BuildingFloorBean)
        fun choseWhite(bean: BuildingFloorBean)
        fun createBuildingSuccess()
    }


    interface IModuleCreateTypeView:IView{
        fun initLocalData(beanList:List<v3ModuleFloorDbBean> )
        fun getFloorRecycleView():RecyclerView
        fun chosePic(bean: ModuleFloorBean)
        fun takePhoto(bean: ModuleFloorBean)
        fun choseWhite(bean: ModuleFloorBean)
        fun createModuleConfigSuccess()
    }


    interface IModuleCreateTypeBuildingView:IView{
        fun initLocalData(beanList:List<v3ModuleFloorDbBean> )
        fun getPicRecycleView():RecyclerView
        fun createModuleConfigSuccess()
    }

    interface IProjectFloorPresenter:IBasePresenter{
        fun getAllUnit(projectId:Long)
        fun uploadUnit(projectId: Long,unitId:Long,unitNo:String,willCover:Boolean)
        fun unitGetConfigHistory(projectId: Long, unitId:Long?, unitNo: String, history:(Array<HistoryBean>, String)->Unit)
        fun unitGetRecordHistory(projectId: Long, unitId:Long?, unitNo: String, history:(Array<HistoryBean>, String)->Unit)
        fun unitDownloadConfig(historyUnitId: String, projectId: Long, bldId:Long, unitId: Long?,cb:(Long)->Unit)
        fun unitDownloadRecord(historyUnitId: String, projectId: Long, unitId: Long?,cb:(Long)->Unit)
    }

    interface IProjectFloorView:IView{
        fun getFlourRecycleView():RecyclerView
        fun onAllUnit(l:List<UnitBean>)
        fun onUpdate(b:Boolean)

    }


    interface IProjectFloorDetailPresent:IBasePresenter{
       fun getRemoteModule(mLocalProjectId:Long, mBuildingId:Long)
       fun getLocalModule(mLocalProjectId:Long, mBuildingId:Long)
    }

    interface IProjectFloorDetailView:IView{
        fun handlItemList(list:ArrayList<BuildingFloorItem>)
        fun addItem(bean:BuildingFloorItem)
        fun removeItem(bean:BuildingFloorItem)
    }
}