package com.sribs.bdd.module.building

import android.content.Context
import com.cbj.sdk.libui.mvp.moudles.IBasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.bean.Building
import com.sribs.bdd.bean.BuildingMainBean
import com.sribs.bdd.bean.Floor
import com.sribs.bdd.bean.data.ProjectConfigBean
import com.sribs.common.bean.db.*
import com.sribs.db.project.building.BuildingBean

/**
 * @date 2021/7/13
 * @author elijah
 * @Description
 */
interface IBuildingContrast {
    interface IView: IBaseView {
        fun getContext(): Context?
    }

    interface IBuildingView:IBaseView {
        fun getContext(): Context?
//        fun onFloorChanged(bean:com.sribs.bdd.bean.Floor)
        fun initBuildingFloors(bldId: Long, floors: ArrayList<Floor>)
        fun initBuildingDrawing(drawings: ArrayList<DrawingBean>)
        fun initDrawingDamages(damages: List<DamageBean>)
        fun saveDamageDataToDbStarted()
        fun updateLocalDamageDetail(dmg:com.sribs.common.bean.db.DamageBean?)
        fun onRemoveDamageInDrawing(dmg:com.sribs.common.bean.db.DamageBean)
    }

    interface IBuildingPresenter:IBasePresenter {
        fun getLocalBuilding(projectId: Long)
        fun createLocalBuilding(proId:Long, bb: Building)
        fun getLocalDrawingListInBuilding(proId:Long, bldId: Long)
        fun getLocalDamageDetail(dmg:com.sribs.common.bean.db.DamageBean?)
        fun removeDamageInDrawing(dmg:com.sribs.common.bean.db.DamageBean)
    }


    interface IDrawWhiteView:IView{


    }


    interface IDrawWhitePresent:IBasePresenter{

    }

    interface IBuildingReviewView:IView{

    }
    interface IBuildingReviewPresent:IBasePresenter{

    }

    /**
     * 楼栋
     */
    interface IBuildingListPresent:IBasePresenter{

        fun getAllBuilding(localProject:Long,projectUUID:String)
    }

    interface IBuildingListView:IView{

        fun onAllBuilding(l:List<BuildingMainBean>)

        fun onAllRemoteBuilding(l:List<BuildingMainBean>)
    }


}