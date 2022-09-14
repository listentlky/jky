package com.sribs.bdd.module.main

import com.cbj.sdk.libui.mvp.moudles.IBasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView

/**
 * @date 2021/8/19
 * @author elijah
 * @Description
 */
interface ISettingContrast {
    interface IView:IBaseView{
        fun onVersion(v:String)
    }
    interface IPresenter:IBasePresenter{
        fun getVersion()
    }
}