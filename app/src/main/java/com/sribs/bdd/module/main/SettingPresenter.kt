package com.sribs.bdd.module.main

import com.cbj.sdk.libnet.http.HttpManager
import com.sribs.common.module.BasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.sribs.bdd.Config
import com.sribs.common.net.HttpApi

/**
 * @date 2021/8/19
 * @author elijah
 * @Description
 */
class SettingPresenter:BasePresenter(),ISettingContrast.IPresenter {
    private var mView:ISettingContrast.IView?=null

    override fun getVersion() {
        if (!Config.isNetAvailable)return
        HttpManager.instance.getHttpService<HttpApi>()
            .systemVersion()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                checkResult(it)
                mView?.onVersion(it.data!!.version)
            },{
                mView?.onMsg(checkError(it))
            })
    }

    override fun bindView(v: IBaseView) {
        mView = v as ISettingContrast.IView
    }

    override fun unbindView() {
        dispose()
        mView = null
    }
}