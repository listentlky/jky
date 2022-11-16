package com.sribs.bdd.v3.ui.check.nres

import com.cbj.sdk.libui.mvp.moudles.IBasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.v3.bean.CheckNResMainBean
import com.sribs.bdd.v3.bean.CheckOBDMainBean
import com.sribs.common.bean.db.DrawingV3Bean

/**
 * create time: 2022/11/15
 * author: bruce
 * description:
 */
interface ICheckNResContrast {

    interface ICheckNonResidentsPresenter:IBasePresenter{

        fun getModuleInfo(localProjectId:Long,localBuildingId:Long,localModuleId:Long)

        fun getModuleFloorInfo(localProjectId:Long,localBuildingId:Long,localModuleId:Long,localList:ArrayList<CheckNResMainBean>)

        fun saveDamageToDb(bean: CheckNResMainBean)
    }

    interface ICheckNonResidentsView: IBaseView {

        fun onModuleInfo(checkNResMainBean: List<CheckNResMainBean>)

    }
}