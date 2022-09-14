package com.sribs.bdd.module.login

import com.cbj.sdk.libui.mvp.moudles.IBasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView

/**
 * @date 2021/8/4
 * @author elijah
 * @Description
 */
interface ILoginContrast {
    interface IVew:IBaseView{
        fun onLogin()
    }
    interface IPresenter:IBasePresenter{

        fun login(account:String,pwd:String)
    }
}