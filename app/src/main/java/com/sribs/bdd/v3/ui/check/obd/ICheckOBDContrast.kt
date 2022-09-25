package com.sribs.bdd.v3.ui.check.obd

import com.cbj.sdk.libui.mvp.moudles.IBasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.v3.bean.CheckOBDMainBean
import com.sribs.common.bean.db.DrawingV3Bean

interface ICheckOBDContrast {

    interface ICheckOBDPresenter:IBasePresenter{

        fun getModuleInfo(localProjectId:Long,localBuildingId:Long,localModuleId:Long)

        fun saveDamageToDb(drawingV3Bean:List<DrawingV3Bean>, id:Long)
    }

    interface ICheckOBDView:IBaseView{

        fun onModuleInfo(checkOBDMainBean: List<CheckOBDMainBean>)

    }
}