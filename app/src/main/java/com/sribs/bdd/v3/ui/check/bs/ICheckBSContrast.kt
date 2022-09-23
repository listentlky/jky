package com.sribs.bdd.v3.ui.check.bs

import com.cbj.sdk.libui.mvp.moudles.IBasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.v3.bean.CheckBSMainBean
import com.sribs.common.bean.db.DamageV3Bean

/**
 * create time: 2022/9/19
 * author: bruce
 * description:
 */
interface ICheckBSContrast {

    interface ICheckBSPresenter: IBasePresenter {
        fun getModuleInfo(localProjectId:Long,localBldId:Long,remoteId:String?)

        fun saveDamageToDb(damageInfo:DamageV3Bean)

        fun deleteDamageToDb(damageInfo: DamageV3Bean)
    }

    interface ICheckBSView: IBaseView{
        fun onModuleInfo(checkMainBean: List<CheckBSMainBean>)

        fun onSaveToDbSuccess()
    }
}