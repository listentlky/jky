package com.sribs.bdd.module.project

import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.bean.BuildingFloorItem
import com.sribs.common.utils.TimeUtil
import java.sql.Date
import java.util.*

class ProjectFloorDetailPresent:IProjectContrast.IProjectFloorDetailPresent {

    private var  mView: IProjectContrast.IProjectFloorDetailView?=null

    var list = arrayListOf<BuildingFloorItem>()

    override fun getData(mLocalProjectId:Long,mBuildingId:Long){
        list.clear()
        list.add(BuildingFloorItem(mLocalProjectId,mBuildingId,1,TimeUtil.YMD_HMS.format(Date())))
        list.add(BuildingFloorItem(mLocalProjectId,mBuildingId,2,TimeUtil.YMD_HMS.format(Date())))
        list.add(BuildingFloorItem(mLocalProjectId,mBuildingId,3,TimeUtil.YMD_HMS.format(Date())))
        list.add(BuildingFloorItem(mLocalProjectId,mBuildingId,4,TimeUtil.YMD_HMS.format(Date())))
        list.add(BuildingFloorItem(mLocalProjectId,mBuildingId,5,TimeUtil.YMD_HMS.format(Date())))
        list.add(BuildingFloorItem(mLocalProjectId,mBuildingId,6,TimeUtil.YMD_HMS.format(Date())))
        mView?.handlItemList(list)

    }

    override fun bindView(v: IBaseView) {
        mView = v as IProjectContrast.IProjectFloorDetailView
    }

    override fun unbindView() {
        mView = null
    }
}