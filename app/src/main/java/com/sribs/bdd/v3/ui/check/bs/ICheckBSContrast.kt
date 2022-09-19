package com.sribs.bdd.v3.ui.check.bs

import com.cbj.sdk.libui.mvp.moudles.IBasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.v3.bean.CheckBSMainBean

/**
 * create time: 2022/9/19
 * author: bruce
 * description:
 */
interface ICheckBSContrast {

    interface ICheckBSPresenter: IBasePresenter {
        fun getModuleInfo(localProjectId:Long,localBldId:Long,remoteId:String?)
    }

    interface ICheckBSView: IBaseView{
        fun onModuleInfo(checkMainBean: List<CheckBSMainBean>)
    }
}