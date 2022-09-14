package com.sribs.common.module

import com.cbj.sdk.libui.mvp.moudles.BasePresenter
import com.sribs.common.utils.SpUtil

/**
 * @date 2021/9/15
 * @author elijah
 * @Description
 */
open class BasePresenter:BasePresenter() {

    override fun onTokenExpired() {
        SpUtil.saveUser(SpUtil.getDefaultContext(),"","")
        super.onTokenExpired()
    }
}