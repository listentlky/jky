package com.sribs.bdd.v3.ui.floor

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.inflate
import com.sribs.bdd.databinding.ActivityFloorBinding


@Route(path = com.sribs.common.ARouterPath.FLOOR_ACTIVITY)
class FloorActivity : BaseActivity() {

    private val mBinding: ActivityFloorBinding by inflate()


    override fun deinitView() {

    }

    override fun getView(): View {
        return mBinding.root
    }

    override fun initView() {

    }

}