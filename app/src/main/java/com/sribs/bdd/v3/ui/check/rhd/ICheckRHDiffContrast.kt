package com.sribs.bdd.v3.ui.check.rhd

import com.cbj.sdk.libui.mvp.moudles.IBasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.v3.bean.CheckHDiffMainBean
import com.sribs.common.bean.db.DrawingV3Bean

/**
 * create time: 2022/9/26
 * author: bruce
 * description:
 */
interface ICheckRHDiffContrast {

    interface ICheckRHDiffPresenter : IBasePresenter {

        fun getModuleInfo(localProjectId:Long,localBuildingId:Long,localModuleId:Long)

        fun saveDamageToDb(drawingV3Bean:List<DrawingV3Bean>, id:Long)
    }

    interface ICheckRHDiffView: IBaseView {

        fun onModuleInfo(checkHDiffMainBean: List<CheckHDiffMainBean>)

    }

}