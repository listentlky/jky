package com.sribs.bdd.v3.ui.check.cd

import com.cbj.sdk.libui.mvp.moudles.IBasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.v3.bean.CheckCDMainBean

/**
 * create time: 2022/9/19
 * author: bruce
 * description:
 */
interface ICheckCDContrast {

    interface ICheckCDPresenter: IBasePresenter {
        fun getModuleInfo(localProjectId:Long,localBldId:Long,localModuleId:Long,remoteId:String?)

        fun saveDamageToDb(bean: CheckCDMainBean)

    }

    interface ICheckCDView: IBaseView{
        fun onModuleInfo(checkMainBean: List<CheckCDMainBean>)
    }
}